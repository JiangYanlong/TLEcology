package weaver.interfaces.jiangyl.kq;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;

public class TlHzkq extends BaseBean {

    /**
     * 根据部门和日期获取汇总信息
     * 
     * @param departmentid
     * @param date
     * @return
     */
    public Map<String, TlHzMode> getAm(String departmentid,
                              String date) {
        Map<String, TlHzMode> map = new HashMap<String, TlHzMode>();
        String tableName = getPropValue("TLConn", "kqhztable");
        String beginDate = date + "-01";
        // 当月最后一天
        String enddate = getLastDayOfCurrentMonth(beginDate);
        String getAllSQL = "select a.hrmid,a.signdate,a.kqlx,a.signtime,a.signlx,a.sjhj,a.bjhj,a.cjhj,a.njhj,a.hjhj,a.txhj,a.jdjhj,a.srjhj,a.sangjhj,a.pcjhj,a.brjhj,a.tqjhj " + "from " + tableName + " a,hrmresource b " + "where a.hrmid = b.id and a.signlx = '0' and b.departmentid = '" + departmentid + "' and a.signdate between '" + beginDate + "' and '" + enddate + "'";
        writeLog("getAllSQL :" + getAllSQL);
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
                Map<String,String> tmpmap = tl.getMap();
                String day = getDayOfDate(signdate);
                if (isWeekend(signdate)) {
                    tmpmap.put(day, "");
                } else {
                    String tsjhj = tl.getSjhj();
                    if("".equals(tsjhj) || null == tsjhj) {
                        tsjhj = "0";
                    }
                    String tbjhj = tl.getBjhj();
                    if("".equals(tbjhj) || null == tbjhj) {
                        tbjhj = "0";
                    }
                    String tcjhj = tl.getCjhj();
                    if("".equals(tcjhj) || null == tcjhj) {
                        tcjhj = "0";
                    }
                    String tnjhj = tl.getNjhj();
                    if("".equals(tnjhj) || null == tnjhj) {
                        tnjhj = "0";
                    }
                    String thjhj = tl.getHjhj();
                    if("".equals(thjhj) || null == thjhj) {
                        thjhj = "0";
                    }
                    String ttxhj = tl.getTxhj();
                    if("".equals(ttxhj) || null == ttxhj) {
                        ttxhj = "0";
                    }
                    
                    String tjdjhj = tl.getJdjhj();
                    if("".equals(tjdjhj) || null == tjdjhj) {
                        tjdjhj = "0";
                    }
                    String tsrjhj = tl.getSrjhj();
                    if("".equals(tsrjhj) || null == tsrjhj) {
                        tsrjhj = "0";
                    }
                    String tsangjhj = tl.getSangjhj();
                    if("".equals(tsangjhj) || null == tsangjhj) {
                        tsangjhj = "0";
                    }
                    String tpcjhj = tl.getPcjhj();
                    if("".equals(tpcjhj) || null == tpcjhj) {
                        tpcjhj = "0";
                    }
                    String tbrjhj = tl.getBrjhj();
                    if("".equals(tbrjhj) || null == tbrjhj) {
                        tbrjhj = "0";
                    }
                    String ttqjhj = tl.getTqjhj();
                    if("".equals(ttqjhj) || null == ttqjhj) {
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
    public Map<String, TlHzMode> getPm(String departmentid,
                              String date) {
        Map<String, TlHzMode> map = new HashMap<String, TlHzMode>();
        String tableName = getPropValue("TlConn", "kqhztable");
        String beginDate = date + "-01";
        // 当月最后一天
        String enddate = getLastDayOfCurrentMonth(beginDate);
        String getAllSQL = "select a.hrmid,a.signdate,a.kqlx,a.signtime,a.signlx,a.sjhj,a.bjhj,a.cjhj,a.njhj,a.hjhj,a.txhj,a.jdjhj,a.srjhj,a.sangjhj,a.pcjhj,a.brjhj,a.tqjhj " + "from " + tableName + " a,hrmresource b " + "where a.hrmid = b.id and a.signlx = '1' and b.departmentid = '" + departmentid + "' and a.signdate between '" + beginDate + "' and '" + enddate + "'";
        writeLog("getAllSQL2 :" + getAllSQL);
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
                Map<String,String> tmpmap = tl.getMap();
                String day = getDayOfDate(signdate);
                if (isWeekend(signdate)) {
                    tmpmap.put(day, "");
                } else {
                    String tsjhj = tl.getSjhj();
                    if("".equals(tsjhj) || null == tsjhj) {
                        tsjhj = "0";
                    }
                    String tbjhj = tl.getBjhj();
                    if("".equals(tbjhj) || null == tbjhj) {
                        tbjhj = "0";
                    }
                    String tcjhj = tl.getCjhj();
                    if("".equals(tcjhj) || null == tcjhj) {
                        tcjhj = "0";
                    }
                    String tnjhj = tl.getNjhj();
                    if("".equals(tnjhj) || null == tnjhj) {
                        tnjhj = "0";
                    }
                    String thjhj = tl.getHjhj();
                    if("".equals(thjhj) || null == thjhj) {
                        thjhj = "0";
                    }
                    String ttxhj = tl.getTxhj();
                    if("".equals(ttxhj) || null == ttxhj) {
                        ttxhj = "0";
                    }
                    String tjdjhj = tl.getJdjhj();
                    if("".equals(tjdjhj) || null == tjdjhj) {
                        tjdjhj = "0";
                    }
                    String tsrjhj = tl.getSrjhj();
                    if("".equals(tsrjhj) || null == tsrjhj) {
                        tsrjhj = "0";
                    }
                    String tsangjhj = tl.getSangjhj();
                    if("".equals(tsangjhj) || null == tsangjhj) {
                        tsangjhj = "0";
                    }
                    String tpcjhj = tl.getPcjhj();
                    if("".equals(tpcjhj) || null == tpcjhj) {
                        tpcjhj = "0";
                    }
                    String tbrjhj = tl.getBrjhj();
                    if("".equals(tbrjhj) || null == tbrjhj) {
                        tbrjhj = "0";
                    }
                    String ttqjhj = tl.getTqjhj();
                    if("".equals(ttqjhj) || null == ttqjhj) {
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
    public String getLxOfKQ(String str) {
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
            return "生日假";
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
    public String getDayOfDate(String date) {
        if(date.split("-")[2].startsWith("0")) {
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
    public String getHrmResourceName(String hrmid) {
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
     * 获取当月最后一天
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getLastDayOfCurrentMonth(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        return last;
    }

    public static void main(String[] args) {
        System.out.println(getLastDayOfCurrentMonth("2016-02-01"));
    }
}
