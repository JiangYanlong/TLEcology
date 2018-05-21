<%@ page language="java" contentType="text/html; charset=GBK" %> <!--added by xwj  for td2903  20051019-->
<SCRIPT language="javascript" src="/js/init_wev8.js"></script>
<%@ include file="/systeminfo/init.jsp" %>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<HTML><HEAD>
<LINK href="/css/Weaver.css" type=text/css rel=STYLESHEET>
<SCRIPT language="javascript" src="/js/weaver.js"></script>
<SCRIPT language="javascript" defer="defer" src="/js/datetime.js"></script>
<SCRIPT language="javascript" defer="defer" src="/js/JSDateTime/WdatePicker.js"></script>
</head>
<%
BaseBean bsWriteLog=new BaseBean();
String imagefilename = "/images/hdDOC.gif";
String titlename = "�򿨼�¼";
String needfav ="1";
String needhelp ="";

%>
<body>
<!-- ��Ҫ���õ� -->
<%@ include file="/systeminfo/TopTitle.jsp" %>
<%@ include file="/systeminfo/RightClickMenuConent.jsp" %>
<%
//����˵��Ҽ���ʾ�Լ�����¼�
RCMenu += "{"+SystemEnv.getHtmlLabelName(197,user.getLanguage())+",javascript:OnChangePage(),_self}" ;
RCMenuHeight += RCMenuHeightStep ;
%>

<%!
/**
 *
 * ������Աid��ȡ����id
 **/
public String getdepid(String id) {
	if ("1".equals(id)) {
		return "";
	}
	String departmentid = "";
	try {
		departmentid = new weaver.hrm.resource.ResourceComInfo().getDepartmentID(id);
	} catch (java.lang.Exception e) {
		e.printStackTrace();
	}
	return departmentid;
}
/**
 *
 * �ж��Ƿ��й���ԱȨ��
 **/
public boolean isadmin(String id) {
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	rs.execute("select * from formtable_main_175 where gly = '"+id+"'");
	rs.next();
	if (rs.getCounts() > 0) {
		return true;
	}
	return false;
}
public static String getAll(int userid) {
	weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
	String sql = "select * from formtable_main_194 where gly = '"+userid+"'";
	rs.execute(sql);
	rs.next();
	String cknr = rs.getString("cknr");
	java.lang.StringBuffer sb = new java.lang.StringBuffer("(");
	if(!"".equals(cknr) && null != cknr) {
		String[] strs = cknr.split(",");
		for(int i = 0 ; i < strs.length; i++) {
			if (i == strs.length -1 ) {
				sb.append(strs[i]);
			}else {
				sb.append(strs[i]);
				sb.append(",");
			}
		}
	}
	sb.append(")");
	new weaver.general.BaseBean().writeLog("string:"+sb.toString());
	return sb.toString();
}
%>
<%
int userId = user.getUID();
String department = request.getParameter("bumen");
new weaver.general.BaseBean().writeLog("department:"+department);
String kqlx = request.getParameter("kqlx");
String beginDate = request.getParameter("begindate");
String endDate = request.getParameter("enddate");
String title1="��Ա";
String title2="������";
String title3="��ʱ��";
String title4="������";
String title5="�򿨿���";
String title6="��λ��";
String title7="�쳣����";
String backfields="";
if (userId == 1) {
	backfields="id,objno,signdate,signtime,signkind,cardid,door,kqlx";
} else {
	backfields="id,objno,signdate,signtime,signkind,cardid,door";
}
String fromsql=" tl_yckq ";
String sqlwhere = "1=1";
if (userId == 1) {
	if (null != kqlx && !"".equals(kqlx)) {
		if (!"0".equals(kqlx)) {
			sqlwhere = sqlwhere + " and kqlx = '"+kqlx+"'";
		}
	}
	if (null != department && !"".equals(department) && !"null".equals(department)) {
		sqlwhere = sqlwhere + " and objno in ("+department.substring(1,department.length())+")";
	}
} else if (isadmin(String.valueOf(userId))) {
	if (null != kqlx && !"".equals(kqlx)) {
		if (!"0".equals(kqlx)) {
			sqlwhere = sqlwhere + " and kqlx = '"+kqlx+"'";
		}
	}
	if (null != department && !"".equals(department) && !"null".equals(department)) {
		sqlwhere = sqlwhere + " and objno in ("+department.substring(1,department.length())+")";
	}
	String depId = getAll(userId);
	new weaver.general.BaseBean().writeLog("depId:"+depId);
	if (null != depId && !"".equals(depId) && !"null".equals(depId)) {
		sqlwhere = sqlwhere + " and bm in "+depId+"";
	}
} else {
	sqlwhere = sqlwhere + " and objno = '"+userId+"'";
}

