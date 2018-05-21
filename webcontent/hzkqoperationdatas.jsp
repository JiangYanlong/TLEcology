<%@page import="java.text.DecimalFormat,java.util.*,java.util.Map.*"%>
<%@ page language="java" contentType="text/html; charset=GBK" %>
<%@page import="weaver.hrm.resource.ResourceComInfo"%>
<%@page import="weaver.conn.RecordSet"%>
<%
request.setCharacterEncoding("UTF-8");
%>
<%@ include file="/systeminfo/init.jsp" %>
<%@ page import="weaver.file.ExcelStyle" %>
<%@ page import="weaver.file.ExcelSheet" %>
<%@ page import="weaver.file.ExcelRow" %>
<jsp:useBean id="ExcelFile" class="weaver.file.ExcelFile" scope="session"/>
<jsp:useBean id="TlHzkq" class="weaver.interfaces.jiangyl.kq.TlHzkq" scope="page"/>
<jsp:useBean id="DepartmentComInfo" class="weaver.hrm.company.DepartmentComInfo" scope="page"/>
<%
if(!HrmUserVarify.checkUserRight("YCBBCK:View", user)){
    	response.sendRedirect("/notice/noright.jsp");
    	return;
}
int userId = user.getUID();
String departmentName = Util.null2String(request.getParameter("department"));
String fromdate2 = Util.null2String(request.getParameter("fromdate2"));
String departChname = DepartmentComInfo.getDepartmentname(departmentName);
String fileName = departChname + fromdate2 + "考勤汇总情况确认表";

Map<String, weaver.interfaces.jiangyl.kq.TlHzMode> amMap = TlHzkq.getAm(departmentName,fromdate2);
new weaver.general.BaseBean().writeLog("amMap:" + amMap.toString());
System.out.println("amMap:" + amMap.toString());
Map<String, weaver.interfaces.jiangyl.kq.TlHzMode> pmMap = TlHzkq.getPm(departmentName,fromdate2);
new weaver.general.BaseBean().writeLog("pmMap:" + pmMap.toString());
System.out.println("pmMap:" + pmMap.toString());
String firstDay = fromdate2 + "-01";
int days = getDaysOfSpecialMonth(firstDay);

%>

<%!

/**
 * 获取一个月多少天数
 *
 * @parameter date
 */
public int getDaysOfSpecialMonth(String date) {
	java.text.SimpleDateFormat s = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.util.Calendar calendar = java.util.Calendar.getInstance(); 
	try {
		calendar.setTime(s.parse(date));
	} catch (java.text.ParseException e) {
		e.printStackTrace();
	}
	calendar.set(calendar.get(java.util.Calendar.YEAR),calendar.get(java.util.Calendar.MONTH),1); 
	calendar.roll(java.util.Calendar.DATE, false); 
	return calendar.get(java.util.Calendar.DATE);
}

/**
 * 判断一个日历是不是周末.
 *
 * @param calendar
 *            the calendar
 * @return true, if checks if is weekend
 */
private static boolean isWeekend(String dateString) {
	java.text.SimpleDateFormat s = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.util.Date date = null;
	try {
		date = s.parse(dateString);
	} catch (java.text.ParseException e) {
		e.printStackTrace();
	}
	java.util.Calendar calendar = new java.util.GregorianCalendar();
	calendar.setTime(date);
	//判断是星期几
	int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);

	if (dayOfWeek == 1 || dayOfWeek == 7) {
		return true;
	}
	return false;
}

/**
 * judge date is holiday or not
 * 
 * @param date
 * @return
 */
public java.util.List<String> getHolidayList() {
	java.util.List<String> list = new java.util.ArrayList<String>();
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	rs.execute("select holidaydate from HrmPubHoliday");
	while(rs.next()) {
		String day = rs.getString("holidaydate");
		list.add(day);
	}
	return list;
}

/**
 * 根据用户获取顶级ID
 */
