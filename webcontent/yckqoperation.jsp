<%@ page language="java" contentType="text/html; charset=GBK" %> <!--added by xwj  for td2903  20051019-->
<SCRIPT language="javascript" src="/js/init_wev8.js"></script>
<%@ include file="/systeminfo/init.jsp" %>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<HTML><HEAD>
<LINK href="/css/Weaver.css" type=text/css rel=STYLESHEET>
<SCRIPT language="javascript" src="/js/weaver.js"></script>
</head>
<%
if(!HrmUserVarify.checkUserRight("YCBBCK:View", user)){
    	response.sendRedirect("/notice/noright.jsp");
    	return;
}
String imagefilename = "/images/hdDOC.gif";
String titlename = "异常考勤";
String needfav ="1";
String needhelp ="";

String department = Util.null2String(request.getParameter("bumen"));
String fromdate2 = Util.null2String(request.getParameter("fromdate2"));
%>

<body onload="showdata()" style="text-align:center">
<!-- 需要引用的 -->
<%@ include file="/systeminfo/TopTitle.jsp" %>
<%@ include file="/systeminfo/RightClickMenuConent.jsp" %>
<%
//定义菜单右键显示以及点击事件
RCMenu += "{"+SystemEnv.getHtmlLabelName(197,user.getLanguage())+",javascript:OnChangePage(),_self}" ;
RCMenuHeight += RCMenuHeightStep ;
RCMenu += "{"+SystemEnv.getHtmlLabelName(17416,user.getLanguage())+",javascript:doExportExcel(),_self}" ;
RCMenuHeight += RCMenuHeightStep ;
%>

<!-- 需要引用的 -->
<%@ include file="/systeminfo/RightClickMenu.jsp" %>
<form id="frmmain" name="frmmain" method="post" action="">
<TABLE class=ViewForm  style="margin:10px" valign="top">
	<TABLE class=ViewForm  style="margin:10px" valign="top">
		<TR valign="top">
		<TD WIDTH="10%">部门</TD>
			<TD width ="20%" CLASS="Field"> 
				<button class=Browser type="button" onClick="onShowQu()"></button>
                 <span  id=bumenspan><%=new weaver.hrm.company.DepartmentComInfo().getDepartmentname(department)%></span>
                 <input class=inputstyle id=bumen type=hidden name=bumen value="<%=department%>">
			</TD>
		</TR>
		<TR style="height:1px;"><td colspan=4 class="line"></td></TR>
		<TR valign="top">
		<TD WIDTH="10%">日期</TD>
			<TD width ="20%" CLASS="Field"> 
				<INPUT class="Wdate" type="text" onFocus="WdatePicker({dateFmt:'yyyy-MM'})" id="fromdate2" name="fromdate2" value="<%=fromdate2%>"/>
			</TD>
		</TR>
		<TR style="height:1px;"><td colspan=4 class="line"></td></TR>
		<TD WIDTH="10%" colSpan="2">
			<button id="search">查看异常信息汇总</button>
		</TD>	
		</TR>
		<TR style="height:1px;"><td colspan=4 class="line"></td></TR>
	</TABLE>
</TABLE>
<div id="showdatadiv" style="width:90%;margin:0 auto">
	<table id="scrollarea" name="scrollarea" width="100%" height="100%" style="zIndex:-1" >
	<tr>
			<td align="center" valign="center">
				<fieldset style="width:30%;margin-top: 30px;">   
					<img src="/images/loading2.gif" align="top"><%=SystemEnv.getHtmlLabelName(20204,user.getLanguage())%></fieldset>
			</td>
	</tr>
	</table>
</div>
</form>
<div style="display:none"><iframe id="excel" width="80%" height="350px;"  frameborder="0" scrolling="no"></iframe></div>
<script>
function OnChangePage(){
	var dep = jQuery("#bumen").val();
	if(dep == "") {
		alert("请选择部门");
		return false;
	}
	var time = jQuery("#fromdate2").val();
	if(time == "") {
		alert("请选择时间");
		return false;
	}
	document.frmmain.submit();
}
function onShowQu(){
	var shi = jQuery("#bumen").val();
    data = window.showModalDialog("/systeminfo/BrowserMain.jsp?url=/hrm/company/DepartmentBrowser.jsp");
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
function ajaxinit(){
    var ajax=false;
    try {
        ajax = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            ajax = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (E) {
            ajax = false;
        }
    }
    if (!ajax && typeof XMLHttpRequest!='undefined') {
        ajax = new XMLHttpRequest();
    }
    return ajax;
}

function showdata(){
    var ajax=ajaxinit();
    ajax.open("POST", "yckqoperationdatas.jsp", true);
    ajax.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
    ajax.send("fromdate2=<%=fromdate2%>&department=<%=department%>");
    //获取执行状态
    ajax.onreadystatechange = function() {
        //如果执行状态成功，那么就把返回信息写到指定的层里
        if (ajax.readyState == 4 && ajax.status == 200) {
            try{
                document.all("showdatadiv").innerHTML=ajax.responseText;
            }catch(e){
                return false;
            }
        }
    }
}
function doExportExcel(){
	jQuery("#excel").attr('src','yckqexportexcel.jsp?fromdate2=<%=fromdate2%>&department=<%=department%>');
}
function printf(){
	location.href = 'yckqprint.jsp?bumen=<%=department%>&fromdate2=<%=fromdate2%>';
}
jQuery(function(){
	jQuery("#search").bind('click',function(){
		var bm = jQuery("#bumen").val();
		var fromdate2 = jQuery("#fromdate2").val();
		window.showModalDialog("yckqhzoperationdatas.jsp?department="+bm+"&fromdate2="+fromdate2);
	});
});
</script>
<SCRIPT language="javascript" defer="defer" src="/js/datetime.js"></script>
<SCRIPT language="javascript" defer="defer" src="/js/JSDateTime/WdatePicker.js"></script>
</body>
