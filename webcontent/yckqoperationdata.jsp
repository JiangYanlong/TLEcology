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
<jsp:useBean id="TlYckq" class="weaver.interfaces.jiangyl.kq.TlYckq" scope="page"/>
<jsp:useBean id="DepartmentComInfo" class="weaver.hrm.company.DepartmentComInfo" scope="page"/>
<%
if(!HrmUserVarify.checkUserRight("YCBBCK:View", user)){
    	response.sendRedirect("/notice/noright.jsp");
    	return;
}
String departmentName = Util.null2String(request.getParameter("department"));
String fromdate2 = Util.null2String(request.getParameter("fromdate2"));
String departChname = DepartmentComInfo.getDepartmentname(departmentName);
String fileName = departChname + fromdate2 + "�����쳣���ȷ�ϱ�";
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
<%
er  = es.newExcelRow() ;
er.addStringValue("����", "Header" ); 
er.addStringValue("����", "Header" ); 
er.addStringValue("�쳣ʱ��", "Header" ); 
er.addStringValue("��������", "Header" ); 
er.addStringValue("�ϰ�ˢ��ʱ��", "Header" ); 
er.addStringValue("�°�ˢ��ʱ��", "Header" ); 
er.addStringValue("���ڿ���", "Header" ); 
er.addStringValue("Ա��ǩ��", "Header" ); 
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
  <td align="center">�쳣ʱ��</td>
  <td align="center">��������</td>
  <td align="center">�ϰ�ˢ��ʱ��</td>
  <td align="center">�°�ˢ��ʱ��</td>
  <td align="center">���ڿ���</td>
  <td align="center">Ա��ǩ��</td>
</tr>
<%
Map<String, weaver.interfaces.jiangyl.kq.TlHrmMode> result = TlYckq.get(fromdate2,departmentName);
for(Entry<String,weaver.interfaces.jiangyl.kq.TlHrmMode> entry : result.entrySet()){
	String key = entry.getKey();
	weaver.interfaces.jiangyl.kq.TlHrmMode model = entry.getValue();
	String xmbh = model.getLastname();
	String xmmc = model.getLoginid();
	String xmqtbm = model.getSigndate();
	String xmlbyj = model.getLx();
	String xmlx = model.getYc_signup();
	String xmjl = model.getYc_signout();
	String cardid = model.getCardid();
	String qz = "";
	er = es.newExcelRow();
	er.addStringValue(xmbh);
	er.addStringValue(xmmc);
	er.addStringValue(xmqtbm);
	er.addStringValue(xmlbyj);
	er.addStringValue(xmlx);
	er.addStringValue(xmjl);
	er.addStringValue(cardid);
	er.addStringValue(qz);
%>
<tr>
  <td align="center"><%=xmbh%></td>
  <td align="center"><%=xmmc%></td>
  <td align="center"><%=xmqtbm%></td>
  <td align="center"><%=xmlbyj%></td>
  <td align="center"><%=xmlx%></td>
  <td align="center"><%=xmjl%></td>
  <td align="center"><%=cardid%></td>
  <td align="center"><%=qz%></td>
</tr>
<%    
 } 
%>
</tbody>
</table>
</div>