public String getDJDepartmentId(int userId) {
	String djId = "";
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	new weaver.general.BaseBean().writeLog("获取人员ID：" + userId);
	rs.execute("select * from hrmresource where id = '"+userId+"'");
	new weaver.general.BaseBean().writeLog("根据人员ID获取部门：" + "select * from hrmresource where id = '"+userId+"'");
	rs.next();
	String depId = rs.getString("departmentid");
	while(!"0".equals(depId)) {
		rs.execute("select supdepid from hrmdepartment where id = '"+depId+"'");
		new weaver.general.BaseBean().writeLog("根据部门ID获取上级部门：" + "select supdepid from hrmdepartment where id = '"+depId+"'");
		rs.next();
		String depIds = rs.getString("supdepid");
		new weaver.general.BaseBean().writeLog("上级部门ID：" + depIds);
		if("0".equals(depIds)) {
			djId = depId;
			break;
		} else {
			depId = depIds;
		}
	}
	djId = depId;
	new weaver.general.BaseBean().writeLog("获取顶级ID：" + djId);
	return djId;
}

public static boolean getAll(String departmentid,int userid) {
	if(userid == 1) {
		return true;
	}
	new weaver.general.BaseBean().writeLog("departmentid:"+departmentid);
	new weaver.general.BaseBean().writeLog("userid:"+userid);
	java.util.List<String> list = new java.util.ArrayList<String>();
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	String sql = "select * from formtable_main_194 where gly = '"+userid+"'";
	rs.execute(sql);
	rs.next();
	String cknr = rs.getString("cknr");
	if(!"".equals(cknr) && null != cknr) {
		String[] strs = cknr.split(",");
		for(String s : strs) {
			list.add(s);
		}
	}
	new weaver.general.BaseBean().writeLog("list:"+list.toString());
	if (list.contains(departmentid)) {
		return true;
	}
	return false;
}

/**
 * 根据部门ID和人员ID判断所选部门是否属于该管理员所在中心
 */
public boolean isRight(String depId,int userId){
	String djid = getDJDepartmentId(userId);
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	rs.execute("select * from hrmdepartment where supdepid = '"+djid+"'");
	java.util.List<String> list = new java.util.ArrayList<String>();
	while(rs.next()) {
		String ids = rs.getString("id");
		list.add(ids);
	}
	list.add(djid);
	if (list.contains(depId)) {
		return true;
	}
	return false;
}
%>
<%
boolean isRight = getAll(departmentName,userId);
if (!isRight) {
	%>
		<table border=0 width="100%">
			<tr>
				<td align="center" ><font size=4><b>您所选部门不在可查询范围之内</b></font></td>
			<tr>
		</table>
	<%
	return;
}
%>
<table  border=0 width="100%" >
<tbody>
<tr>
  <td align="center" ><font size=4><b><%=fileName%></b></font></td>
</tr>
</tbody>
</table>
<div style="margin:0 auto">
<table  border=1  bordercolor=black style="border-collapse:collapse;" width="100%" align="center" >
  <COLGROUP>
  <COL width="4%">
  <%
  for(int i = 1; i <= days; i++) {
	
	%>
		<COL width="2%">
	<%
  }
  %>
  <COL width="4%">
  <COL width="4%">
  <COL width="4%">
  <COL width="4%">
  <COL width="4%">
  <COL width="4%">
  <COL width="4%">
<tbody>
<tr>
  <td align="center">姓名</td>
  <%
  for(int j = 1; j <= days; j++) {
	
	%>
		<td align="center"><%=j%></td>
	<%
  }
  %>
  <td align="center">事假合计</td>
  <td align="center">病假合计</td>
  <td align="center">产假合计</td>
  <td align="center">年假合计</td>
  <td align="center">婚假合计</td>
  <td align="center">调休合计</td>
  <td align="center">积分合计</td>
  <td align="center">生日合计</td>
  <td align="center">丧假合计</td>
  <td align="center">陪产合计</td>
  <td align="center">哺乳合计</td>
  <td align="center">探亲合计</td>
  <td align="center">签字确认</td>
