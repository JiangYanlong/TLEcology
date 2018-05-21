<%@ page language="java" contentType="text/html; charset=GBK" %> <!--added by xwj  for td2903  20051019-->
<%@ include file="/systeminfo/init.jsp" %>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<HTML><HEAD>
<LINK href="/css/Weaver.css" type=text/css rel=STYLESHEET>
<SCRIPT language="javascript" src="/js/weaver.js"></script>
<SCRIPT language="javascript" defer="defer" src="/js/datetime.js"></script>
<SCRIPT language="javascript" defer="defer" src="/js/JSDateTime/WdatePicker.js"></script>
</head>
<body>
<%
weaver.interfaces.jiangyl.kq.TlSynKQTestAction action = new weaver.interfaces.jiangyl.kq.TlSynKQTestAction();
String result = action.exec();
out.print(new String(result.getBytes(),"UTF-8"));
%>
</body>
