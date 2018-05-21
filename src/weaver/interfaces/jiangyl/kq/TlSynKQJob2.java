package weaver.interfaces.jiangyl.kq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;
import weaver.interfaces.schedule.BaseCronJob;

public class TlSynKQJob2 extends BaseCronJob {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static BaseBean bean = new BaseBean();
    //考勤汇总表
    public String kqhztable = bean.getPropValue("TLConn", "kqhztable");
    //考勤记录表
    public String kqtable = bean.getPropValue("TLConn", "kqtable");
    //正式卡卡片标识
    public String zscardflag = bean.getPropValue("TLConn", "synzscard");
    //临时卡前是否需要加入标识
    public String lskbs = bean.getPropValue("TLConn", "lskbs");
    //临时卡前加入的标识内容
    public String lskflag = bean.getPropValue("TLConn", "lskflag");
    //一般时间对应的表
    public String ybsjtable = bean.getPropValue("TLConn", "ybsjtable");
    //未刷卡流程对应workflowid
    public String wdkflow = bean.getPropValue("TLConn", "wdkflow");

    /**
     * 执行
     */
    public void execute() {
        syn();
    }

    /**
     * 同步
     */
    public void syn() {
        String bdate = bean.getPropValue("TLConn", "syndate");
        bean.writeLog("bdate:"+bdate);
        //先获取数据库最新的数据内容
        String getLastDate = "select * from "+kqhztable+" where signtime is not null order by signdate desc";
        RecordSet rs = new RecordSet();
        rs.execute(getLastDate);
        rs.next();
        String signdate = rs.getString("signdate");
        if (null == signdate || "".equals(signdate)) {
            signdate = bdate;
        }

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date lastdate = null;
        try {
            lastdate = s.parse(signdate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        //Date lastdate = getLastDate();
        lastdate = getIncomeDate2(lastdate,1);
//        Date nowdate = null;
//        try {
//            nowdate = format.parse(format.format(new Date()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(getURL(), getUserName(),
                    getPassword());
        } catch (ClassNotFoundException e) {
            bean.writeLog("连接门禁数据库驱动类没有找到，请查看对应的jar是否已经导入.");
            e.printStackTrace();
        } catch (SQLException e) {
            bean.writeLog("连接门禁数据失败，请检查对应的连接数据库字符串、用户名、密码是否正确.");
            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rss = null;
        String now = "";
        try {
            st = conn.createStatement();
            rss = st.executeQuery("select max(sign_time) as sign_time from TimeRecords");
            rss.next();
            now = rss.getString("sign_time");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (null == now || "".equals(now)) {
            bean.writeLog("获取门禁数据库最新时间为空.");
            return;
        }
        Date nowdate = null;
        try {
            nowdate = s.parse(now.split(" ")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while(lastdate.getTime() <= nowdate.getTime()) {
            synDay(format.format(lastdate));
            lastdate = getIncomeDate2(lastdate,1);
        }
    }

    public void synDay(String date){
        String beginDate = date;
        bean.writeLog("同步考勤数据日期:"+date);
        // 检查开始时间，如2015-12-12 00:00:00
        String sbeginDate = beginDate + " 00:00:01";
        // 检查结束时间，如2015-12-12 23:59:59
        String searchEndDate = beginDate + " 23:59:59";
        // 判断是否是工作时间
        if (isHoliday(beginDate)) {
            bean.writeLog("时间【" + beginDate + "】为假日，无需同步数据。");
            return;
        }
        // 定义装载获取时间段内的数据，根据emp_id区分，一条emp_id为一个单位
        Map<String, TlHrmMode> map = new HashMap<String, TlHrmMode>();
        // 获取门禁数据连接，查询对应开始时间和结束时间内的数据
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(getURL(), getUserName(),
                    getPassword());
        } catch (ClassNotFoundException e) {
            bean.writeLog("连接门禁数据库驱动类没有找到，请查看对应的jar是否已经导入.");
            e.printStackTrace();
        } catch (SQLException e) {
            bean.writeLog("连接门禁数据失败，请检查对应的连接数据库字符串、用户名、密码是否正确.");
            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select a.emp_id,a.sign_time,a.card_id,b.clock_name from TimeRecords a, Clocks b where a.clock_id = b.clock_id and a.sign_time between '"
                    + sbeginDate
                    + "' and '"
                    + searchEndDate
                    + "' order by a.sign_time asc");
            // 把查询出来的数据装载到map中，以emp_id为key，以tl为value
            // 获取正式卡标识
            while (rs.next()) {
                String emp_id = rs.getString("emp_id");
                String card_id = rs.getString("card_id");
                String sign_time = rs.getString("sign_time");
                String door_name = rs.getString("clock_name");
                if (null != emp_id && emp_id.startsWith(zscardflag)) {
                    if (emp_id.contains("(")) {
                        emp_id = emp_id.split("\\(")[0];
                    }
                    if (emp_id.contains("（")) {
                        emp_id = emp_id.split("\\（")[0];
                    }
                    if (map.containsKey(emp_id)) {
                        String currentDate = map.get(emp_id).getMutil_date();
                        currentDate = currentDate + "," + sign_time;
                        map.get(emp_id).setMutil_date(currentDate);
                        String doorname = map.get(emp_id).getDoor();
                        doorname = doorname + "," + door_name;
                        map.get(emp_id).setDoor(doorname);
                    } else {
                        TlHrmMode tl = new TlHrmMode();
                        tl.setCardid(card_id);
                        tl.setMutil_date(sign_time);
                        tl.setObjno(emp_id);
                        tl.setSigndate(beginDate);
                        tl.setDoor(door_name);
                        String empid = getHrmid(emp_id);
                        if ("".equals(empid)) {
                            continue;
                        }
                        tl.setHrmid(empid);
                        tl.setSfdk("带卡");
                        map.put(emp_id, tl);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 定义容器装载待插入的数据
        List<TlHrmMode> list = new ArrayList<TlHrmMode>();
        // 遍历map,把正常上下班数据装载到list中
        for (Entry<String, TlHrmMode> entry : map.entrySet()) {
            TlHrmMode tl = entry.getValue();
            List<TlHrmMode> t = generateTlHrmModeInfo(tl);
            list.addAll(t);
        }
        bean.writeLog(date + " 获取考勤数据：" + list.toString());
        // 获取归档（未带卡）流程数据，获取sqr，转换成emp_id即OA编号loginid，申请日期，临时卡卡号
//        SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String now = forma.format(new Date());
//        String nowDate = now.split(" ")[0];
        String finishedSQL = getFinishedSQL(beginDate, "00:00:00", "23:59:59");
        RecordSet recordset = new RecordSet();
        recordset.execute(finishedSQL);
        Connection conn1 = null;
        Statement st1 = null;
        ResultSet rs1 = null;
        while (recordset.next()) {
            String creater = recordset.getString("creater");// 创建者
            String date1 = recordset.getString("date1");// 创建者
            String empid = getEmpId(creater);
            String lskh = recordset.getString("desc11");// 临时卡卡号
            //TODO 临时卡号前面是否需要加入00
//            if (Boolean.valueOf(lskbs)) {
//                lskh = lskflag + lskh;
//            }
//            if (lskh.length() == 4) {
//                lskh = "00" + lskh;
//            }
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conn1 = DriverManager.getConnection(getURL(), getUserName(),
                        getPassword());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                st1 = conn1.createStatement();
                rs1 = st1
                        .executeQuery("select a.emp_id,a.sign_time,a.card_id,b.clock_name from TimeRecords a, Clocks b where a.clock_id = b.clock_id and a.sign_time between '"
                                + date1
                                + " 00:00:01"
                                + "' and '"
                                + date1
                                + " 23:59:59"
                                + "' and a.emp_id = '"
                                + lskh
                                + "' order by a.sign_time asc");
                String signtime = "";
                String doorname = "";
                while (rs1.next()) {
                    String time = rs1.getString("sign_time");
                    signtime = signtime + time + ",";
                    String door = rs1.getString("clock_name");
                    if ("".equals(doorname)) {
                        doorname = door;
                    } else {
                        doorname = doorname + "," + door;
                    }
                }
                if("".equals(signtime)) {
                    bean.writeLog("获取未带卡，日期"+date1+";临时卡卡号"+lskh+"，未获取到刷卡记录.");
                    continue;
                }
                TlHrmMode tmode = new TlHrmMode();
                tmode.setCardid(lskh);
                tmode.setDoor(doorname);
                tmode.setHrmid(creater);
                tmode.setMutil_date(signtime.substring(0, signtime.length() - 1));
                tmode.setObjno(empid);
                tmode.setSfdk("未带卡");
                tmode.setSigndate(date1);
                list.addAll(generateTlHrmModeInfo(tmode));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs1.close();
                    st1.close();
                    conn1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // 把处理完的数据装载到考勤表中
        RecordSet set = new RecordSet();
        bean.writeLog("获取考勤数据为：" + list.toString());
        if (list.isEmpty()) {
            return;
        }
        for (TlHrmMode tlmode : list) {
            String insertSQL = mosicSQL(tlmode);
            bean.writeLog("插入到考勤表SQL：" + insertSQL);
            set.execute(insertSQL);
        }

        // 算出来正式和临时卡数据中所有的所属类型
        List<TlHrmMode> allList = new ArrayList<TlHrmMode>();
        for (TlHrmMode tlmode : list) {
            List<TlHrmMode> all = compareTime(tlmode);
            allList.addAll(all);
        }

        // 把获取完成的数据插入考勤汇总表中
        for (TlHrmMode tlmode : allList) {
            String insertSQL = mosicHZBSQL(tlmode);
            bean.writeLog("插入到考勤汇总表SQL：" + insertSQL);
            set.execute(insertSQL);
        }

        //处理公出记录
        String getGCSQL = getGC(beginDate,"00:00:00","23:59:59");
        bean.writeLog(date + " 获取" + beginDate + " 公出归档流程SQL："+ getGCSQL);
        RecordSet rsset = new RecordSet();
        rsset.execute(getGCSQL);
        while (rsset.next()) {
            String sqr = rsset.getString("creater");
            String datejl = rsset.getString("date1");
            String time1 = rsset.getString("time1");
            String time2 = rsset.getString("time2");
            bean.writeLog("creater:"+sqr + ";date:"+datejl+";没有公出结束时间");
            if(null == time2 || "".equals(time2)) {
                time2 = "20:00";
            }
            dealExist(sqr,datejl,time1,time2,sqr);
        }
        synCC(date);
    }

    /**
     * 同步出差
     *
     * @param date
     */
    public void synCC(String date){
        RecordSet rs = new RecordSet();
        //同步出差
        String getallcxsql = "select t3.shqperson,t3.sjccksrq,t3.sjccjsrq,t3.sjccjssj,t3.cpscsj,a.departmentid from formtable_main_23 t3,hrmresource a where a.id = t3.shqperson and shqperson is not null and sjccksrq is not null and t3.sjccksrq between '"+date+"' and '"+date+"' order by sjccksrq desc";
        bean.writeLog("同步出差流程SQL：" + getallcxsql);
        rs.execute(getallcxsql);
        while (rs.next()) {
            String shqperson = rs.getString("shqperson");
            String sjccksrq = rs.getString("sjccksrq");
            String sjccjsrq = rs.getString("sjccjsrq");
            String sjccjssj = rs.getString("sjccjssj");
            String cpscsj = rs.getString("cpscsj");
            Date da = null;
            try {
                da = new SimpleDateFormat("HH:mm").parse(sjccjssj);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date da1 = null;
            try {
                da1 = new SimpleDateFormat("HH:mm").parse(cpscsj);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String departmentid = rs.getString("departmentid");
            if (sjccksrq.equals(sjccjsrq)) {
                String week = getWeekOfDateNum(sjccksrq);
                TlTimeMode t = getTimeInfo(week, departmentid, sjccksrq);
                Date dat = null;
                try {
                    dat = new SimpleDateFormat("HH:mm").parse(t.getPm_sbsj());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dat1 = null;
                try {
                    dat1 = new SimpleDateFormat("HH:mm").parse(t.getAm_xbsj());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //如果在上午下班之前，下午上班之后
                if (da1.getTime() <= dat1.getTime() && da.getTime() >= dat.getTime()) {
                    String resl = getExist(shqperson,sjccksrq,"0");
                    if (",".equals(resl)) {
                        insertCC(shqperson,sjccksrq,"0");
                    } else {
                        updateCC(shqperson,sjccksrq,"0",resl);
                    }
                    String resl1 = getExist(shqperson,sjccksrq,"1");
                    if (",".equals(resl1)) {
                        insertCC(shqperson,sjccksrq,"1");
                    } else {
                        updateCC(shqperson,sjccksrq,"1",resl1);
                    }
                } else if (da1.getTime() > dat1.getTime() && da.getTime() >= dat.getTime()) {
                    String resl1 = getExist(shqperson,sjccksrq,"1");
                    if (",".equals(resl1)) {
                        insertCC(shqperson,sjccksrq,"1");
                    } else {
                        updateCC(shqperson,sjccksrq,"1",resl1);
                    }
                } else if (da1.getTime() < dat1.getTime() && da.getTime() < dat.getTime()) {
                    String resl = getExist(shqperson,sjccksrq,"0");
                    if (",".equals(resl)) {
                        insertCC(shqperson,sjccksrq,"0");
                    } else {
                        updateCC(shqperson,sjccksrq,"0",resl);
                    }
                }
            } else {
                int count = 0;
                while (getDateWithStr(sjccksrq, "yyyy-MM-dd").before(getDateWithStr(sjccjsrq, "yyyy-MM-dd"))) {
                    String week = getWeekOfDateNum(sjccksrq);
                    TlTimeMode t = getTimeInfo(week, departmentid, sjccksrq);
                    Date dat1 = null;
                    try {
                        dat1 = new SimpleDateFormat("HH:mm").parse(t.getAm_xbsj());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (count == 0 ) {
                        //如果在上午下班之前，下午上班之后
                        if (da1.getTime() <= dat1.getTime()) {
                            String resl = getExist(shqperson,sjccksrq,"0");
                            if (",".equals(resl)) {
                                insertCC(shqperson,sjccksrq,"0");
                            } else {
                                updateCC(shqperson,sjccksrq,"0",resl);
                            }
                            String resl1 = getExist(shqperson,sjccksrq,"1");
                            if (",".equals(resl1)) {
                                insertCC(shqperson,sjccksrq,"1");
                            } else {
                                updateCC(shqperson,sjccksrq,"1",resl1);
                            }
                        } else {
                            String resl1 = getExist(shqperson,sjccksrq,"1");
                            if (",".equals(resl1)) {
                                insertCC(shqperson,sjccksrq,"1");
                            } else {
                                updateCC(shqperson,sjccksrq,"1",resl1);
                            }
                        }
                    } else {
                        String resl = getExist(shqperson,sjccksrq,"0");
                        if (",".equals(resl)) {
                            insertCC(shqperson,sjccksrq,"0");
                        } else {
                            updateCC(shqperson,sjccksrq,"0",resl);
                        }
                        String resl1 = getExist(shqperson,sjccksrq,"1");
                        if (",".equals(resl1)) {
                            insertCC(shqperson,sjccksrq,"1");
                        } else {
                            updateCC(shqperson,sjccksrq,"1",resl1);
                        }
                    }
                    count++;
                    sjccksrq = getIncomeDate3(getDateWithStr(sjccksrq, "yyyy-MM-dd"), 1);
                }
                String week = getWeekOfDateNum(sjccksrq);
                TlTimeMode t = getTimeInfo(week, departmentid, sjccksrq);
                Date dat = null;
                try {
                    dat = new SimpleDateFormat("HH:mm").parse(t.getPm_sbsj());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (da.getTime() <= dat.getTime()) {
                    String resl = getExist(shqperson,sjccksrq,"0");
                    if (",".equals(resl)) {
                        insertCC(shqperson,sjccksrq,"0");
                    } else {
                        updateCC(shqperson,sjccksrq,"0",resl);
                    }
                } else {
                    String resl = getExist(shqperson,sjccksrq,"0");
                    if (",".equals(resl)) {
                        insertCC(shqperson,sjccksrq,"0");
                    } else {
                        updateCC(shqperson,sjccksrq,"0",resl);
                    }
                    String resl1 = getExist(shqperson,sjccksrq,"1");
                    if (",".equals(resl1)) {
                        insertCC(shqperson,sjccksrq,"1");
                    } else {
                        updateCC(shqperson,sjccksrq,"1",resl1);
                    }
                }
            }
        }
    }

    /**
     * 获取后一天时间
     *
     * @param date
     * @param flag
     * @return
     * @throws NullPointerException
     */
    public static String getIncomeDate3(Date date,
                                        int flag) throws NullPointerException {
        if (null == date) {
            throw new NullPointerException("the date is null or empty!");
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH, +flag);

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        return s.format(calendar.getTime());
    }

    /**
     * 获取出差打卡记录
     *
     * @param hrmid
     * @param date
     * @param flag
     * @return
     */
    public String getExist(String hrmid,String date,String flag) {
        StringBuffer sb = new StringBuffer(",");
        RecordSet rs = new RecordSet();
        rs.execute("select id from formtable_main_174 where signdate = '"+date+"' and hrmid = '"+hrmid+"' and signlx = '"+flag+"'");
        while (rs.next()) {
            String id = rs.getString("id");
            sb.append(id);
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * 把字符串时间转换成日期
     *
     * @param date
     * @return
     */
    public Date getDateWithStr(String date,
                               String flag) {
        SimpleDateFormat s = new SimpleDateFormat(flag);
        Date d = null;
        try {
            d = s.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 插入考勤数据
     *
     * @param hrmid
     * @param date
     * @param time
     * @param flag
     */
    public void insertCC (String hrmid,String date,String flag) {
        RecordSet rs = new RecordSet();
        String sql1 = "insert into formtable_main_174 (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " +
                "values('"+ hrmid + "','" + date + "','8','','"+flag+"','" + "" + "','0','0','0','0','0','0','0','0','0','0','0','0','221','" + hrmid + "','0')";
        bean.writeLog("插入" + hrmid + " 上午出差SQL："+sql1);
        rs.execute(sql1);
    }

    /**
     * 更新考勤数据
     *
     * @param hrmid
     * @param date
     * @param time
     * @param flag
     */
    public void updateCC (String hrmid,String date,String flag,String ids) {
        String id = ids.substring(1, ids.length()-1);
        RecordSet rs = new RecordSet();
        String updateSQL = "update formtable_main_174 set kqlx = '8' where id in ("+id+")";
        bean.writeLog("更新" + hrmid + " 出差SQL："+updateSQL);
        rs.execute(updateSQL);
    }

    /**
     * 处理已经存在的考勤记录
     *
     * @param sql
     * @param date
     * @return
     */
    private void dealExist (String sql,String date,String time1,String time2,String sqr) {
        RecordSet rs = new RecordSet();
        RecordSet rs1 = new RecordSet();
        String departmentid = "";
        try {
            departmentid = new ResourceComInfo().getDepartmentID(sqr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String week = getWeekOfDateNum(date);
        TlTimeMode mode = getTimeInfo(week, departmentid, date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date beginTime = null;
        Date endTime = null;
        Date swxbTime = null;
        Date xwsbTime = null;
        try {
            beginTime = format.parse(time1);
            endTime = format.parse(time2);
            swxbTime = format.parse(mode.getAm_xbsj());
            xwsbTime = format.parse(mode.getPm_sbsj());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String flag = "";
        if (beginTime.getTime() <= swxbTime.getTime() && endTime.getTime() >= xwsbTime.getTime()) {
            flag = "3";
        } else if (beginTime.getTime() > swxbTime.getTime() && endTime.getTime() >= xwsbTime.getTime()) {
            flag = "2";
        } else {
            flag = "1";
        }
        if ("1".equals(flag)) {
            rs.execute("select id,kqlx,signlx from " + kqhztable + " where hrmid = '"+sql+"' and signdate = '"+date+"' and signlx = '0'");
            rs.next();
            if (rs.getCounts() >0) {
                String id = rs.getString("id");
                rs1.execute("update " + kqhztable + " set kqlx = '7' where id = '"+id+"'");
            } else {
                String insertAM = "insert into "
                        + kqhztable
                        + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,sfdk,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) values('"
                        + sql + "','" + date + "','7','06:00','0','" + getCardId(sql) + "','0','0','0','0','0','0','带卡','0','0','0','0','0','0','221','" + sql
                        + "','0')";
                bean.writeLog("插入公出上午SQL：" +insertAM);
                rs1.execute(insertAM);
            }
        } else if ("2".equals(flag)) {
            rs.execute("select id,kqlx,signlx from " + kqhztable + " where hrmid = '"+sql+"' and signdate = '"+date+"' and signlx = '1'");
            rs.next();
            if (rs.getCounts() >0) {
                String id = rs.getString("id");
                rs1.execute("update " + kqhztable + " set kqlx = '7' where id = '"+id+"'");
            } else {
                String insertPM = "insert into "
                        + kqhztable
                        + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,sfdk,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) values('"
                        + sql + "','" + date + "','7','20:00','1','" + getCardId(sql) + "','0','0','0','0','0','0','带卡','0','0','0','0','0','0','221','" + sql
                        + "','0')";
                bean.writeLog("插入公出下午SQL：" +insertPM);
                rs1.execute(insertPM);
            }
        } else {
            rs.execute("select id,kqlx,signlx from " + kqhztable + " where hrmid = '"+sql+"' and signdate = '"+date+"' and signlx = '0'");
            rs.next();
            if (rs.getCounts() >0) {
                String id = rs.getString("id");
                rs1.execute("update " + kqhztable + " set kqlx = '7' where id = '"+id+"'");
            } else {
                String insertAM = "insert into "
                        + kqhztable
                        + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,sfdk,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) values('"
                        + sql + "','" + date + "','7','06:00','0','" + getCardId(sql) + "','0','0','0','0','0','0','带卡','0','0','0','0','0','0','221','" + sql
                        + "','0')";
                bean.writeLog("插入公出上午SQL：" +insertAM);
                rs1.execute(insertAM);
            }
            rs.execute("select id,kqlx,signlx from " + kqhztable + " where hrmid = '"+sql+"' and signdate = '"+date+"' and signlx = '1'");
            rs.next();
            if (rs.getCounts() >0) {
                String id = rs.getString("id");
                rs1.execute("update " + kqhztable + " set kqlx = '7' where id = '"+id+"'");
            } else {
                String insertPM = "insert into "
                        + kqhztable
                        + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,sfdk,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) values('"
                        + sql + "','" + date + "','7','20:00','1','" + getCardId(sql) + "','0','0','0','0','0','0','带卡','0','0','0','0','0','0','221','" + sql
                        + "','0')";
                bean.writeLog("插入公出下午SQL：" +insertPM);
                rs1.execute(insertPM);
            }
        }
    }

    /**
     * 获取考勤卡号
     *
     * @param hrmid
     * @return
     */
    public String getCardId (String hrmid) {
        String getCardSQL = "select cardid from " + kqhztable + " where hrmid = '"+hrmid+"'";
        RecordSet rs = new RecordSet();
        rs.execute(getCardSQL);
        rs.next();
        return rs.getString("cardid");
    }

    /**
     * 获取公出记录
     *
     * @param begindate
     * @param enddate
     * @return
     */
    public String getGC(String time, String hour, String hour2) {
        String sql = "select distinct t1.requestid, t1.createdate, t1.createtime,t1.creater,t3.resource1,t3.desc11,t1.lastoperatedate,t1.lastoperatetime,t3.date1,t3.wsklx,t3.time1,t3.time2 "
                + "from workflow_requestbase t1,workflow_currentoperator t2,workflow_form t3 "
                + "where t1.requestid = t2.requestid "
                + "and t1.workflowid in("+wdkflow+") "
                + "and t2.usertype=0 "
                + "and t2.isremark in('2','4') "
                + "and t1.currentnodetype = '3' "
                + "and iscomplete=1 "
                + "and t1.lastoperatedate  = '"
                + time
                + "' and t1.lastoperatetime between '"
                + hour
                + "' and '"
                + hour2
                + "' "
                + "and islasttimes=1 "
                + "and t3.billformid = '103' and t3.wsklx = '0' "
                + "and t3.requestid = t1.requestid ";
        return sql;
    }

    /**
     * 获取数据库中最后一天更新的记录
     *
     * @return
     */
    public Date getLastDate(){
        RecordSet set = new RecordSet();
        set.execute("select * from formtable_main_160 where signtime is not null and rownum = 1 order by signdate");
        set.next();
        String signdate = Util.null2String(set.getString("signdate"));
        if("".equals(signdate)) {
            try {
                return format.parse(format.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        try {
            return format.parse(signdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过比对时间算出来所属类型
     *
     * @param mode
     * @return
     */
    public List<TlHrmMode> compareTime(TlHrmMode mode) {
        List<TlHrmMode> list = new ArrayList<TlHrmMode>();
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        String date = mode.getSigndate();
        String hrmid = mode.getHrmid();
        ResourceComInfo r = null;
        try {
            r = new ResourceComInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String departmentid = r.getDepartmentID(hrmid);
        String number = getWeekOfDateNum(date);
        TlTimeMode timeMode = getTimeInfo(number, departmentid,date);
        // 1 正常打卡，判断时间是否迟到早退
        String signkind = mode.getSignkind();
        if ("上班".equals(signkind)) {
            String signtime = mode.getSigntime();
            try {
                Date sigindate = s.parse(signtime);
                Date standarddate = s.parse(timeMode.getAm_sbsj());
                if (sigindate.before(standarddate) || (sigindate.getTime() == standarddate.getTime())) {
                    mode.setLx("0");
                } else {
                    boolean isnot = judgeTime(standarddate, sigindate,
                            getKQTime());
                    if (isnot) {
                        mode.setLx("18");
                    } else {
                        mode.setLx("20");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            String signtime = mode.getSigntime();
            try {
                Date sigindate = s.parse(signtime);
                Date standarddate = s.parse(timeMode.getPm_xbsj());
                if (sigindate.after(standarddate) || (sigindate.getTime() == sigindate.getTime())) {
                    mode.setLx("0");
                } else {
                    boolean isnot = judgeTime(sigindate, standarddate,
                            getKQTime());
                    if (isnot) {
                        mode.setLx("20");
                    } else {
                        mode.setLx("19");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        list.add(mode);
        return list;
    }

    /**
     * 根据时间判断是否迟到/早退/旷工
     *
     * @param date1
     * @param date2
     * @param min
     * @return
     */
    public static boolean judgeTime(Date date1, Date date2, int min) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date2);

        calendar.add(Calendar.MINUTE, -min);
        Date time = calendar.getTime();
        if (time.before(date1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 拼接插入考勤表数据
     *
     * @param mode
     * @return
     */
    public String mosicHZBSQL(TlHrmMode mode) {
        String tableName = bean.getPropValue("TLConn", "kqhztable");
        String cardid = mode.getCardid();
        String hrmid = mode.getHrmid();
        String signdate = mode.getSigndate();
        String signtime = mode.getSigntime();
        int kind = Integer.parseInt(mode.getLx());
        String signlx = mode.getSignlx();
        String sfdk = mode.getSfdk();
        return "insert into "
                + tableName
                + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,sfdk,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) values('"
                + hrmid + "','" + signdate + "','" + kind + "','" + signtime
                + "','" + signlx + "','" + cardid + "','0','0','0','0','0','0','"+sfdk+"','0','0','0','0','0','0','221','" + hrmid
                + "','0')";
    }

    /**
     * 拼接插入考勤表数据
     *
     * @param mode
     * @return
     */
    public String mosicSQL(TlHrmMode mode) {
        String tableName = bean.getPropValue("TLConn", "kqtable");
        String cardid = mode.getCardid();
        String door = mode.getDoor();
        String empid = mode.getHrmid();
        String signdate = mode.getSigndate();
        String kind = mode.getSignkind();
        String signtime = mode.getSigntime();
        return "insert into "
                + tableName
                + " (OBJNO,SIGNDATE,SIGNKIND,SIGNTIME,CARDID,DOOR,formmodeid,modedatacreater,modedatacreatertype) values('"
                + empid + "','" + signdate + "','" + kind + "','" + signtime
                + "','" + cardid + "','" + door + "','221','" + empid
                + "','0')";
    }

    /**
     * 根据人员id获取loginid
     *
     * @param id
     * @return
     */
    public String getEmpId(String id) {
        RecordSet rs = new RecordSet();
        rs.execute("select loginid from hrmresource where id = '" + id + "'");
        rs.next();
        String loginid = rs.getString("loginid");
        return loginid;
    }

    /**
     * 根据人员id获取loginid
     *
     * @param id
     * @return
     */
    public String getHrmid(String id) {
        if ("".equals(id) || null == id) {
            return "";
        }
        if (id.contains("(")) {
            id = id.split("\\(")[0];
        }
        if (id.contains("（")) {
            id = id.split("\\（")[0];
        }
        RecordSet rs = new RecordSet();
        rs.execute("select id from hrmresource where loginid = '" + id + "'");
        rs.next();
        String loginid = rs.getString("id");
        return loginid;
    }

    /**
     * get url for sqlserver
     *
     * @return
     */
    public static int getKQTime() {
        return Integer.parseInt(bean.getPropValue("TLConn", "kqtime"));
    }

    /**
     * get url for sqlserver
     *
     * @return
     */
    public String getURL() {
        return bean.getPropValue("TLConn", "url");
    }

    /**
     * get username for sqlserver
     *
     * @return
     */
    public String getUserName() {
        return bean.getPropValue("TLConn", "username");
    }

    /**
     * get password for sqlserver
     *
     * @return
     */
    public String getPassword() {
        return bean.getPropValue("TLConn", "password");
    }

    /**
     * get special date
     *
     * @param date
     * @param flag
     * @return
     * @throws NullPointerException
     */
    public static Date getIncomeDate(Date date, int flag)
            throws NullPointerException {
        if (null == date) {
            throw new NullPointerException("the date is null or empty!");
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH, -flag);

        return calendar.getTime();
    }

    /**
     * get special date
     *
     * @param date
     * @param flag
     * @return
     * @throws NullPointerException
     */
    public static Date getIncomeDate2(Date date, int flag)
            throws NullPointerException {
        if (null == date) {
            throw new NullPointerException("the date is null or empty!");
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH, +flag);

        return calendar.getTime();
    }

    /**
     * judge date is holiday or not
     *
     * @param date
     * @return
     */
    public boolean isHoliday(String date) {
        RecordSet rs = new RecordSet();
        rs.execute("select * from HrmPubHoliday where holidaydate = '" + date
                + "'");
        if (rs.getCounts() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDateCh(String date) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
                "星期六" };
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(s.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDateNum(String date) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(s.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysCode[intWeek];
    }

    /**
     * 根据是星期几获取对应的一般时间
     *
     * @param week
     * @return
     */
    public TlTimeMode getTimeInfo(String week, String departmentId,String date) {
        RecordSet rs = new RecordSet();
        rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb from "+ybsjtable+" a, "+ybsjtable+"_dt1 b where a.id = b.mainid and b.bm = '"
                + departmentId + "' and b.xq = '" + week + "' and a.kssxrq <= '"+date+"' and a.jssxrq >= '"+date+"'");
        rs.next();
        String swsb = rs.getString("swsb");
        String swxb = rs.getString("swxb");
        String xwsb = rs.getString("xwsb");
        String xwxb = rs.getString("xwxb");
        TlTimeMode t = new TlTimeMode();
        if ("".equals(swsb) || null == swsb) {
            swsb = "08:30";
        }
        t.setAm_sbsj(swsb);
        if ("".equals(swxb) || null == swxb) {
            swxb = "11:30";
        }
        t.setAm_xbsj(swxb);
        if ("".equals(xwsb) || null == xwsb) {
            xwsb = "12:30";
        }
        t.setPm_sbsj(xwsb);
        if ("".equals(xwxb) || null == xwsb) {
            xwxb = "17:30";
        }
        t.setPm_xbsj(xwxb);
        return t;
    }

    /**
     * 根据一般时间获取TlHrmMode对象
     *
     * @param multi_date
     * @param mode
     * @return
     */
    public List<TlHrmMode> generateTlHrmModeInfoForLS(TlHrmMode hrmmode,
                                                      String empid, String hrmid, String date) {
        List<TlHrmMode> list = new ArrayList<TlHrmMode>();
        SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
        String mutil_date = hrmmode.getMutil_date();
        String[] mutis = mutil_date.split(",");
        long[] mutilss = new long[mutis.length];
        for (int i = 0; i < mutis.length; i++) {
            long time;
            try {
                time = s.parse(mutis[i].split("\\.")[0]).getTime();
                mutilss[i] = time;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Arrays.sort(mutilss);

        hrmmode.setHrmid(hrmid);
        hrmmode.setObjno(empid);
        hrmmode.setLx("21");
        hrmmode.setSigntime(s.format(mutilss[0]));
        hrmmode.setSignkind("上班");
        list.add(hrmmode);
        hrmmode.setSigntime(s.format(mutilss[mutilss.length - 1]));
        hrmmode.setSignkind("下班");
        list.add(hrmmode);
        return list;
    }

    /**
     * 根据一般时间获取TlHrmMode对象
     *
     * @param multi_date
     * @param mode
     * @return
     */
    public static List<TlHrmMode> generateTlHrmModeInfo(TlHrmMode hrmmode) {
        List<TlHrmMode> list = new ArrayList<TlHrmMode>();
        SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
        String mutil_date = hrmmode.getMutil_date();
        String[] mutis = mutil_date.split(",");
        long[] mutilss = new long[mutis.length];
        for (int i = 0; i < mutis.length; i++) {
            long time;
            try {
                time = s.parse(mutis[i].split(" ")[1].split("\\.")[0])
                        .getTime();
                mutilss[i] = time;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Arrays.sort(mutilss);
        if (mutilss.length == 1) {
            String first = String.valueOf(s.format(mutilss[0]));
            hrmmode.setSigntime(first.split(":")[0] + ":" + first.split(":")[1]);
            //try {
            //if (mutilss[0] < s.parse("12:00:00").getTime()) {
            hrmmode.setSignlx("0");
            //} else {
            //hrmmode.setSignlx("1");
            //}
            //} catch (ParseException e) {
            //e.printStackTrace();
            //}
            String doorname = hrmmode.getDoor();
            String[] strs = doorname.split(",");
            Arrays.sort(strs);
            hrmmode.setDoor(strs[0]);
            hrmmode.setSignkind("上班");
            list.add(hrmmode);
        } else {
            String first = String.valueOf(s.format(mutilss[0]));
            hrmmode.setSigntime(first.split(":")[0] + ":" + first.split(":")[1]);
            //try {
            //if (mutilss[0] < s.parse("12:00:00").getTime()) {
            hrmmode.setSignlx("0");
            //} else {
            //hrmmode.setSignlx("1");
            //}
            //} catch (ParseException e) {
            //e.printStackTrace();
            //}
            String doorname = hrmmode.getDoor();
            String[] strs = doorname.split(",");
            Arrays.sort(strs);
            hrmmode.setDoor(strs[0]);
            hrmmode.setSignkind("上班");
            list.add(hrmmode);
            TlHrmMode t = new TlHrmMode();
            t.setCardid(hrmmode.getCardid());
            t.setDoor(hrmmode.getDoor());
            t.setHrmid(hrmmode.getHrmid());
            t.setLx(hrmmode.getLx());
            t.setMutil_date(hrmmode.getMutil_date());
            t.setObjno(hrmmode.getObjno());
            t.setSigndate(hrmmode.getSigndate());
            t.setSfdk(hrmmode.getSfdk());
            String last = s.format(mutilss[mutilss.length - 1]);
            t.setSigntime(last.split(":")[0] + ":" + last.split(":")[1]);
            //try {
            //if (mutilss[mutilss.length - 1] < s.parse("12:00:00").getTime()) {
            //t.setSignlx("0");
            //} else {
            t.setSignlx("1");
            //}
            //} catch (ParseException e) {
            //e.printStackTrace();
            //}
            t.setDoor(strs[strs.length - 1]);
            t.setSignkind("下班");
            list.add(t);
        }
        return list;
    }

    /**
     * 获取办结SQL
     *
     * @param time
     * @param time2
     * @return
     */
    public String getFinishedSQL(String time, String hour, String hour2) {
        String sql = "select distinct t1.requestid, t1.createdate, t1.createtime,t1.creater,t3.resource1,t3.desc11,t1.lastoperatedate,t1.lastoperatetime,t3.date1 "
                + "from workflow_requestbase t1,workflow_currentoperator t2,workflow_form t3 "
                + "where t1.requestid = t2.requestid "
                + "and t1.workflowid in("+wdkflow+") "
                + "and t2.usertype=0 "
                + "and t2.isremark in('2','4') "
                + "and t1.currentnodetype = '3' "
                + "and iscomplete=1 "
                + "and t1.lastoperatedate  = '"
                + time
                + "' and t1.lastoperatetime between '"
                + hour
                + "' and '"
                + hour2
                + "' "
                + "and islasttimes=1 "
                + "and t3.billformid = '103' and (t3.wsklx = '1' or t3.wsklx = '2') "
                + "and t3.requestid = t1.requestid ";
        return sql;
    }

    public static void main(String[] args) throws ParseException {
        // String searchBeginDate = format.format(getIncomeDate(new
        // Date(),Integer.parseInt("1")));
        // 日期，如2015-12-12
        // String beginDate = searchBeginDate.split(" ")[0];
        // System.out.println(getWeekOfDate("2016-02-22"));
        // System.out.println(getWeekOfDateNum("2016-02-28"));
        // 2016-02-24 08:14:10.0,2016-02-24
        // TlHrmMode t = new TlHrmMode();
        // t.setMutil_date("2016-02-24 08:14:10.0,2016-02-24 08:15:12.0");
        // List<TlHrmMode> list = generateTlHrmModeInfo(t);
        // for(TlHrmMode g : list){
        // System.out.println(g.toString());
        // }
//		System.out.println(getWeekOfDateNum("2016-02-24"));
//		SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
//		boolean isnot = judgeTime(s.parse("08:30:00"), s.parse("08:45:23"), 30);
//		System.out.println(isnot);
    }
}