</tr>
<%
java.util.List<String> holidayList = getHolidayList();
for(Entry<String,weaver.interfaces.jiangyl.kq.TlHzMode> entry : amMap.entrySet()){
	String key = entry.getKey();
	weaver.interfaces.jiangyl.kq.TlHzMode tl = entry.getValue();
	String name = tl.getName();
	java.util.Map map = tl.getMap();
	%>
<tr>
<td align="center" rowSpan="2"><%=name%></td>
	<%
	for(int j = 1; j <= days; j++) {
		String dateStr = "";
		if (String.valueOf(j).length() == 1) {
			dateStr = fromdate2 + "-0" + String.valueOf(j);
		}else {
			dateStr = fromdate2 + "-" + String.valueOf(j);
		}
		if(isWeekend(dateStr) || holidayList.contains(dateStr)) {
		%>
		<td align="center"></td>
		<%
		continue;
		}
		if(map.containsKey(String.valueOf(j))) {
			String dv = (String)map.get(String.valueOf(j));
			%>
			<td align="center"><%=dv%></td>
			<%
		}else {
			%>
			<td align="center">旷工</td>
			<%
		}
	}
	String sjhj = tl.getSjhj();
	String bjhj = tl.getBjhj();
	String cjhj = tl.getCjhj();
	String njhj = tl.getNjhj();
	String hjhj = tl.getHjhj();
	String txhj = tl.getTxhj();
	
	String jdjhj = tl.getJdjhj();
	String srjhj = tl.getSrjhj();
	String sangjhj = tl.getSangjhj();
	String pcjhjs = tl.getPcjhj();
	String brjhj = tl.getBrjhj();
	String tqjhj = tl.getTqjhj();
	%>
	<td align="center"><%=sjhj%></td>
	<td align="center"><%=bjhj%></td>
	<td align="center"><%=cjhj%></td>
	<td align="center"><%=njhj%></td>
	<td align="center"><%=hjhj%></td>
	<td align="center"><%=txhj%></td>
	
	<td align="center"><%=jdjhj%></td>
	<td align="center"><%=srjhj%></td>
	<td align="center"><%=sangjhj%></td>
	<td align="center"><%=pcjhjs%></td>
	<td align="center"><%=brjhj%></td>
	<td align="center"><%=tqjhj%></td>
	
	<td align="center" rowSpan="2"></td>
</tr>
<tr>
	<%
	if(pmMap.containsKey(key)) {
		weaver.interfaces.jiangyl.kq.TlHzMode ptl = pmMap.get(key);
		java.util.Map pmap = ptl.getMap();
		for(int n = 1; n <= days; n++) {
			String dateStr = "";
			if (String.valueOf(n).length() == 1) {
				dateStr = fromdate2 + "-0" + String.valueOf(n);
			}else {
				dateStr = fromdate2 + "-" + String.valueOf(n);
			}
			if(isWeekend(dateStr) || holidayList.contains(dateStr)) {
			%>
			<td align="center"></td>
			<%
			continue;
			}
			if(pmap.containsKey(String.valueOf(n))) {
				String pdv = (String)pmap.get(String.valueOf(n));
				%>
					<td align="center"><%=pdv%></td>
				<%
			}else {
				%>
				<td align="center">旷工</td>
				<%
			}
		}
		String psjhj = ptl.getSjhj();
		String pbjhj = ptl.getBjhj();
		String pcjhj = ptl.getCjhj();
		String pnjhj = ptl.getNjhj();
		String phjhj = ptl.getHjhj();
		String ptxhj = ptl.getTxhj();
		String jdjhjs = ptl.getJdjhj();
		String srjhjs = ptl.getSrjhj();
		String sangjhjs = ptl.getSangjhj();
		String pcjhjss = ptl.getPcjhj();
		String brjhjs = ptl.getBrjhj();
		String tqjhjs = ptl.getTqjhj();
		%>
		<td align="center"><%=psjhj%></td>
		<td align="center"><%=pbjhj%></td>
		<td align="center"><%=pcjhj%></td>
		<td align="center"><%=pnjhj%></td>
		<td align="center"><%=phjhj%></td>
		<td align="center"><%=ptxhj%></td>
		
		<td align="center"><%=jdjhjs%></td>
		<td align="center"><%=srjhjs%></td>
		<td align="center"><%=sangjhjs%></td>
		<td align="center"><%=pcjhjss%></td>
		<td align="center"><%=brjhjs%></td>
		<td align="center"><%=tqjhjs%></td>
		<%
	}else {
		for(int m = 1; m <= days; m++) {
			String dateStr = "";
			if (String.valueOf(m).length() == 1) {
				dateStr = fromdate2 + "-0" + String.valueOf(m);
			}else {
				dateStr = fromdate2 + "-" + String.valueOf(m);
			}
			if(isWeekend(dateStr) || holidayList.contains(dateStr)) {
			%>
			<td align="center"></td>
			<%
			}else {			
			%>
			<td align="center">旷工</td>
			<%
			}
		}
		%>
	<td align="center">0</td>
	<td align="center">0</td>
	<td align="center">0</td>
	<td align="center">0</td>
	<td align="center">0</td>
	<td align="center">0</td>
		<%
	}
%>
</tr>
<%
}
%>
</tbody>
</table>
</div>