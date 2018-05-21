package weaver.interfaces.jiangyl.kq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.hrm.resource.ResourceComInfo;

public class TlYckq extends BaseBean {

	private static BaseBean bean = new BaseBean();
	//考勤汇总表
	public String kqhztable = bean.getPropValue("TLConn", "kqhztable");
	//一般时间对应的表
	public String ybsjtable = bean.getPropValue("TLConn", "ybsjtable");

	public Map<String, TlHrmMode> get(String date, String departdmentid) {
		// 当月第一天
		String begindate = date + "-01";
		// 当月最后一天
		String enddate = getLastDayOfCurrentMonth(begindate);

		RecordSet set = new RecordSet();
		RecordSet rs = new RecordSet();
		rs.execute("select a.hrmid,a.signdate,a.kqlx,a.signtime,a.cardid,b.loginid,b.lastname,b.departmentid  "
				+ "from "+kqhztable+" a, hrmresource b "
				+ "where a.kqlx in (18,19,20,21) and a.hrmid = b.id and a.signdate between '"
				+ begindate
				+ "' and '"
				+ enddate
				+ "' and b.departmentid in ("
				+ departdmentid + ")");
		Map<String, TlHrmMode> map = new HashMap<String, TlHrmMode>();
		while (rs.next()) {
			String lastname = rs.getString("lastname");
			String loginid = rs.getString("loginid");
			String signdate = rs.getString("signdate");
			String kqlx = rs.getString("kqlx");
			String cardid = rs.getString("cardid");
			String hrmid = rs.getString("hrmid");
			String signtime = rs.getString("signtime");
			String key = cardid + "-" + signdate;
			if (map.containsKey(key)) {
				String time = map.get(key).getMutil_date();
				time = time + "," + signtime;
				String lx = map.get(key).getLx();
				if ("18".equals(kqlx)) {
					kqlx = "迟到";
				}
				if ("19".equals(kqlx)) {
					kqlx = "早退";
				}
				if ("20".equals(kqlx)) {
					kqlx = "旷工";
				}
				if ("21".equals(kqlx)) {
					kqlx = "未带卡";
				}
				lx = lx + "," + kqlx;
				map.get(key).setMutil_date(time);
				map.get(key).setLx(lx);
			} else {
				TlHrmMode tl = new TlHrmMode();
				tl.setLastname(lastname);
				tl.setLoginid(loginid);
				tl.setSigndate(signdate);
				tl.setCardid(cardid);
				tl.setHrmid(hrmid);
				if ("18".equals(kqlx)) {
					kqlx = "迟到";
				}
				if ("19".equals(kqlx)) {
					kqlx = "早退";
				}
				if ("20".equals(kqlx)) {
					kqlx = "旷工";
				}
				if ("21".equals(kqlx)) {
					kqlx = "未带卡";
				}
				tl.setLx(kqlx);

				set.execute("select a.hrmid,a.signdate,a.kqlx,a.signtime,a.cardid from "+kqhztable+" a "
						+ "where a.signdate between '"
						+ signdate
						+ "' and '"
						+ signdate
						+ "' and a.hrmid = '"
						+ hrmid
						+ "' and a.cardid = '" + cardid + "' and a.kqlx = '0'");
				while(set.next()){
					String time1 = set.getString("signtime");
					signtime += "," + time1;
				}
//				String signUp = "";
//				String signOut = "";
//				if (!"".equals(time1) || null != time1) {
//					if (pssj(time1, "17:30")) {
//						signOut = time1;
//						tl.setYc_signout(signOut);
//					} else if (pssj2(time1, "08:30")) {
//						signUp = time1;
//						tl.setYc_signup(signUp);
//					}
//				}
				tl.setMutil_date(signtime);
				map.put(key, tl);
			}
		}
		Map<String, TlHrmMode> all = new HashMap<String, TlHrmMode>();
		for (Entry<String, TlHrmMode> entry : map.entrySet()) {
			String key = entry.getKey();
			TlHrmMode t = entry.getValue();
			if ("".equals(t.getMutil_date())) {
				continue;
			}
			String mul = t.getMutil_date();
			String[] strs = mul.split(",");
			Arrays.sort(strs);
			if(null == t.getYc_signup()) {
				t.setYc_signup(strs[0]);
			}
			if(null == t.getYc_signout()) {
				t.setYc_signout(strs[strs.length - 1]);
			}
			if(!t.getYc_signup().equals(t.getYc_signout())){
				String lx = getLx(t.getHrmid(),t.getSigndate(),t.getYc_signup(),t.getYc_signout());
				t.setLx(lx);
				all.put(key, t);
			}else {
				all.put(key, t);
			}
		}
		RecordSet setrs = new RecordSet();
		RecordSet setrs2 = new RecordSet();
		List<String> list = new ArrayList<String>();
		setrs.execute("select a.hrmid,a.signdate,a.kqlx,a.signtime,a.cardid,b.loginid,b.lastname,b.departmentid  "+
				"from "+kqhztable+" a, hrmresource b "+
				"where a.hrmid = b.id and a.signdate between '"+begindate+"' and '"+enddate+"' and b.departmentid = '"+departdmentid+"'");
		while (setrs.next()) {
			String hrmid = setrs.getString("hrmid");
			list.add(hrmid);
		}
		for (String s : list) {
			setrs.execute("select a.hrmid,a.signdate,a.kqlx,a.signtime,a.cardid,a.signlx,b.loginid,b.lastname,b.departmentid  "
					+ "from "+kqhztable+" a, hrmresource b "
					+ "where a.hrmid = b.id and a.signdate between '"
					+ begindate
					+ "' and '"
					+ enddate
					+ "' and b.departmentid = '"+departdmentid+"' and a.hrmid = '"+s+"'");
			while (setrs.next()) {
				String datestr = setrs.getString("signdate");
				setrs2.execute("select a.hrmid,a.signdate,a.kqlx,a.signtime,a.cardid,a.signlx,b.loginid,b.lastname,b.departmentid  "
						+ "from "+kqhztable+" a, hrmresource b "
						+ "where a.hrmid = b.id and a.signdate between '"
						+ datestr
						+ "' and '"
						+ datestr
						+ "' and b.departmentid = '"+departdmentid+"' and a.hrmid = '"+s+"'");
				while (setrs2.next()) {
					if (setrs2.getCounts() == 1) {
						String lastname = setrs2.getString("lastname");
						String loginid = setrs2.getString("loginid");
						String signdate = setrs2.getString("signdate");
						String signtime = setrs2.getString("signtime");
						String cardid = setrs2.getString("cardid");
						String signlx = setrs2.getString("signlx");
						String key = cardid + "-" + signdate;
						TlHrmMode t = new TlHrmMode();
						if (signlx == "0") {
							t.setCardid(cardid);
							t.setLastname(lastname);
							t.setLoginid(loginid);
							t.setSigndate(signdate);
							t.setLx("旷工");
							t.setYc_signup(signtime);
							t.setYc_signout("");
							all.put(key, t);
						} else {
							t.setCardid(cardid);
							t.setLastname(lastname);
							t.setLoginid(loginid);
							t.setSigndate(signdate);
							t.setLx("旷工");
							t.setYc_signup("");
							t.setYc_signout(signtime);
							all.put(key, t);
						}
					}
				}
			}
		}
		return all;
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
