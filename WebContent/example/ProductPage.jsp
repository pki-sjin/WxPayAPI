<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="com.wx.pay.business.JsApiPay, org.apache.log4j.Logger"
%>
<%
	/// <summary>
	/// 调用js获取收货地址时需要传入的参数
	/// 格式：json串
	/// 包含以下字段：
	///     appid：公众号id
	///     scope: 填写“jsapi_address”，获得编辑地址权限
	///     signType:签名方式，目前仅支持SHA1
	///     addrSign: 签名，由appid、url、timestamp、noncestr、accesstoken参与签名
	///     timeStamp：时间戳
	///     nonceStr: 随机字符串
	/// </summary>
	Logger Log = Logger.getLogger(this.getClass());
	String self = (String) session.getAttribute("openid");

	if (self == null) {
		// redirect to get openid
		JsApiPay.requestOpenId(request, response);
		return;
	}
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例-JSAPI支付</title>
</head>
<script type="text/javascript">
</script>
<body>
	<form action="ProductPage" method="get">
		<br />
		<div>
			<label ID="Label1" style="color: #00CD00;"><b>商品一：价格为<span style="color: #f00; font-size: 50px">1分</span>钱
			</b></label><br /> <br />
		</div>
		<div align="center">
			<input type="hidden" value="1" name="total_fee" /> <input type="submit" ID="Button1" value="立即购买"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
		</div>
	</form>
	<br />
	<br />
	<br />
	<form action="ProductPage" method="get">
		<div>
			<label ID="Label2" style="color: #00CD00;"><b>商品二：价格为<span style="color: #f00; font-size: 50px">2分</span>钱
			</b></label><br /> <br />
		</div>
		<div align="center">
			<input type="hidden" value="2" name="total_fee" /> <input type="submit" ID="Button1" value="立即购买"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
		</div>
	</form>
</body>
</html>