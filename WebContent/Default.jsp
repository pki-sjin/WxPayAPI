<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例</title>
<style type="text/css">
ul {
	margin-left: 10px;
	margin-right: 10px;
	margin-top: 10px;
	padding: 0;
}

li {
	width: 32%;
	float: left;
	margin: 0px;
	margin-left: 1%;
	padding: 0px;
	height: 100px;
	display: inline;
	line-height: 100px;
	color: #fff;
	font-size: x-large;
	word-break: break-all;
	word-wrap: break-word;
	margin-bottom: 5px;
	background-color: #00CD00;
}

a {
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	text-decoration: none;
	color: #fff;
}

a:link {
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	text-decoration: none;
	color: #fff;
}

a:visited {
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	text-decoration: none;
	color: #fff;
}

a:hover {
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	text-decoration: none;
	color: #fff;
}

a:active {
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	text-decoration: none;
	color: #fff;
}
</style>
</head>
<body>
	<div align="center">
		<ul>
			<li style="background-color: #00CD00"><a href="example/ProductPage.jsp">JSAPI支付</a></li>
			<li style="background-color: #00CD00"><a href="example/MicroPayPage.jsp">刷卡支付</a></li>
			<li style="background-color: #00CD00"><a href="example/NativePayPage.jsp">扫码支付</a></li>
			<li style="background-color: #00CD00"><a href="example/OrderQueryPage.jsp">订单查询</a></li>
			<li style="background-color: #00CD00"><a href="example/RefundPage.jsp">订单退款</a></li>
			<li style="background-color: #00CD00"><a href="example/RefundQueryPage.jsp">退款查询</a></li>
			<li style="background-color: #00CD00"><a href="example/DownloadBillPage.jsp">下载账单</a></li>
		</ul>
	</div>
</body>
</html>