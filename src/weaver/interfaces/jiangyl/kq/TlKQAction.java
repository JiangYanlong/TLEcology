package weaver.interfaces.jiangyl.kq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

public class TlKQAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        Property[] properties = request.getMainTableInfo().getProperty();// 获取表单主字段信息
        // 主表数据字段
        String resource1 = "";// 流程申请人
        String qjlb = ""; // 休假类别（请假 1 调休 0）
        String qjly = ""; // 调休类型（调休 0 ，三八 2，出差调休 1）
        String absencetype1 = ""; // 请假类型
        String begindate = ""; // 休假开始日期
        String begintime = ""; // 休假开始时间
        String enddate = ""; // 休假结束日期
        String endtime = ""; // 休假结束时间

        for (int i = 0; i < properties.length; i++) {
            String name = properties[i].getName();// 主字段名称
            String value = Util.null2String(properties[i].getValue());// 主字段对应的值
            if ("qjlb".equals(name)) {
                qjlb = value;
            }
            if ("qjly".equals(name)) {
                qjly = value;
            }
            if ("absencetype1".equals(name)) {
                absencetype1 = value;
            }
            if ("begindate".equals(name)) {
                begindate = value;
            }
            if ("begintime".equals(name)) {
                begintime = value;
            }
            if ("enddate".equals(name)) {
                enddate = value;
            }
            if ("endtime".equals(name)) {
                endtime = value;
            }
            if ("resource1".equals(name)) {
                resource1 = value;
            }
        }
        List<TlKQMode> list = get(resource1, begindate, begintime, enddate, endtime);
        if ("0".equals(qjlb)) {
            qjly = trunslate(qjlb, qjly);
            inserttx(list, qjly);
        }
        if ("1".equals(qjlb)) {
            absencetype1 = trunslate(qjlb, absencetype1);
            insertqj(list, absencetype1);
        }
        return SUCCESS;
    }

    /**
     * 插入请假记录
     *
     * @param list
     * @param absencetype1
     */
    public void insertqj(List<TlKQMode> list,
                         String absencetype1) {
        RecordSet rs = new RecordSet();
        String tableName = getPropValue("TLConn", "kqhztable");
        if (list.isEmpty()) {
            writeLog("没有数据处理。");
            return;
        }
        for (TlKQMode t : list) {
            String sxw = t.getSxw();
            String total = t.getTotal();
            String date = t.getDate();
            String hrmid = t.getHrmid();
            String sql = "";
            //查询是否已经有打卡记录
            String checkExistSQL = "select * from " + tableName + " where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
            writeLog("调休流程查询是否有打卡记录SQL：" + checkExistSQL);
            rs.execute(checkExistSQL);
            rs.next();
            if (rs.getCounts() > 0) {
                if ("3".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', bjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("11".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', cjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("14".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', brjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("16".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', srjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("22".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', tqjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("23".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', jdjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("5".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', bjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("4".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', sjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("10".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', hjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("6".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', cjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("12".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', pcjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("9".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', sangjhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
                if ("13".equals(absencetype1)) {
                    sql = "update " + tableName + " set kqlx = '" + absencetype1 + "', njhj = '" + total + "' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                }
            } else {
                if ("3".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','" + total + "','0','0','0','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("11".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','" + total + "','0','0','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("14".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','0','0','0','0','0','0','" + total + "','0','221','" + hrmid
                            + "','0')";
                }
                if ("16".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','0','0','0','" + total + "','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("22".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','0','0','0','0','0','0','0','" + total + "','221','" + hrmid
                            + "','0')";
                }
                if ("23".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','0','0','" + total + "','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("5".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','" + total + "','0','0','0','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("4".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','" + total + "','0','0','0','0','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("10".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','" + total + "','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("6".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','" + total + "','0','0','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("12".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','" + total + "','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("9".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','0','0','0','0','" + total + "','0','0','0','221','" + hrmid
                            + "','0')";
                }
                if ("13".equals(absencetype1)) {
                    sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + absencetype1 + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','" + total + "','0','0','0','0','0','0','0','0','221','" + hrmid
                            + "','0')";
                }
            }
            rs.execute(sql);
        }

    }

    /**
     * 插入调休记录
     *
     * @param list
     * @param qjly
     */
    public void inserttx(List<TlKQMode> list,
                         String qjly) {
        RecordSet rs = new RecordSet();
        String tableName = getPropValue("TLConn", "kqhztable");
        if (list.isEmpty()) {
            writeLog("没有数据处理。");
            return;
        }
        for (TlKQMode t : list) {
            String sxw = t.getSxw();
            String total = t.getTotal();
            String date = t.getDate();
            String hrmid = t.getHrmid();
            //查询是否已经有打卡记录
            String checkExistSQL = "select * from " + tableName + " where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
            writeLog("调休流程查询是否有打卡记录SQL：" + checkExistSQL);
            rs.execute(checkExistSQL);
            rs.next();
            if (rs.getCounts() > 0) {
                String updateSQL = "update " + tableName + " set txhj = '" + total + "',kqlx = '"+qjly+"' where hrmid = '" + hrmid + "' and signdate = '" + date + "' and signlx = '" + sxw + "'";
                writeLog("调休流程更改已经存在的打卡记录SQL：" + updateSQL);
                rs.execute(updateSQL);
            } else {
                String sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " + "values('" + hrmid + "','" + date + "','" + qjly + "','" + "" + "','" + sxw + "','" + "" + "','0','0','0','0','0','" + total + "','0','0','0','0','0','0','221','" + hrmid
                        + "','0')";
                rs.execute(sql);
            }
        }
    }

    /**
     * 转换下拉列表值
     *
     * @param flag
     * @param str
     * @return
     */
    public String trunslate(String flag,
                            String str) {
        if ("0".equals(flag)) {
            if ("0".equals(str)) {
                return "1";
            }
            if ("2".equals(str)) {
                return "15";
            }
            if ("3".equals(str)) {
                return "2";
            }
        }
        if ("1".equals(flag)) {
            if ("2".equals(str)) {
                return "3";
            }
            if ("8".equals(str)) {
                return "11";
            }
            if ("9".equals(str)) {
                return "14";
            }
            if ("13".equals(str)) {
                return "14";
            }
            if ("10".equals(str)) {
                return "16";
            }
            if ("11".equals(str)) {
                return "22";
            }
            if ("12".equals(str)) {
                return "23";
            }
            if ("1".equals(str)) {
                return "4";
            }
            if ("0".equals(str)) {
                return "5";
            }
            if ("3".equals(str)) {
                return "10";
            }
            if ("4".equals(str)) {
                return "6";
            }
            if ("5".equals(str)) {
                return "12";
            }
            if ("6".equals(str)) {
                return "9";
            }
            if ("7".equals(str)) {
                return "13";
            }
        }
        return "";
    }

    /**
     * 根据开始日期、开始时间、结束日期、结束时间计算出来日期和小时数
     *
     * @param hrmid
     * @param begindate
     * @param begintime
     * @param enddate
     * @param endtime
     * @return
     */
    public List<TlKQMode> get(String hrmid,
                              String begindate,
                              String begintime,
                              String enddate,
                              String endtime) {
        List<TlKQMode> mode = new ArrayList<TlKQMode>();
        if (begindate.equals(enddate)) {
            TlTimeMode timemode = getStandardTime(begindate, hrmid);
            if (getDateWithStr(begintime, "HH:mm").before(getDateWithStr(timemode.getAm_xbsj(), "HH:mm"))) {
                TlKQMode t = new TlKQMode();
                t.setDate(begindate);
                t.setSxw("0");
                t.setHrmid(hrmid);
                t.setTotal(getTotalTime(timemode.getAm_sbsj(), timemode.getAm_xbsj()));
                mode.add(t);
                if (getDateWithStr(endtime, "HH:mm").after(getDateWithStr(timemode.getPm_sbsj(), "HH:mm"))) {
                    TlKQMode t1 = new TlKQMode();
                    t1.setDate(begindate);
                    t1.setSxw("1");
                    t1.setHrmid(hrmid);
                    t1.setTotal(getTotalTime(timemode.getPm_sbsj(), timemode.getPm_xbsj()));
                    mode.add(t1);
                }
            } else {
                TlKQMode t1 = new TlKQMode();
                t1.setDate(begindate);
                t1.setSxw("1");
                t1.setHrmid(hrmid);
                t1.setTotal(getTotalTime(timemode.getPm_sbsj(), timemode.getPm_xbsj()));
                mode.add(t1);
            }
        } else {
            String tempbegindate = "";
            while (getDateWithStr(begindate, "yyyy-MM-dd").before(getDateWithStr(enddate, "yyyy-MM-dd"))) {
                TlTimeMode timemode = getStandardTime(begindate, hrmid);
                if (getDateWithStr(begintime, "HH:mm").before(getDateWithStr(timemode.getAm_xbsj(), "HH:mm"))) {
                    TlKQMode t = new TlKQMode();
                    t.setDate(begindate);
                    t.setSxw("0");
                    t.setHrmid(hrmid);
                    t.setTotal(getTotalTime(timemode.getAm_sbsj(), timemode.getAm_xbsj()));
                    mode.add(t);
                    //if (getDateWithStr(endtime, "HH:mm").after(getDateWithStr(timemode.getPm_sbsj(), "HH:mm"))) {
                    TlKQMode t1 = new TlKQMode();
                    t1.setDate(begindate);
                    t1.setSxw("1");
                    t1.setHrmid(hrmid);
                    t1.setTotal(getTotalTime(timemode.getPm_sbsj(), timemode.getPm_xbsj()));
                    mode.add(t1);
                    //}
                } else {
                    if("".equals(tempbegindate)){
                        TlKQMode t1 = new TlKQMode();
                        t1.setDate(begindate);
                        t1.setSxw("1");
                        t1.setHrmid(hrmid);
                        t1.setTotal(getTotalTime(timemode.getPm_sbsj(), timemode.getPm_xbsj()));
                        mode.add(t1);
                    } else {
                        TlKQMode t = new TlKQMode();
                        t.setDate(begindate);
                        t.setSxw("0");
                        t.setHrmid(hrmid);
                        t.setTotal(getTotalTime(timemode.getAm_sbsj(), timemode.getAm_xbsj()));
                        mode.add(t);
                        TlKQMode t1 = new TlKQMode();
                        t1.setDate(begindate);
                        t1.setSxw("1");
                        t1.setHrmid(hrmid);
                        t1.setTotal(getTotalTime(timemode.getPm_sbsj(), timemode.getPm_xbsj()));
                        mode.add(t1);
                    }
                }
                tempbegindate = begindate;
                begindate = getIncomeDate(getDateWithStr(begindate, "yyyy-MM-dd"), 1);
            }
            TlTimeMode timemode = getStandardTime(enddate, hrmid);
            writeLog("timemode : " + timemode.toString());
            writeLog("enddate : " + enddate);
            if (getDateWithStr(endtime, "HH:mm").before(getDateWithStr(timemode.getAm_sbsj(), "HH:mm"))) {

            } else if ((getDateWithStr(endtime, "HH:mm").before(getDateWithStr(timemode.getPm_sbsj(), "HH:mm")) || endtime.equals(timemode.getPm_sbsj())) && getDateWithStr(endtime, "HH:mm").after(getDateWithStr(timemode.getAm_sbsj(), "HH:mm"))) {
                TlKQMode t = new TlKQMode();
                t.setDate(enddate);
                t.setSxw("0");
                t.setHrmid(hrmid);
                t.setTotal(getTotalTime(timemode.getAm_sbsj(), timemode.getAm_xbsj()));
                mode.add(t);
            } else {
                TlKQMode t = new TlKQMode();
                t.setDate(enddate);
                t.setSxw("0");
                t.setHrmid(hrmid);
                t.setTotal(getTotalTime(timemode.getAm_sbsj(), timemode.getAm_xbsj()));
                mode.add(t);
                TlKQMode t1 = new TlKQMode();
                t1.setDate(enddate);
                t1.setSxw("1");
                t1.setHrmid(hrmid);
                t1.setTotal(getTotalTime(timemode.getPm_sbsj(), timemode.getPm_xbsj()));
                mode.add(t1);
            }
        }
        return mode;
    }

    /**
     * 获取后一天时间
     *
     * @param date
     * @param flag
     * @return
     * @throws NullPointerException
     */
    public static String getIncomeDate(Date date,
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
     * 根据时间和人员id获取一般时间
     *
     * @param date
     * @param hrmid
     * @return
     */
    public TlTimeMode getStandardTime(String date,
                                      String hrmid) {
        ResourceComInfo r = null;
        try {
            r = new ResourceComInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String departmentid = r.getDepartmentID(hrmid);
        String number = getWeekOfDateNum(date);
        String special = getHolidaySpecialDay(date);
        if(!"".equals(special)){
            number = special;
        }
        return getTimeInfo(number, departmentid,date,hrmid);
    }

    public String getHolidaySpecialDay(String date){
        RecordSet rs = new RecordSet();
        rs.execute("select * from Hrmpubholiday where holidaydate = '"+date+"' and changetype = '2' ");
        String result = "";
        while(rs.next()){
            int relateweekday = rs.getInt("relateweekday");
            if(relateweekday == 1){
                result = "0";
            }else {
                result = String.valueOf(relateweekday -1);
            }
        }
        return result;
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
    public static TlTimeMode getTimeInfo(String week, String departmentId,String date,String hrmid) {
        RecordSet rs = new RecordSet();
        String swsb = "";
        String swxb = "";
        String xwsb = "";
        String xwxb = "";
        rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb,b.renyuan from formtable_main_173 a, formtable_main_173_dt1 b where a.id = b.mainid and b.xq = '" + week + "' and b.bm = '"+departmentId+"' and a.kssxrq <= '"+date+"' and a.jssxrq >= '"+date+"' and renyuan like '%"+hrmid+"%'");
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
        if("".equals(swsb) || null == swsb || "".equals(swxb) || null == swxb || "".equals(xwsb) || null == xwsb || "".equals(xwxb) || null == xwsb) {
            rs.execute("select b.swsb,b.swxb,b.xwsb,b.xwxb from formtable_main_173 a, formtable_main_173_dt1 b where a.id = b.mainid and b.xq = '" + week + "' and b.bm = '"+departmentId+"' and a.kssxrq <= '"+date+"' and a.jssxrq >= '"+date+"' and renyuan is null");
            while(rs.next()){
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
     * 获取两个时间相差小时数
     *
     * @param begintime
     *            开始日期
     * @param endtime
     *            结束日期
     * @return
     */
    public static String getTotalTime(String begintime,
                                      String endtime) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        SimpleDateFormat s1 = new SimpleDateFormat("HH:mm");
        String hour = "";
        try {
            long begin = s.parse(begintime).getTime();
            long end = s1.parse(endtime).getTime();
            hour = new BigDecimal(end - begin).divide(new BigDecimal(60 * 60 * 1000), 2, RoundingMode.HALF_DOWN).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hour;
    }

    /**
     *
     * @param qjlb
     * @param qjly
     * @param absencetype1
     * @param resource1
     * @param begindate
     * @param begintime
     * @param enddate
     * @param endtime
     */
    public void insert(String qjlb,
                       String qjly,
                       String absencetype1,
                       String resource1,
                       String begindate,
                       String begintime,
                       String enddate,
                       String endtime) {
        List<TlKQMode> list = get(resource1, begindate, begintime, enddate, endtime);
        if ("0".equals(qjlb)) {
            qjly = trunslate(qjlb, qjly);
            inserttx(list, qjly);
        }
        if ("1".equals(qjlb)) {
            absencetype1 = trunslate(qjlb, absencetype1);
            insertqj(list, absencetype1);
        }
    }

    public static void main(String[] args) {
        System.out.println(getWeekOfDateNum("2016-03-07"));
    }
}
