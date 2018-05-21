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

<%
int userId = user.getUID();
String department = request.getParameter("bumen");
String beginDate = request.getParameter("begindate");
String endDate = request.getParameter("enddate");
String title1="��Ա";
String title2="������";
String title3="��ʱ��";
String title4="������";
String title5="�򿨿���";
String title6="��λ��";
String backfields="id,objno,signdate,signtime,signkind,cardid,door,kqlx";
String fromsql=" formtable_main_159 ";
String sqlwhere=" 1=1";
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
if (null != department && !"".equals(department)) {
	sqlwhere = sqlwhere + " and objno  in ("+department.substring(1,department.length())+") ";
}
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
String orderby="objno";
String tableString="";
	tableString+= "<table instanceid=\"formtable_main_159\" tabletype=\"none\" pagesize=\"10\" >";
	tableString+= "		<sql backfields=\""+backfields+"\" sqlform=\""+fromsql+"\" sqlwhere=\""+Util.toHtmlForSplitPage(sqlwhere)+"\" sqlorderby=\""+orderby+"\" sqlprimarykey=\"id\" sqlsortway=\"asc\" sqlisdistinct=\"false\" />";
	tableString+= "		<head>";
	tableString+= "			<col width=\"10%\" text=\""+title1+"\" column=\"objno\"  transmethod=\"weaver.hrm.resource.ResourceComInfo.getResourcename\"/>";
	tableString+= "			<col width=\"10%\" text=\""+title2+"\" column=\"signdate\" />";
	tableString+= "         <col width=\"20%\" text=\""+title3+"\" column=\"signtime\"  />";
	tableString+= "			<col width=\"20%\" text=\""+title4+"\" column=\"signkind\"		  />";
	tableString+= "			<col width=\"10%\" text=\""+title5+"\" column=\"cardid\"          />";
	tableString+= "         <col width=\"10%\" text=\""+title6+"\" column=\"door\"           />";
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
		<TD WIDTH="10%">����</TD>
			<TD CLASS="Field">
				<select >
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
	</TABLE>
</form>
<wea:SplitPageTag isShowTopInfo="false" tableString="<%=tableString%>" mode="run" />
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
</script>
</body>
