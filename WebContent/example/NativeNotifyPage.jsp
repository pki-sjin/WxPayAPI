<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.wx.pay.business.NativeNotify"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
</head>
<body>
	<%
		NativeNotify resultNotify = new NativeNotify(request, response);
    	resultNotify.ProcessNotify();
	%>
</body>
</html>