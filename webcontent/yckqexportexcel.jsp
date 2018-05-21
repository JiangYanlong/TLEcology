<%@ page language="java" contentType="text/html; charset=GBK" %> <!--added by xwj  for td2903  20051019-->
<%@ include file="/systeminfo/init.jsp" %>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<%@ page import="weaver.general.*,java.util.*,java.util.Map.*" %>
<%@ page import="weaver.file.*" %>
<%@ page import="weaver.teechart.*" %>
<jsp:useBean id="ExcelFile" class="weaver.file.ExcelFile" scope="session"/>
<HTML><HEAD>
<LINK href="/css/Weaver.css" type=text/css rel=STYLESHEET>
<SCRIPT language="javascript" src="/js/weaver.js"></script>
</head>
<body>
<%
if(!HrmUserVarify.checkUserRight("YCBBCK:View", user)){
    	response.sendRedirect("/notice/noright.jsp");
    	return;
}
%>
<%!
public String translateKXJSF(String context){
	java.math.BigDecimal b = new java.math.BigDecimal(context);
	return b.toPlainString();
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
int userId = user.getUID();
String departmentName = Util.null2String(request.getParameter("department"));
String fromdate2 = Util.null2String(request.getParameter("fromdate2"));

ExcelSheet es = new ExcelSheet();
ExcelStyle excelStyle = ExcelFile.newExcelStyle("Header");
excelStyle.setGroundcolor(ExcelStyle.WeaverHeaderGroundcolor);
excelStyle.setFontcolor(ExcelStyle.WeaverHeaderFontcolor);
excelStyle.setFontbold(ExcelStyle.WeaverHeaderFontbold);
excelStyle.setAlign(ExcelStyle.WeaverHeaderAlign);
excelStyle.setCellBorder(ExcelStyle.WeaverBorderThin);

ExcelRow title = es.newExcelRow();
title.setHight(20);
title.addStringValue("����", "title");
title.addStringValue("����", "title");
title.addStringValue("�쳣ʱ��", "title");
title.addStringValue("��������", "title");
title.addStringValue("�ϰ�ˢ��ʱ��", "title");
title.addStringValue("�°�ˢ��ʱ��", "title");
title.addStringValue("���ڿ���", "title");
title.addStringValue("Ա��ǩ��", "title");

weaver.interfaces.jiangyl.kq.TlYckq c = new weaver.interfaces.jiangyl.kq.TlYckq();
Map<String, weaver.interfaces.jiangyl.kq.TlHrmMode>  map = c.get(fromdate2,departmentName);

for(Entry<String,weaver.interfaces.jiangyl.kq.TlHrmMode> d : map.entrySet()){
	weaver.interfaces.jiangyl.kq.TlHrmMode model = d.getValue();
	String xmbh = model.getLastname();
	String xmmc = model.getLoginid();
	String xmqtbm = model.getSigndate();
	String xmlbyj = model.getLx();
	String xmlx = model.getYc_signup();
	String xmjl = model.getYc_signout();
	String cardid = model.getCardid();
	String qz = "";
	ExcelRow er = es.newExcelRow();
	er.addStringValue(xmbh,"normal");
	er.addStringValue(xmmc,"normal");
	er.addStringValue(xmqtbm,"normal");
	er.addStringValue(xmlbyj,"normal");
	er.addStringValue(xmlx,"normal");
	er.addStringValue(xmjl,"normal");
	er.addStringValue(cardid,"normal");
	er.addStringValue(qz,"normal");
}

ExcelStyle titleStyle = ExcelFile.newExcelStyle("title");
titleStyle.setGroundcolor(ExcelStyle.BLUE_Color);
titleStyle.setFontcolor(ExcelStyle.WHITE_Color);
titleStyle.setFontbold(ExcelStyle.Strong_Font);
titleStyle.setCellBorder(ExcelStyle.WeaverBorderThin);
titleStyle.setAlign(ExcelStyle.ALIGN_LEFT);

ExcelStyle normalStyle = ExcelFile.newExcelStyle("normal");
normalStyle.setValign(ExcelStyle.VALIGN_CENTER);

ExcelFile.init();
String titlename = new weaver.hrm.company.DepartmentComInfo().getDepartmentname(departmentName) + fromdate2 + "�����쳣���ȷ�ϱ�";
ExcelFile.setFilename(titlename);
ExcelFile.addSheet(titlename, es);
%>
<%
boolean isRight = getAll(departmentName,userId);
if (!isRight) {
	
}else {
	%>
	<script language="javascript">
		window.location="/weaver/weaver.file.ExcelOut";
	</script>
	<%
}
%>
</body>

