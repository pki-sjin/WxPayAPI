<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例-刷卡支付</title>
</head>
<body>
	<form action="MicroPayPage" method="get">
		<div style="margin-left: 2%;">商品描述：</div>
		<br /> <input type="text" ID="body" name="body" style="width: 96%; height: 35px; margin-left: 2%;" value="test" /><br />
		<br />
		<div style="margin-left: 2%;">支付金额(分)：</div>
		<br /> <input type="text" ID="fee" name="fee" style="width: 96%; height: 35px; margin-left: 2%;" value="1" /><br />
		<br />
		<div style="margin-left: 2%;">授权码：</div>
		<br /> <input type="text" ID="auth_code" name="auth_code" style="width: 96%; height: 35px; margin-left: 2%;" /><br />
		<br />
		<div align="center">
			<input type="submit" ID="submit" value="提交刷卡"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
		</div>
	</form>
</body>
</html>