java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd"); 
//��ȡ��ǰ�µ�һ��
java.util.Calendar c = java.util.Calendar.getInstance();    
c.add(java.util.Calendar.MONTH, 0);
c.set(java.util.Calendar.DAY_OF_MONTH,1);
String first = format.format(c.getTime());
//��ȡ��ǰ�����һ��
java.util. Calendar ca = java.util.Calendar.getInstance();    
ca.set(java.util.Calendar.DAY_OF_MONTH, ca.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));  
String last = format.format(ca.getTime());
if(null != beginDate) {
	sqlwhere = sqlwhere + " and signdate  >= '"+beginDate+"' ";
}else {
	beginDate = first;
	sqlwhere = sqlwhere + " and signdate  >= '"+beginDate+"' ";
}
if(null != endDate) {
	sqlwhere = sqlwhere + " and signdate  <= '"+endDate+"' ";
}else {
	endDate = last;
	sqlwhere = sqlwhere + " and signdate  <= '"+endDate+"' ";
}
new weaver.general.BaseBean().writeLog("sqlwhere:"+sqlwhere);
int pagesize = 10;
String orderby="signdate";
String tableString="";
	tableString+= "<table instanceid=\"tl_yckq\" tabletype=\"none\" pagesize=\""+pagesize+"\" >";
	tableString+= "		<sql backfields=\""+backfields+"\" sqlform=\""+fromsql+"\" sqlwhere=\""+Util.toHtmlForSplitPage(sqlwhere)+"\" sqlorderby=\""+orderby+"\" sqlprimarykey=\"id\" sqlsortway=\"desc\" sqlisdistinct=\"true\" />";
	tableString+= "		<head>";
	tableString+= "			<col width=\"10%\" text=\""+title1+"\" column=\"objno\"  transmethod=\"weaver.hrm.resource.ResourceComInfo.getResourcename\"/>";
	tableString+= "			<col width=\"10%\" text=\""+title2+"\" column=\"signdate\" />";
	tableString+= "         <col width=\"20%\" text=\""+title3+"\" column=\"signtime\"  />";
	tableString+= "			<col width=\"20%\" text=\""+title4+"\" column=\"signkind\"		  />";
	if(userId == 1) {
		tableString+= "			<col width=\"20%\" text=\""+title7+"\" column=\"kqlx\"	transmethod=\"weaver.interfaces.jiangyl.kq.TlTranslateYCKQ.trunslate\"	  />";
		tableString+= "			<col width=\"10%\" text=\""+title5+"\" column=\"cardid\"          />";
		tableString+= "         <col width=\"10%\" text=\""+title6+"\" column=\"door\"           />";
	} else {
		tableString+= "			<col width=\"10%\" text=\""+title5+"\" column=\"cardid\"          />";
		tableString+= "         <col width=\"10%\" text=\""+title6+"\" column=\"door\"           />";
	}
	tableString+= "		</head>";
	tableString+= "</table>";
%>

<!-- ��Ҫ���õ� -->
<%@ include file="/systeminfo/RightClickMenu.jsp" %>
<form id="frmmain" name="frmmain" method="post" action="">
	<TABLE class=ViewForm  style="margin:10px" valign="top">
		<TR valign="top">
		<TD WIDTH="10%">������</TD>
			<TD CLASS="Field">
				<INPUT class="Wdate" type="text" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" id="begindate" name="begindate" value="<%=beginDate%>"/>-
				<INPUT class="Wdate" type="text" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" id="enddate" name="enddate" value="<%=endDate%>"/>
			</TD>
		</TR>
		<TR style="height:1px;"><td colspan=4 class="line"></td></TR>
		<%
		if (userId == 1 || isadmin(String.valueOf(userId))) {
		%>
		<TD WIDTH="10%">����</TD>
			<TD CLASS="Field">
				<select id="kqlx" name="kqlx" value="<%=kqlx%>">
					<option value="0"></option>
					<option value="1">�ٵ�</option>
					<option value="2">����</option>
					<option value="3">����</option>
				</select>
			</TD>
		</TR>
		<TR style="height:1px;"><td colspan=4 class="line"></td></TR>
		<TD WIDTH="10%">��Ա</TD>
			<TD CLASS="Field">
				<button class=Browser type="button" onClick="onShowQu()"></button>
                 <span  id=bumenspan><%=new weaver.hrm.resource.ResourceComInfo().getMulResourcename(department)%></span>
                 <input class=inputstyle id=bumen type=hidden name=bumen value="<%=department%>">
			</TD>
		</TR>
		<TR style="height:1px;"><td colspan=4 class="line"></td></TR>
		<%
		}
		%>
	</TABLE>
</form>
<wea:SplitPageTag  tableInstanceId=""  tableString="<%=tableString%>"  mode="run" selectedstrs="" isShowTopInfo="true"   isShowBottomInfo ="true" />
<script>
function OnChangePage(){
	document.frmmain.submit();
}
function onShowQu(){
	var shi = jQuery("#bumen").val();
    data = window.showModalDialog("/systeminfo/BrowserMain.jsp?url=/hrm/resource/MutiResourceBrowser.jsp");
	if (data!=null){
		if (data.id != "" ){
			ids = data.id.split(",");
			names =data.name.split(",");
			sHtml = "";
			for( var i=0;i<ids.length;i++){
				if(ids[i]!=""){
					sHtml = sHtml+names[i]+"&nbsp;&nbsp;";
				}
			}
			jQuery("#bumenspan").html(sHtml);
			jQuery("input[name=bumen]").val(data.id);
		}else{
			jQuery("#bumenspan").html("");
			jQuery("input[name=bumen]").val("");
		}
	}
}
jQuery(function(){
	if ("<%=kqlx%>" != null) {
		var value = "";
		if ("<%=kqlx%>" == "0") {
			value = "";
		}
		if ("<%=kqlx%>" == "1") {
			value = "�ٵ�";
		}
		if ("<%=kqlx%>" == "2") {
			value = "����";
		}
		if ("<%=kqlx%>" == "3") {
			value = "����";
		}
		jQuery("#kqlx option[text='"+value+"']").attr("selected", true);
	}
});
</script>
</body>
