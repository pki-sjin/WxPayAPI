<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例-下载对账单</title>
</head>
<body>
	<form action="DownloadBillPage" method="get">
		<div style="margin-left: 2%;">对账日期：</div>
		<br /> <input type="text" ID="bill_date" name="bill_date" style="width: 96%; height: 35px; margin-left: 2%;" /> <br />
		<br />
		<div style="margin-left: 2%;">账单类型：</div>
		<br /> <select ID="bill_type" name="bill_type" style="width: 96%; height: 35px; margin-left: 2%;">
			<option value="ALL">所有订单信息</option>
			<option value="SUCCESS">成功支付的订单</option>
			<option value="REFUND">退款订单</option>
			<option value="REVOKED">撤销的订单</option>
		</select> <br /> <br />
		<div align="center">
			<input type="submit" ID="submit" value="下载对账单"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
		</div>
	</form>
</body>
</html>