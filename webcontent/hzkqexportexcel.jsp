<%@ page language="java" contentType="text/html; charset=GBK" %> <!--added by xwj  for td2903  20051019-->
<%@ include file="/systeminfo/init.jsp" %>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<%@ page import="weaver.general.*,java.util.*,java.util.Map.*" %>
<%@ page import="weaver.file.*" %>
<%@ page import="weaver.teechart.*" %>
<jsp:useBean id="ExcelFile" class="weaver.file.ExcelFile" scope="session"/>
<jsp:useBean id="TlHzkq" class="weaver.interfaces.jiangyl.kq.TlHzkq" scope="page"/>
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
int userId = user.getUID();
String departmentName = Util.null2String(request.getParameter("department"));
String fromdate2 = Util.null2String(request.getParameter("fromdate2"));
%>
<%!
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
	
}else {
	%>
	<script language="javascript">
		window.location="/weaver/weaver.file.ExcelExport?departmentid=<%=departmentName%>&date=<%=fromdate2%>";
	</script>
	<%
}
%>
</body>
