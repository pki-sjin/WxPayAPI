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
	String wxEditAddrParam = null;
	Log.info("page load");
	if (!("POST".equalsIgnoreCase(request.getMethod()) && (request
			.getRemoteHost() != null && request
			.getRemoteHost()
			.toString()
			.equalsIgnoreCase(
					request.getScheme() + "://"
							+ request.getServerName())))) {
		JsApiPay jsApiPay = new JsApiPay(request, response);
		try {
			//调用【网页授权获取用户信息】接口获取用户的openid和access_token
			jsApiPay.GetOpenidAndAccessToken();

			//获取收货地址js函数入口参数
			wxEditAddrParam = jsApiPay.GetEditAddressParameters();
			session.setAttribute("openid", jsApiPay.openid);
		} catch (Exception ex) {
			out.write("<span style='color:#FF0000;font-size:20px'>"
					+ "页面加载出错，请重试" + "</span>");
		}
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
	//获取共享地址
	function editAddress() {
		WeixinJSBridge.invoke('editAddress',
<%=wxEditAddrParam%>
	, //josn串
		function(res) {
			var addr1 = res.proviceFirstStageName;
			var addr2 = res.addressCitySecondStageName;
			var addr3 = res.addressCountiesThirdStageName;
			var addr4 = res.addressDetailInfo;
			var tel = res.telNumber;
			var addr = addr1 + addr2 + addr3 + addr4;
			alert(addr + ":" + tel);
		});
	}

	window.onload = function() {
		if (typeof WeixinJSBridge == "undefined") {
			if (document.addEventListener) {
				document.addEventListener('WeixinJSBridgeReady', editAddress,
						false);
			} else if (document.attachEvent) {
				document.attachEvent('WeixinJSBridgeReady', editAddress);
				document.attachEvent('onWeixinJSBridgeReady', editAddress);
			}
		} else {
			editAddress();
		}
	};
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