package weaver.interfaces.jiangyl.kq;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;

public class TlKQServiceImpl implements TlKQService {

    public BaseBean      bean         = new BaseBean();
    public String        kqhztable    = bean.getPropValue("TLConn", "kqhztable");//formtable_main_160
    //一般时间对应的表
    public String        ybsjtable    = bean.getPropValue("TLConn", "ybsjtable");//formtable_main_161
    public static String fieldNd      = "13004";
    public static String fieldYf      = "13005";
    public static String zydFormTable = "formtable_main_68";

    @Override
    public ArrayList<TlSAPMode> getKQDataByDate(String begindate,
                                           String enddate) {
        ArrayList<TlSAPMode> result = new ArrayList<TlSAPMode>();
        //行政
        ArrayList<TlSAPMode> xzList = dealXZByDate(begindate, enddate);
        //直营店
        ArrayList<TlSAPMode> zydList = dealZYDByDate(begindate, enddate);
        result.addAll(xzList);
        result.addAll(zydList);
        return result;
    }

    /**
     * 处理行政
     * 
     * @param begindate
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealXZByDate(String begindate,
                                         String enddate) {
        //读取上午数据
        HashMap<String, TlHzMode> res1 = getAm(begindate, enddate);
        //读取下午数据
        HashMap<String, TlHzMode> res2 = getPm(begindate, enddate);
        ArrayList<TlSAPMode> jqList = dealAmPm(res1, res2, enddate);
        ArrayList<TlSAPMode> kgList = dealKg(res1, res2, enddate);
        ArrayList<TlSAPMode> cdList = dealCd(res1, enddate);
        ArrayList<TlSAPMode> ztList = dealZt(res2, enddate);
        ArrayList<TlSAPMode> wskList = dealWsk(res1, res2, begindate, enddate);
        ArrayList<TlSAPMode> jbList = dealJiaBan(begindate, enddate);
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        list.addAll(jqList);
        list.addAll(kgList);
        list.addAll(cdList);
        list.addAll(ztList);
        list.addAll(wskList);
        list.addAll(jbList);
        return list;
    }

    /**
     * 处理直营店
     * 
     * @param begindate
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealZYDByDate(String begindate,
                                          String enddate) {
        String nd = begindate.split("-")[0];
        String yf = begindate.split("-")[1];
        if (yf.startsWith("0")) {
            yf = yf.substring(1, yf.length());
        }
        yf = yf + "月";
        String fieldNd = "13004";
        String getNdSelectValue = "select selectvalue from workflow_selectitem where fieldid = '" + fieldNd + "' and selectname = '" + nd + "'";
        RecordSet rs = new RecordSet();
        rs.execute(getNdSelectValue);
        rs.next();
        String selectNdValue = rs.getString("selectvalue");

        String getYfSelectValue = "select selectvalue from workflow_selectitem where fieldid = '" + fieldYf + "' and selectname = '" + yf + "'";
        rs.execute(getYfSelectValue);
        rs.next();
        String selectYfValue = rs.getString("selectvalue");

        ArrayList<TlSAPMode> tl = new ArrayList<TlSAPMode>();
        String getAllZYDSQL = "select * from " + zydFormTable + " a, " + zydFormTable + "_dt1 b where a.id = b.mainid and a.nf = '" + selectNdValue + "' and a.yf = '" + selectYfValue + "'";
        rs.execute(getAllZYDSQL);
        while (rs.next()) {
            String gh = rs.getString("gh");
            String bj = rs.getString("C12");
            String sj = rs.getString("C11");
            String kg = rs.getString("C14");
            String bjy = rs.getString("bjy");
            String fd = rs.getString("C16");
            String cj = rs.getString("C13");
            String cqsc = rs.getString("C2");
            String hl = rs.getString("C6");
            String nj = rs.getString("C5");
            String jdj = rs.getString("jfj");
            String srj = rs.getString("srj");
            String sangj = rs.getString("C7");
            String pcj = rs.getString("C4");
            String brj = rs.getString("C3");
            String tqj = rs.getString("C10");
            String jhcqsj = rs.getString("C1");
            //String txhsc = rs.getString("");
            if ("".equals(gh)) {
                continue;
            }
            if (!"0".equals(bj) && null != bj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5000");
                mode.setDate(enddate);
                mode.setNumber(bj);
                tl.add(mode);
            }
            if (!"0".equals(sj) && null != sj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5010");
                mode.setDate(enddate);
                mode.setNumber(sj);
                tl.add(mode);
            }
            if (!"0".equals(bjy) && null != bjy) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5080");
                mode.setDate(enddate);
                mode.setNumber(bjy);
                tl.add(mode);
            }
            if (!"0".equals(kg) && null != kg) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5060");
                mode.setDate(enddate);
                mode.setNumber(kg);
                tl.add(mode);
            }
            if (!"0".equals(fd) && null != fd) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5090");
                mode.setDate(enddate);
                mode.setNumber(fd);
                tl.add(mode);
            }
            if (!"0".equals(cj) && null != cj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5100");
                mode.setDate(enddate);
                mode.setNumber(cj);
                tl.add(mode);
            }
            if (!"0".equals(cqsc) && null != cqsc) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5110");
                mode.setDate(enddate);
                mode.setNumber(cqsc);
                tl.add(mode);
            }
            if (!"0".equals(hl) && null != hl) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5120");
                mode.setDate(enddate);
                mode.setNumber(hl);
                tl.add(mode);
            }
            if (!"0".equals(nj) && null != nj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5130");
                mode.setDate(enddate);
                mode.setNumber(nj);
                tl.add(mode);
            }
            if (!"0".equals(jdj) && null != jdj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5140");
                mode.setDate(enddate);
                mode.setNumber(jdj);
                tl.add(mode);
            }
            if (!"0".equals(srj) && null != srj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5150");
                mode.setDate(enddate);
                mode.setNumber(srj);
                tl.add(mode);
            }
            if (!"0".equals(sangj) && null != sangj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5160");
                mode.setDate(enddate);
                mode.setNumber(sangj);
                tl.add(mode);
            }
            if (!"0".equals(pcj) && null != pcj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5170");
                mode.setDate(enddate);
                mode.setNumber(pcj);
                tl.add(mode);
            }
            if (!"0".equals(brj) && null != brj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5180");
                mode.setDate(enddate);
                mode.setNumber(brj);
                tl.add(mode);
            }
            if (!"0".equals(tqj) && null != tqj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5190");
                mode.setDate(enddate);
                mode.setNumber(tqj);
                tl.add(mode);
            }
            if (!"0".equals(jhcqsj) && null != jhcqsj) {
                TlSAPMode mode = new TlSAPMode();
                mode.setEmpid(getEmpId(gh));
                mode.setCode("5210");
                mode.setDate(enddate);
                mode.setNumber(jhcqsj);
                tl.add(mode);
            }

        }
        return tl;
    }

    /**
     * 处理加班
     * 
     * @param begindate
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealJiaBan(String begindate,
                                      String enddate) {
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        String getAllJiaBan = "select distinct t1.requestid, t1.createdate, t1.createtime,t1.creater,t3.resource1,t3.desc11,t1.lastoperatedate,t1.lastoperatetime,t3.date1,t3.amount,t3.jiejiari,t3.begindate " + "from workflow_requestbase t1,workflow_currentoperator t2,workflow_form t3 " + "where t1.requestid = t2.requestid " + "and t1.workflowid in(27) " + "and t2.usertype=0 "
                + "and t2.isremark in('2','4') " + "and t1.currentnodetype = '3' " + "and iscomplete=1 " + "and t1.lastoperatedate  between '" + begindate + "' and '" + enddate + "'" + "and islasttimes=1 " + "and t3.billformid = '20' and t3.jiejiari = '1'  " + "and t3.requestid = t1.requestid";

        RecordSet rs = new RecordSet();
        rs.execute(getAllJiaBan);
        while (rs.next()) {
            String hrmid = rs.getString("creater");
            String loginid = "";
            try {
                loginid = new ResourceComInfo().getLoginID(hrmid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String date = rs.getString("begindate");
            String count = rs.getString("amount");
            TlSAPMode tl = new TlSAPMode();
            tl.setCode("5090");
            tl.setDate(date);
            tl.setEmpid(loginid);
            tl.setNumber(count);
            list.add(tl);
        }
        return list;
    }

    /**
     * 处理未刷卡
     * 
     * @param res1
     * @param res2
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealWsk(HashMap<String, TlHzMode> res1,
            HashMap<String, TlHzMode> res2,
                                   String begindate,
                                   String enddate) {
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        HashMap<String, String> map = new HashMap<String, String>();
        RecordSet rs = new RecordSet();
        rs.execute("select distinct signdate,hrmid from " + kqhztable + " where signdate between '" + begindate + "' and '" + enddate + "' and sfdk = '未带卡'");
        while (rs.next()) {
            String hrmid = rs.getString("hrmid");
            if (null == hrmid) {
                continue;
            }
            String loginid = "";
            try {
                loginid = new ResourceComInfo().getLoginID(hrmid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (map.containsKey(loginid)) {
                map.put(loginid, String.valueOf(Integer.parseInt(map.get(loginid)) + 1));
            } else {
                int count = 1;
                map.put(loginid, String.valueOf(count));
            }
        }
        for (Entry<String, String> entry : map.entrySet()) {
            TlSAPMode tl = new TlSAPMode();
            tl.setCode("5070");
            tl.setDate(enddate);
            tl.setEmpid(entry.getKey());
            tl.setNumber(entry.getValue());
            list.add(tl);
        }
        return list;
    }

    /**
     * 处理迟到
     * 
     * @param res1
     * @param res2
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealCd(HashMap<String, TlHzMode> res1,
                                  String enddate) {
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        RecordSet rs = new RecordSet();
        ResourceComInfo r = null;
        for (Entry<String, TlHzMode> entry : res1.entrySet()) {
            String key = entry.getKey();
            TlHzMode mode = entry.getValue();
            if (null == key) {
                continue;
            }
            Map<String, String> map = mode.getMap();
            for (Entry<String, String> ent : map.entrySet()) {
                String ekey = ent.getKey();
                String eval = ent.getValue();
                if ("迟到".equals(eval)) {
                    String fsdate = enddate.split("-")[0] + "-" + enddate.split("-")[1];
                    if (ekey.length() == 1) {
                        fsdate = fsdate + "-0" + ekey;
                    } else {
                        fsdate = fsdate + "-" + ekey;
                    }
                    String sql = "select * from " + kqhztable + " where hrmid = '" + key + "' and signdate = '" + fsdate + "' and kqlx = '18'";
                    rs.execute(sql);
                    rs.next();
                    String signtime = rs.getString("signtime");
                    try {
                        r = new ResourceComInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String departmentid = r.getDepartmentID(key);
                    String number = getWeekOfDateNum(fsdate);
                    TlTimeMode timeMode = getTimeInfo(number, departmentid, fsdate,key);
                    String sbsj = timeMode.getAm_sbsj();
                    String mins = compareTime(signtime, sbsj);
                    String loginid = "";
                    try {
                        loginid = new ResourceComInfo().getLoginID(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TlSAPMode tl = new TlSAPMode();
                    tl.setCode("5020");
                    tl.setDate(fsdate);
                    tl.setEmpid(loginid);
                    tl.setNumber(mins);
                    list.add(tl);
                }
            }
        }
        return list;
    }

    /**
     * 处理早退
     * 
     * @param res1
     * @param res2
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealZt(HashMap<String, TlHzMode> res2,
                                  String enddate) {
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        RecordSet rs = new RecordSet();
        String date = enddate.split("-")[0] + enddate.split("-")[1];
        ResourceComInfo r = null;
        for (Entry<String, TlHzMode> entry : res2.entrySet()) {
            String key = entry.getKey();
            TlHzMode mode = entry.getValue();
            if (null == key) {
                continue;
            }
            Map<String, String> map = mode.getMap();
            for (Entry<String, String> ent : map.entrySet()) {
                String ekey = ent.getKey();
                String eval = ent.getValue();
                if ("早退".equals(eval)) {
                    String fsdate = date;
                    if (ekey.length() == 1) {
                        fsdate = fsdate + "-0" + ekey;
                    } else {
                        fsdate = fsdate + ekey;
                    }
                    String sql = "select * from " + kqhztable + " where hrmid = '" + key + "' and signdate = '" + fsdate + "' and kqlx = '19'";
                    rs.execute(sql);
                    rs.next();
                    String signtime = rs.getString("signtime");
                    try {
                        r = new ResourceComInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String departmentid = r.getDepartmentID(key);
                    String number = getWeekOfDateNum(fsdate);
                    TlTimeMode timeMode = getTimeInfo(number, departmentid, date,key);
                    String sbsj = timeMode.getPm_xbsj();
                    String mins = String.valueOf(Math.abs(Integer.parseInt(compareTime(signtime, sbsj))));
                    String loginid = "";
                    try {
                        loginid = new ResourceComInfo().getLoginID(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TlSAPMode tl = new TlSAPMode();
                    tl.setCode("5020");
                    tl.setDate(fsdate);
                    tl.setEmpid(loginid);
                    tl.setNumber(mins);
                    list.add(tl);
                }
            }
        }
        return list;
    }

    /**
     * 处理旷工
     * 
     * @param res1
     * @param res2
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealKg(HashMap<String, TlHzMode> res1,
            HashMap<String, TlHzMode> res2,
                                  String enddate) {
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        for (Entry<String, TlHzMode> entry : res1.entrySet()) {
            String key = entry.getKey();
            if (null == key) {
                continue;
            }
            String loginid = "";
            try {
                loginid = new ResourceComInfo().getLoginID(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TlHzMode ammode = entry.getValue();
            int count = 0;
            Map<String, String> ammap = ammode.getMap();
            for (Entry<String, String> amEntry : ammap.entrySet()) {
                String day = amEntry.getKey();
                String content = amEntry.getValue();
                if ("旷工".equals(content)) {
                    count++;
                    TlSAPMode tl = new TlSAPMode();
                    tl.setCode("5060");
                    tl.setDate(enddate);
                    tl.setEmpid(loginid);
                    tl.setNumber(String.valueOf(count));
                    list.add(tl);
                    if (res2.containsKey(key)) {
                        res2.get(key).getMap().remove(day);
                    }
                }

            }
        }
        for (Entry<String, TlHzMode> entry : res2.entrySet()) {
            String key = entry.getKey();
            if (null == key) {
                continue;
            }
            String loginid = "";
            try {
                loginid = new ResourceComInfo().getLoginID(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TlHzMode pmmode = entry.getValue();
            int count = 0;
            Map<String, String> ammap = pmmode.getMap();
            for (Entry<String, String> amEntry : ammap.entrySet()) {
                String content = amEntry.getValue();
                if ("旷工".equals(content)) {
                    count++;
                    TlSAPMode tl = new TlSAPMode();
                    tl.setCode("5060");
                    tl.setDate(enddate);
                    tl.setEmpid(loginid);
                    tl.setNumber(String.valueOf(count));
                    list.add(tl);
                }

            }
        }
        return list;
    }

    /**
     * 处理各种假小时数
     * 
     * @param res1
     * @param res2
     * @param enddate
     * @return
     */
    private ArrayList<TlSAPMode> dealAmPm(HashMap<String, TlHzMode> res1,
            HashMap<String, TlHzMode> res2,
                                    String enddate) {
        ArrayList<TlSAPMode> list = new ArrayList<TlSAPMode>();
        for (Entry<String, TlHzMode> entry : res1.entrySet()) {
            String key = entry.getKey();
            String loginid = "";
            try {
                loginid = new ResourceComInfo().getLoginID(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TlHzMode ammode = entry.getValue();
            if (res2.containsKey(key)) {
                TlHzMode pmmode = res2.get(key);
                if (ammode.getBjhj() != "0" || pmmode.getBjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5000");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getBjhj();
                    String pmTotal = pmmode.getBjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getBrjhj() != "0" || pmmode.getBrjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5180");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getBrjhj();
                    String pmTotal = pmmode.getBrjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getCjhj() != "0" || pmmode.getCjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5100");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getCjhj();
                    String pmTotal = pmmode.getCjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getHjhj() != "0" || pmmode.getHjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5120");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getHjhj();
                    String pmTotal = pmmode.getHjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getJdjhj() != "0" || pmmode.getJdjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5140");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getJdjhj();
                    String pmTotal = pmmode.getJdjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getNjhj() != "0" || pmmode.getNjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5130");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getNjhj();
                    String pmTotal = pmmode.getNjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getPcjhj() != "0" || pmmode.getPcjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5170");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getPcjhj();
                    String pmTotal = pmmode.getPcjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getSangjhj() != "0" || pmmode.getSangjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5160");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getSangjhj();
                    String pmTotal = pmmode.getSangjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getSjhj() != "0" || pmmode.getSjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5010");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getSjhj();
                    String pmTotal = pmmode.getSjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getSrjhj() != "0" || pmmode.getSrjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5150");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getSrjhj();
                    String pmTotal = pmmode.getSrjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getTqjhj() != "0" || pmmode.getTqjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5190");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getTqjhj();
                    String pmTotal = pmmode.getTqjhj();
                    String totalHours = new BigDecimal(amTotal).add(new BigDecimal(pmTotal)).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
            } else {
                if (ammode.getBjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5000");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getBjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getBrjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5180");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getBrjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getCjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5100");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getCjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getHjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5120");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getHjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getJdjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5140");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getJdjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getNjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5130");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getNjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getPcjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5170");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getPcjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getSangjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5160");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getSangjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getSjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5010");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getSjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getSrjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5150");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getSrjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
                if (ammode.getTqjhj() != "0") {
                    TlSAPMode t = new TlSAPMode();
                    t.setCode("5190");
                    t.setDate(enddate);
                    t.setEmpid(loginid);
                    String amTotal = ammode.getTqjhj();
                    String totalHours = new BigDecimal(amTotal).toString();
                    t.setNumber(totalHours);
                    list.add(t);
                }
            }
        }
        return list;
    }

    /**
     * 根据部门和日期获取汇总信息
     * 
     * @param departmentid
     * @param date
     * @return
     */
    private HashMap<String, TlHzMode> getAm(String begindate,
                                       String enddate) {
        HashMap<String, TlHzMode> map = new HashMap<String, TlHzMode>();
        String tableName = bean.getPropValue("TLConn", "kqhztable");
        String getAllSQL = "select a.hrmid,a.signdate,a.kqlx,a.signtime,a.signlx,a.sjhj,a.bjhj,a.cjhj,a.njhj,a.hjhj,a.txhj,a.jdjhj,a.srjhj,a.sangjhj,a.pcjhj,a.brjhj,a.tqjhj " + "from " + tableName + " a,hrmresource b " + "where a.hrmid = b.id and a.signlx = '0' and a.signdate between '" + begindate + "' and '" + enddate + "'";
        bean.writeLog("getAllSQL :" + getAllSQL);
        RecordSet rs = new RecordSet();
        rs.execute(getAllSQL);
        while (rs.next()) {
            String hrmid = Util.null2String(rs.getString("hrmid"));
            String signdate = Util.null2String(rs.getString("signdate"));
            String kqlx = Util.null2String(rs.getString("kqlx"));
            String sjhj = Util.null2String(rs.getString("sjhj"));

            if ("".equals(sjhj)) {
                sjhj = "0";
            }
            String bjhj = Util.null2String(rs.getString("bjhj"));
            if ("".equals(bjhj)) {
                bjhj = "0";
            }
            String cjhj = Util.null2String(rs.getString("cjhj"));
            if ("".equals(cjhj)) {
                cjhj = "0";
            }
            String njhj = Util.null2String(rs.getString("njhj"));
            if ("".equals(njhj)) {
                njhj = "0";
            }
            String hjhj = Util.null2String(rs.getString("hjhj"));
            if ("".equals(hjhj)) {
                hjhj = "0";
            }
            String txhj = Util.null2String(rs.getString("txhj"));
            if ("".equals(txhj)) {
                txhj = "0";
            }

            String jdjhj = Util.null2String(rs.getString("jdjhj"));
            if ("".equals(jdjhj)) {
                jdjhj = "0";
            }
            String srjhj = Util.null2String(rs.getString("srjhj"));
            if ("".equals(srjhj)) {
                srjhj = "0";
            }
            String sangjhj = Util.null2String(rs.getString("sangjhj"));
            if ("".equals(sangjhj)) {
                sangjhj = "0";
            }
            String pcjhj = Util.null2String(rs.getString("pcjhj"));
            if ("".equals(pcjhj)) {
                pcjhj = "0";
            }
            String brjhj = Util.null2String(rs.getString("brjhj"));
            if ("".equals(brjhj)) {
                brjhj = "0";
            }
            String tqjhj = Util.null2String(rs.getString("tqjhj"));
            if ("".equals(tqjhj)) {
                tqjhj = "0";
            }

            if (map.containsKey(hrmid)) {
                TlHzMode tl = map.get(hrmid);
                Map<String, String> tmpmap = tl.getMap();
                String day = getDayOfDate(signdate);
                if (isWeekend(signdate)) {
                    tmpmap.put(day, "");
                } else {
                    String tsjhj = tl.getSjhj();
                    if ("".equals(tsjhj) || null == tsjhj) {
                        tsjhj = "0";
                    }
                    String tbjhj = tl.getBjhj();
                    if ("".equals(tbjhj) || null == tbjhj) {
                        tbjhj = "0";
                    }
                    String tcjhj = tl.getCjhj();
                    if ("".equals(tcjhj) || null == tcjhj) {
                        tcjhj = "0";
                    }
                    String tnjhj = tl.getNjhj();
                    if ("".equals(tnjhj) || null == tnjhj) {
                        tnjhj = "0";
                    }
                    String thjhj = tl.getHjhj();
                    if ("".equals(thjhj) || null == thjhj) {
                        thjhj = "0";
                    }
                    String ttxhj = tl.getTxhj();
                    if ("".equals(ttxhj) || null == ttxhj) {
                        ttxhj = "0";
                    }

                    String tjdjhj = tl.getJdjhj();
                    if ("".equals(tjdjhj) || null == tjdjhj) {
                        tjdjhj = "0";
                    }
                    String tsrjhj = tl.getSrjhj();
                    if ("".equals(tsrjhj) || null == tsrjhj) {
                        tsrjhj = "0";
                    }
                    String tsangjhj = tl.getSangjhj();
                    if ("".equals(tsangjhj) || null == tsangjhj) {
                        tsangjhj = "0";
                    }
                    String tpcjhj = tl.getPcjhj();
                    if ("".equals(tpcjhj) || null == tpcjhj) {
                        tpcjhj = "0";
                    }
                    String tbrjhj = tl.getBrjhj();
                    if ("".equals(tbrjhj) || null == tbrjhj) {
                        tbrjhj = "0";
                    }
                    String ttqjhj = tl.getTqjhj();
                    if ("".equals(ttqjhj) || null == ttqjhj) {
                        ttqjhj = "0";
                    }
                    tl.setSjhj(new BigDecimal(tsjhj).add(new BigDecimal(String.valueOf(sjhj))).toString());
                    tl.setBjhj(new BigDecimal(tbjhj).add(new BigDecimal(String.valueOf(bjhj))).toString());
                    tl.setCjhj(new BigDecimal(tcjhj).add(new BigDecimal(String.valueOf(cjhj))).toString());
                    tl.setNjhj(new BigDecimal(tnjhj).add(new BigDecimal(String.valueOf(bjhj))).toString());
                    tl.setHjhj(new BigDecimal(thjhj).add(new BigDecimal(String.valueOf(hjhj))).toString());
                    tl.setTxhj(new BigDecimal(ttxhj).add(new BigDecimal(String.valueOf(txhj))).toString());

                    tl.setJdjhj(new BigDecimal(tjdjhj).add(new BigDecimal(String.valueOf(jdjhj))).toString());
                    tl.setSrjhj(new BigDecimal(tsrjhj).add(new BigDecimal(String.valueOf(srjhj))).toString());
                    tl.setSangjhj(new BigDecimal(tsangjhj).add(new BigDecimal(String.valueOf(sangjhj))).toString());
                    tl.setPcjhj(new BigDecimal(tpcjhj).add(new BigDecimal(String.valueOf(pcjhj))).toString());
                    tl.setBrjhj(new BigDecimal(tbrjhj).add(new BigDecimal(String.valueOf(brjhj))).toString());
                    tl.setTqjhj(new BigDecimal(ttqjhj).add(new BigDecimal(String.valueOf(tqjhj))).toString());

                    tmpmap.put(day, getLxOfKQ(kqlx));
                    tl.setMap(tmpmap);
                    map.put(hrmid, tl);
                }
            } else {
                TlHzMode tl = new TlHzMode();
                HashMap<String, String> tmpmap = new HashMap<String, String>();
                String day = getDayOfDate(signdate);
                if (isWeekend(signdate)) {
                    tl.setName(getHrmResourceName(hrmid));
                    tmpmap.put(day, "");
                    tl.setBjhj("0");
                    tl.setCjhj("0");
                    tl.setHjhj("0");
                    tl.setNjhj("0");
                    tl.setSjhj("0");
                    tl.setTxhj("0");
                    tl.setJdjhj("0");
                    tl.setSrjhj("0");
                    tl.setSangjhj("0");
                    tl.setPcjhj("0");
                    tl.setBrjhj("0");
                    tl.setTqjhj("0");
                    tl.setMap(tmpmap);
                } else {
                    tl.setName(getHrmResourceName(hrmid));
                    tmpmap.put(day, getLxOfKQ(kqlx));
                    tl.setBjhj(String.valueOf(bjhj));
                    tl.setCjhj(String.valueOf(cjhj));
                    tl.setHjhj(String.valueOf(hjhj));
                    tl.setNjhj(String.valueOf(njhj));
                    tl.setSjhj(String.valueOf(sjhj));
                    tl.setTxhj(String.valueOf(txhj));
                    tl.setJdjhj(String.valueOf(jdjhj));
                    tl.setSrjhj(String.valueOf(srjhj));
                    tl.setSangjhj(String.valueOf(sangjhj));
                    tl.setPcjhj(String.valueOf(pcjhj));
                    tl.setBrjhj(String.valueOf(brjhj));
                    tl.setTqjhj(String.valueOf(tqjhj));
                    tl.setMap(tmpmap);
                }
                map.put(hrmid, tl);
            }
        }
        return map;
    }

    /**
     * 根据部门和日期获取汇总信息
     * 
     * @param departmentid
     * @param date
     * @return
     */
    private HashMap<String, TlHzMode> getPm(String begindate,
                                       String enddate) {
        HashMap<String, TlHzMode> map = new HashMap<String, TlHzMode>();
        String tableName = bean.getPropValue("TlConn", "kqhztable");
        String getAllSQL = "select a.hrmid,a.signdate,a.kqlx,a.signtime,a.signlx,a.sjhj,a.bjhj,a.cjhj,a.njhj,a.hjhj,a.txhj,a.jdjhj,a.srjhj,a.sangjhj,a.pcjhj,a.brjhj,a.tqjhj " + "from " + tableName + " a,hrmresource b " + "where a.hrmid = b.id and a.signlx = '1' and a.signdate between '" + begindate + "' and '" + enddate + "'";
        bean.writeLog("getAllSQL2 :" + getAllSQL);
        RecordSet rs = new RecordSet();
        rs.execute(getAllSQL);
        while (rs.next()) {
            String hrmid = Util.null2String(rs.getString("hrmid"));
            String signdate = Util.null2String(rs.getString("signdate"));
            String kqlx = Util.null2String(rs.getString("kqlx"));
            String sjhj = Util.null2String(rs.getString("sjhj"));
            if ("".equals(sjhj)) {
                sjhj = "0";
            }
            String bjhj = Util.null2String(rs.getString("bjhj"));
            if ("".equals(bjhj)) {
                bjhj = "0";
            }
            String cjhj = Util.null2String(rs.getString("cjhj"));
            if ("".equals(cjhj)) {
                cjhj = "0";
            }
            String njhj = Util.null2String(rs.getString("njhj"));
            if ("".equals(njhj)) {
                njhj = "0";
            }
            String hjhj = Util.null2String(rs.getString("hjhj"));
            if ("".equals(hjhj)) {
                hjhj = "0";
            }
            String txhj = Util.null2String(rs.getString("txhj"));
            if ("".equals(txhj)) {
                txhj = "0";
            }
            String jdjhj = Util.null2String(rs.getString("jdjhj"));
            if ("".equals(jdjhj)) {
                jdjhj = "0";
            }
            String srjhj = Util.null2String(rs.getString("srjhj"));
            if ("".equals(srjhj)) {
                srjhj = "0";
            }
            String sangjhj = Util.null2String(rs.getString("sangjhj"));
            if ("".equals(sangjhj)) {
                sangjhj = "0";
            }
            String pcjhj = Util.null2String(rs.getString("pcjhj"));
            if ("".equals(pcjhj)) {
                pcjhj = "0";
            }
            String brjhj = Util.null2String(rs.getString("brjhj"));
            if ("".equals(brjhj)) {
                brjhj = "0";
            }
            String tqjhj = Util.null2String(rs.getString("tqjhj"));
            if ("".equals(tqjhj)) {
                tqjhj = "0";
            }

            if (map.containsKey(hrmid)) {
                TlHzMode tl = map.get(hrmid);
                Map<String, String> tmpmap = tl.getMap();
                String day = getDayOfDate(signdate);
                if (isWeekend(signdate)) {
                    tmpmap.put(day, "");
                } else {
                    String tsjhj = tl.getSjhj();
                    if ("".equals(tsjhj) || null == tsjhj) {
                        tsjhj = "0";
                    }
                    String tbjhj = tl.getBjhj();
                    if ("".equals(tbjhj) || null == tbjhj) {
                        tbjhj = "0";
                    }
                    String tcjhj = tl.getCjhj();
                    if ("".equals(tcjhj) || null == tcjhj) {
                        tcjhj = "0";
                    }
                    String tnjhj = tl.getNjhj();
                    if ("".equals(tnjhj) || null == tnjhj) {
                        tnjhj = "0";
                    }
                    String thjhj = tl.getHjhj();
                    if ("".equals(thjhj) || null == thjhj) {
                        thjhj = "0";
                    }
                    String ttxhj = tl.getTxhj();
                    if ("".equals(ttxhj) || null == ttxhj) {
                        ttxhj = "0";
                    }
                    String tjdjhj = tl.getJdjhj();
                    if ("".equals(tjdjhj) || null == tjdjhj) {
                        tjdjhj = "0";
                    }
                    String tsrjhj = tl.getSrjhj();
                    if ("".equals(tsrjhj) || null == tsrjhj) {
                        tsrjhj = "0";
                    }
                    String tsangjhj = tl.getSangjhj();
                    if ("".equals(tsangjhj) || null == tsangjhj) {
                        tsangjhj = "0";
                    }
                    String tpcjhj = tl.getPcjhj();
                    if ("".equals(tpcjhj) || null == tpcjhj) {
                        tpcjhj = "0";
                    }
                    String tbrjhj = tl.getBrjhj();
                    if ("".equals(tbrjhj) || null == tbrjhj) {
                        tbrjhj = "0";
                    }
                    String ttqjhj = tl.getTqjhj();
                    if ("".equals(ttqjhj) || null == ttqjhj) {
                        ttqjhj = "0";
                    }
                    tl.setSjhj(new BigDecimal(tsjhj).add(new BigDecimal(sjhj)).toString());
                    tl.setBjhj(new BigDecimal(tbjhj).add(new BigDecimal(bjhj)).toString());
                    tl.setCjhj(new BigDecimal(tcjhj).add(new BigDecimal(cjhj)).toString());
                    tl.setNjhj(new BigDecimal(tnjhj).add(new BigDecimal(bjhj)).toString());
                    tl.setHjhj(new BigDecimal(thjhj).add(new BigDecimal(hjhj)).toString());
                    tl.setTxhj(new BigDecimal(ttxhj).add(new BigDecimal(txhj)).toString());
                    tl.setJdjhj(new BigDecimal(tjdjhj).add(new BigDecimal(String.valueOf(jdjhj))).toString());
                    tl.setSrjhj(new BigDecimal(tsrjhj).add(new BigDecimal(String.valueOf(srjhj))).toString());
                    tl.setSangjhj(new BigDecimal(tsangjhj).add(new BigDecimal(String.valueOf(sangjhj))).toString());
                    tl.setPcjhj(new BigDecimal(tpcjhj).add(new BigDecimal(String.valueOf(pcjhj))).toString());
                    tl.setBrjhj(new BigDecimal(tbrjhj).add(new BigDecimal(String.valueOf(brjhj))).toString());
                    tl.setTqjhj(new BigDecimal(ttqjhj).add(new BigDecimal(String.valueOf(tqjhj))).toString());
                    tmpmap.put(day, getLxOfKQ(kqlx));
                    tl.setMap(tmpmap);
                    map.put(hrmid, tl);
                }
            } else {
                TlHzMode tl = new TlHzMode();
                HashMap<String, String> tmpmap = new HashMap<String, String>();
                String day = getDayOfDate(signdate);
                if (isWeekend(signdate)) {
                    tl.setName(getHrmResourceName(hrmid));
                    tmpmap.put(day, "");
                    tl.setBjhj("0");
                    tl.setCjhj("0");
                    tl.setHjhj("0");
                    tl.setNjhj("0");
                    tl.setSjhj("0");
                    tl.setTxhj("0");
                    tl.setJdjhj("0");
                    tl.setSrjhj("0");
                    tl.setSangjhj("0");
                    tl.setPcjhj("0");
                    tl.setBrjhj("0");
                    tl.setTqjhj("0");
                    tl.setMap(tmpmap);
                } else {
                    tl.setName(getHrmResourceName(hrmid));
                    tmpmap.put(day, getLxOfKQ(kqlx));
                    tl.setBjhj(String.valueOf(bjhj));
                    tl.setCjhj(String.valueOf(cjhj));
                    tl.setHjhj(String.valueOf(hjhj));
                    tl.setNjhj(String.valueOf(njhj));
                    tl.setSjhj(String.valueOf(sjhj));
                    tl.setTxhj(String.valueOf(txhj));
                    tl.setJdjhj(String.valueOf(jdjhj));
                    tl.setSrjhj(String.valueOf(srjhj));
                    tl.setSangjhj(String.valueOf(sangjhj));
                    tl.setPcjhj(String.valueOf(pcjhj));
                    tl.setBrjhj(String.valueOf(brjhj));
                    tl.setTqjhj(String.valueOf(tqjhj));
                    tl.setMap(tmpmap);
                }
                map.put(hrmid, tl);
            }
        }
        return map;
    }

    /**
     * 转换下拉列表值
     * 
     * @param str
     * @return
     */
    private String getLxOfKQ(String str) {
        if ("0".equals(str)) {
            return "/";
        }
        if ("1".equals(str)) {
            return "调休";
        }
        if ("2".equals(str)) {
            return "三八调休";
        }
        if ("3".equals(str)) {
            return "产前病假";
        }
        if ("4".equals(str)) {
            return "事假";
        }
        if ("5".equals(str)) {
            return "病假";
        }
        if ("6".equals(str)) {
            return "产假";
        }
        if ("7".equals(str)) {
            return "公出";
        }
        if ("8".equals(str)) {
            return "出差";
        }
        if ("9".equals(str)) {
            return "丧假";
        }
        if ("10".equals(str)) {
            return "婚假";
        }
        if ("11".equals(str)) {
            return "产检假";
        }
        if ("12".equals(str)) {
            return "陪护假";
        }
        if ("13".equals(str)) {
            return "年假";
        }
        if ("14".equals(str)) {
            return "哺乳假";
        }
        if ("15".equals(str)) {
            return "出差调休";
        }
        if ("16".equals(str)) {
            return "生日调休";
        }
        if ("17".equals(str)) {
            return "离职";
        }
        if ("18".equals(str)) {
            return "迟到";
        }
        if ("19".equals(str)) {
            return "早退";
        }
        if ("20".equals(str)) {
            return "旷工";
        }
        if ("21".equals(str)) {
            return "未带卡";
        }
        if ("22".equals(str)) {
            return "探亲假";
        }
        if ("23".equals(str)) {
            return "积分假";
        }
        return "";
    }

    /**
     * 获取日期后面的数字 2016-03-01 获取01
     * 
     * @param date
     * @return
     */
    private String getDayOfDate(String date) {
        if (date.split("-")[2].startsWith("0")) {
            return date.split("-")[2].substring(1, date.split("-")[2].length());
        }
        return date.split("-")[2];
    }

    /**
     * 判断一个日历是不是周末.
     *
     * @param calendar
     *            the calendar
     * @return true, if checks if is weekend
     */
    private static boolean isWeekend(String dateString) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = s.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //判断是星期几
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1 || dayOfWeek == 7) {
            return true;
        }
        return false;
    }

    /**
     * 根据人员id获取人员姓名
     * 
     * @param hrmid
     * @return
     */
    private String getHrmResourceName(String hrmid) {
        ResourceComInfo r;
        String name = "";
        try {
            r = new ResourceComInfo();
            name = r.getResourcename(hrmid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 根据日期获得星期
     * 
     * @param date
     * @return
     */
    private static String getWeekOfDateNum(String date) {
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
    private TlTimeMode getTimeInfo(String week,
                                  String departmentId,
                                  String date,
                                  String hrmid) {
        RecordSet rs = new RecordSet();
        String swsb = "";
        String swxb = "";
        String xwsb = "";
        String xwxb = "";
        rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb,b.renyuan from " + ybsjtable + " a, " + ybsjtable + "_dt1 b where a.id = b.mainid and b.xq = '" + week + "' and b.bm = '" + departmentId + "' and a.kssxrq <= '" + date + "' and a.jssxrq >= '" + date + "' and renyuan like '%" + hrmid + "%'");
        while (rs.next()) {
            String renyuan = rs.getString("renyuan");
            if (!"".equals(renyuan) && null != renyuan) {
                String[] strs = renyuan.split(",");
                for (String s : strs) {
                    if (s.equalsIgnoreCase(hrmid)) {
                        swsb = rs.getString("swsb");
                        swxb = rs.getString("swxb");
                        xwsb = rs.getString("xwsb");
                        xwxb = rs.getString("xwxb");
                    }
                }
            }
        }
        if ("".equals(swsb) || null == swsb || "".equals(swxb) || null == swxb || "".equals(xwsb) || null == xwsb || "".equals(xwxb) || null == xwxb) {
            rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb from " + ybsjtable + " a, " + ybsjtable + "_dt1 b where a.id = b.mainid and b.xq = '" + week + "' and b.bm = '" + departmentId + "' and a.kssxrq <= '" + date + "' and a.jssxrq >= '" + date + "' and renyuan is null ");
            while (rs.next()) {
                swsb = rs.getString("swsb");
                swxb = rs.getString("swxb");
                xwsb = rs.getString("xwsb");
                xwxb = rs.getString("xwxb");
            }
        }
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
     * 获取迟到时间（分数）
     * 
     * @param time
     * @param standardtime
     * @return
     */
    private static String compareTime(String time,
                                     String standardtime) {
        Calendar c = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("HH:mm").parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            c1.setTime(new SimpleDateFormat("HH:mm").parse(standardtime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long mins = (c.getTime().getTime() - c1.getTime().getTime()) / (1000 * 60);
        return String.valueOf(mins);
    }

    /**
     * 获取员工编号
     * 
     * @param hrmid
     * @return
     */
    private String getEmpId(String hrmid) {
        ResourceComInfo r;
        String login = "";
        try {
            r = new ResourceComInfo();
            login = r.getLoginID(hrmid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return login;
    }
}
