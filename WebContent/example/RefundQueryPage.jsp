<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例-退款查询</title>
</head>
<body>
	<form action="RefundQueryPage" method="get">
		<div style="margin-left: 2%; color: #f00">微信订单号、商户订单号、商户退款单号、微信退款单号选填至少一个，微信退款单号优先：</div>
		<br />
		<div style="margin-left: 2%;">微信订单号：</div>
		<br /> <input type="text" ID="transaction_id" name="transaction_id"
			style="width: 96%; height: 35px; margin-left: 2%;"
		/><br /> <br />
		<div style="margin-left: 2%;">商户订单号：</div>
		<br /> <input type="text" ID="out_trade_no" name="out_trade_no" style="width: 96%; height: 35px; margin-left: 2%;" /><br />
		<br />
		<div style="margin-left: 2%;">商户退款单号：</div>
		<br /> <input type="text" ID="out_refund_no" name="out_refund_no" style="width: 96%; height: 35px; margin-left: 2%;" /><br />
		<br />
		<div style="margin-left: 2%;">微信退款单号：</div>
		<br /> <input type="text" ID="refund_id" name="refund_id" style="width: 96%; height: 35px; margin-left: 2%;" /><br />
		<br />
		<div align="center">
			<input type="submit" ID="submit" value="查询退款"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
		</div>
	</form>
</body>
</html>