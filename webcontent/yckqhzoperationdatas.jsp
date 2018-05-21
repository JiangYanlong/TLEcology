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
<jsp:useBean id="TlYckqhz" class="weaver.interfaces.jiangyl.kq.TlYckqhz" scope="page"/>
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
String fileName = departChname + fromdate2 + "�����쳣�������ȷ�ϱ�";
ExcelFile.init ();
ExcelFile.setFilename(fileName);
// ���潨��һ��ͷ������ʽ, ����ϵͳ�еı�ͷ�����������ʽ!
ExcelStyle excelStyle = ExcelFile.newExcelStyle("Header") ;
//excelStyle.setGroundcolor(ExcelStyle.WeaverHeaderGroundcolor) ;
excelStyle.setFontcolor(ExcelStyle.WeaverHeaderFontcolor) ;
excelStyle.setFontbold(ExcelStyle.WeaverHeaderFontbold) ;
excelStyle.setAlign(ExcelStyle.WeaverHeaderAlign) ;

ExcelSheet es = ExcelFile.newExcelSheet(fileName) ;

ExcelRow er  = es.newExcelRow() ;
er.addStringValue(fileName, "Header" ) ;

%>
<table  border=0 width="100%" >
<tbody>
<tr>
  <td align="center" ><font size=4><b><%=fileName%></b></font></td>
</tr>
</tbody>
</table>
<%!
/**
 * �ж�һ�������ǲ�����ĩ.
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
	//�ж������ڼ�
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
 * �����û���ȡ����ID
 */
public String getDJDepartmentId(int userId) {
	String djId = "";
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	new weaver.general.BaseBean().writeLog("��ȡ��ԱID��" + userId);
	rs.execute("select * from hrmresource where id = '"+userId+"'");
	new weaver.general.BaseBean().writeLog("������ԱID��ȡ���ţ�" + "select * from hrmresource where id = '"+userId+"'");
	rs.next();
	String depId = rs.getString("departmentid");
	while(!"0".equals(depId)) {
		rs.execute("select supdepid from hrmdepartment where id = '"+depId+"'");
		new weaver.general.BaseBean().writeLog("���ݲ���ID��ȡ�ϼ����ţ�" + "select supdepid from hrmdepartment where id = '"+depId+"'");
		rs.next();
		String depIds = rs.getString("supdepid");
		new weaver.general.BaseBean().writeLog("�ϼ�����ID��" + depIds);
		if("0".equals(depIds)) {
			djId = depId;
			break;
		} else {
			depId = depIds;
		}
	}
	djId = depId;
	new weaver.general.BaseBean().writeLog("��ȡ����ID��" + djId);
	return djId;
}

/**
 * ���ݲ���ID����ԱID�ж���ѡ�����Ƿ����ڸù���Ա��������
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
%>
<%
boolean isRight = getAll(departmentName,userId);
if (!isRight) {
	%>
		<table border=0 width="100%">
			<tr>
				<td align="center" ><font size=4><b>����ѡ���Ų��ڿɲ�ѯ��Χ֮��</b></font></td>
			<tr>
		</table>
	<%
	return;
}
%>
<div style="margin:0 auto">
<table  border=1  bordercolor=black style="border-collapse:collapse;" width="100%" align="center" >
  <COLGROUP>
  <COL width="6%">
  <COL width="6%">
  <COL width="6%">
  <COL width="6%">
  <COL width="6%">
  <COL width="6%">
  <COL width="6%">
  <COL width="6%">
<tbody>
<tr>
  <td align="center">����</td>
  <td align="center">����</td>
  <td align="center">��������</td>
  <td align="center">����</td>
  <td align="center">Ա��ǩ��</td>
</tr>
<%
java.util.List<String> holidayList = getHolidayList();
Map<String, weaver.interfaces.jiangyl.kq.TlYcMode> result = TlYckqhz.get(fromdate2,departmentName);
for(Entry<String,weaver.interfaces.jiangyl.kq.TlYcMode> entry : result.entrySet()){
	String key = entry.getKey();
	weaver.interfaces.jiangyl.kq.TlYcMode model = entry.getValue();
	String xmbh = model.getName();
	String xmmc = model.getLoginid();
	String xmqtbm = model.getSigndate();
	String type = model.getType();
	String xmlbyj = model.getCount();
	if(isWeekend(xmqtbm) || holidayList.contains(xmqtbm)) {
		continue;
	}
%>
<tr>
  <td align="center"><%=xmbh%></td>
  <td align="center"><%=xmmc%></td>
  <td align="center"><%=type%></td>
  <td align="center"><%=xmlbyj%></td>
  <td align="center"></td>
</tr>
<%    
 } 
%>
</tbody>
</table>
</div>