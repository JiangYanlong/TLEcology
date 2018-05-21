package weaver.interfaces.jiangyl.kq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.hrm.resource.ResourceComInfo;

public class TlYckqhz extends BaseBean {

    private static BaseBean bean = new BaseBean();
    //考勤汇总表
    public String kqhztable = bean.getPropValue("TLConn", "kqhztable");
    //一般时间对应的表
    public String ybsjtable = bean.getPropValue("TLConn", "ybsjtable");

    public Map<String, TlYcMode> get(String date, String departdmentid) {
        // 当月第一天
        String begindate = date + "-01";
        // 当月最后一天
        String enddate = getLastDayOfCurrentMonth(begindate);

        RecordSet rs = new RecordSet();
        rs.execute("select hrmid,signdate,b.departmentid from "+kqhztable+" a,hrmresource b where a.hrmid = b.id and sfdk = '未带卡'  and b.departmentid = '"+departdmentid+"' and signdate between '"+begindate+"' and '"+enddate+"';");
        Map<String,TlYcMode> map = new HashMap<String,TlYcMode>();
        while(rs.next()) {
            String hrmid = rs.getString("hrmid");
            String signdate = rs.getString("signdate");
            if (null == hrmid) {
                continue;
            }
            if (map.containsKey(hrmid)) {
                String count = map.get(hrmid).getCount();
                map.get(hrmid).setCount(String.valueOf(Integer.parseInt(count) + 1));
            } else {
                ResourceComInfo r = null;
                try {
                    r = new ResourceComInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String name = r.getResourcename(hrmid);
                String loginid = r.getLoginID(hrmid);
                TlYcMode t = new TlYcMode();
                t.setName(name);
                t.setLoginid(loginid);
                t.setSigndate(signdate);
                map.put(hrmid, t);
            }
        }
        Map<String,TlYcMode> result = new HashMap<String,TlYcMode>();
        for(Entry<String, TlYcMode> entry : map.entrySet()) {
            String key = entry.getKey();
            TlYcMode t = entry.getValue();
            if (Integer.parseInt(t.getCount()) >= 1) {
                t.setType("其他");
                result.put(key, t);
            }
        }


        TlYckq tl = new TlYckq();
        Map<String, TlHrmMode> tmap = tl.get(date, departdmentid);
        Map<String,TlYcMode> tmpmap = new HashMap<String,TlYcMode>();
        for (Entry<String, TlHrmMode> entry : tmap.entrySet()) {
            TlHrmMode tt = entry.getValue();
            String hrmid = tt.getHrmid();
            ResourceComInfo r = null;
            try {
                r = new ResourceComInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String name = r.getResourcename(hrmid);
            String loginid = r.getLoginID(hrmid);
            String lx = tt.getLx();
            if ("迟到、早退".equals(lx)) {
                if (tmpmap.containsKey(hrmid)) {
                    String count = tmpmap.get(hrmid).getCount();
                    tmpmap.get(hrmid).setCount(String.valueOf(Integer.parseInt(count) + 1));
                }else {
                    TlYcMode t = new TlYcMode();
                    t.setLoginid(loginid);
                    t.setName(name);
                    t.setSigndate(tt.getSigndate());
                    tmpmap.put(hrmid, t);
                }
            }
        }
        for(Entry<String, TlYcMode> entry : tmpmap.entrySet()) {
            String key = entry.getKey();
            TlYcMode t = entry.getValue();
            if (Integer.parseInt(t.getCount()) >= 3) {
                t.setType("旷工");
                result.put(key, t);
            }
        }
        return result;
    }

    /**
     * 获取类型
     *
     * @param hrmid
     * @param signdate
     * @param begintime
     * @param endtime
     * @return
     */
    public String getLx(String hrmid,String signdate,String begintime,String endtime) {
        RecordSet rs = new RecordSet();
        String num = getWeekOfDateNum(signdate);
        ResourceComInfo r = null;
        try {
            r = new ResourceComInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String depid = r.getDepartmentID(hrmid);
        String swsb = "";
        String swxb = "";
        String xwsb = "";
        String xwxb = "";
        rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb,b.renyuan from "+ybsjtable+" a, "+ybsjtable+"_dt1 b where a.id = b.mainid and b.xq = '" + num + "' and b.bm = '"+depid+"' and a.kssxrq <= '"+signdate+"' and a.jssxrq >= '"+signdate+"' and renyuan like '%"+hrmid+"%'");
        while(rs.next()){
            String renyuan = rs.getString("renyuan");
            if(!"".equals(renyuan) && null != renyuan){
                String[] strs = renyuan.split(",");
                for(String s : strs){
                    if(s.equalsIgnoreCase(hrmid)){
                        swsb = rs.getString("swsb");
                        swxb = rs.getString("swxb");
                        xwsb = rs.getString("xwsb");
                        xwxb = rs.getString("xwxb");
                    }
                }
            }
        }
        if("".equals(swsb) || null == swsb || "".equals(swxb) || null == swxb || "".equals(xwsb) || null == xwsb || "".equals(xwxb) || null == xwxb) {
            rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb from "+ybsjtable+" a, "+ybsjtable+"_dt1 b where a.id = b.mainid and b.xq = '" + num + "' and b.bm = '"+depid+"' and a.kssxrq <= '"+signdate+"' and a.jssxrq >= '"+signdate+"' and renyuan is null ");
            while(rs.next()){
                swsb = rs.getString("swsb");
                swxb = rs.getString("swxb");
                xwsb = rs.getString("xwsb");
                xwxb = rs.getString("xwxb");
            }
        }
        String am_sbsj = rs.getString("swsb");
        String pm_xbxb = rs.getString("xwxb");
        if ("".equals(am_sbsj) || null == am_sbsj) {
            am_sbsj = "08:30";
        }
        if ("".equals(pm_xbxb) || null == pm_xbxb) {
            pm_xbxb = "17:30";
        }
        //1. 签到和签退只要有一个在30分钟以外，旷工
        boolean one = judgeKGTime(am_sbsj,begintime);
        boolean two = judgeKGTime(endtime,pm_xbxb);
        if(one || two) {
            return "旷工";
        }
        boolean three = judgeCDTime(begintime,am_sbsj);
        boolean four = judgeZTTime(pm_xbxb,endtime);
        if(three && four) {
            return "迟到、早退";
        }
        if(three){
            return "迟到";
        }
        if(four){
            return "早退";
        }
        return "";
    }

    /**
     * 判断上班或者下班时间是否旷工
     *
     * @param am_sbsj
     * @param begintime
     * @return
     */
    public static boolean judgeKGTime(String time1, String time2) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(s.parse(time2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ca.add(Calendar.MINUTE, -30);
        Date tmptime = ca.getTime();
        try {
            if(tmptime.getTime() > s.parse(time1).getTime()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断迟到
     *
     * @param am_sbsj
     * @param begintime
     * @return
     */
    public static boolean judgeCDTime(String sbsj, String standardtime) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(s.parse(standardtime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ca.add(Calendar.MINUTE, +30);
        Date tmptime = ca.getTime();
        try {
            if(s.parse(sbsj).getTime() < tmptime.getTime() && (s.parse(sbsj).getTime() > s.parse(standardtime).getTime())) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断早退
     *
     * @param am_sbsj
     * @param begintime
     * @return
     */
    public static boolean judgeZTTime(String standardtime, String xbsj) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(s.parse(standardtime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ca.add(Calendar.MINUTE, -30);
        Date tmptime = ca.getTime();
        try {
            if(s.parse(xbsj).getTime() > tmptime.getTime() && (s.parse(xbsj).getTime() < s.parse(standardtime).getTime())) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
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
     * 获取当月最后一天
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getLastDayOfCurrentMonth(String date) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        // 获取当前月最后一天
        java.util.Calendar ca = java.util.Calendar.getInstance();
        try {
            ca.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ca.set(java.util.Calendar.DAY_OF_MONTH,
                ca.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        return last;
    }

    /**
     * 判断时间
     *
     * @param time
     * @param standardtime
     * @return
     */
    public boolean pssj(String time, String standardtime) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        try {
            long t = s.parse(time).getTime();
            long st = s.parse(standardtime).getTime();
            if (t > st) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断时间
     *
     * @param time
     * @param standardtime
     * @return
     */
    public boolean pssj2(String time, String standardtime) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        try {
            long t = s.parse(time).getTime();
            long st = s.parse(standardtime).getTime();
            if (t < st) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    static String test(String begintime, String endtime){
        String am_sbsj = "";
        String pm_xbxb = "";
        if ("".equals(am_sbsj) || null == am_sbsj) {
            am_sbsj = "08:30";
        }
        if ("".equals(pm_xbxb) || null == pm_xbxb) {
            pm_xbxb = "17:30";
        }
        //1. 签到和签退只要有一个在30分钟以外，旷工
        boolean one = judgeKGTime(am_sbsj,begintime);
        boolean two = judgeKGTime(endtime,pm_xbxb);
        if(one || two) {
            return "旷工";
        }
        boolean three = judgeCDTime(begintime,am_sbsj);
        boolean four = judgeZTTime(pm_xbxb,endtime);
        if(three && four) {
            return "迟到、早退";
        }
        if(three){
            return "迟到";
        }
        if(four){
            return "早退";
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(test("08:20","17:30"));
    }
}
