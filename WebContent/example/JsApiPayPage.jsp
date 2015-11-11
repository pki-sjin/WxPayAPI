<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="com.wx.pay.business.JsApiPay, org.apache.log4j.Logger,com.wx.pay.api.WxPayData, com.wx.pay.api.WxPayApi"
%>
<%
	WxPayData wxJsApiParam = null;
	Logger Log = Logger.getLogger(this.getClass());
	String openid = request.getParameter("openid");
	String total_fee = request.getParameter("total_fee");
	//检测是否给当前页面传递了相关参数
	if (openid == null || openid.isEmpty() || total_fee == null
			|| total_fee.isEmpty()) {
		out.println("<span style='color:#FF0000;font-size:20px'>"
				+ "页面传参出错,请返回重试" + "</span>");
		Log.error("This page have not get params, cannot be inited, exit...");
		return;
	}

	//JSAPI支付预处理
	try {
		WxPayData unifiedOrderResult = JsApiPay.GetUnifiedOrderResult(
				openid, total_fee);
		wxJsApiParam = JsApiPay.GetJsApiParameters(unifiedOrderResult);//获取H5调起JS API参数
		//在页面上显示订单信息
		out.println("<span style='color:#00CD00;font-size:20px'>订单详情：</span><br/>");
		out.println("<span style='color:#00CD00;font-size:20px'>"
				+ unifiedOrderResult.ToPrintStr() + "</span>");
	} catch (Exception ex) {
		out.println("<span style='color:#FF0000;font-size:20px'>"
				+ "下单失败，请返回重试" + ex.getMessage() + "</span>");
		return;
	}

	String nonceStr = WxPayApi.GenerateNonceStr();
	String jsapi_ticket = JsApiPay.getJsApiTicket(JsApiPay
			.getAccessToken());
	long timestamp = System.currentTimeMillis();
	String host = request.getRequestURL().toString();
	String queryString = request.getQueryString();
	String url = host
			+ (queryString != null ? ("?" + queryString) : "");

	String signature = JsApiPay.getSignature(jsapi_ticket, nonceStr,
			timestamp, url);
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例-JSAPI支付</title>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
	//调用微信JS api 支付
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: 'wx744a5f3859265b34', // 必填，公众号的唯一标识
	    timestamp: <%=timestamp%>, // 必填，生成签名的时间戳
	    nonceStr: '<%=nonceStr%>', // 必填，生成签名的随机串
	    signature: '<%=signature%>', // 必填，签名，见附录1
		jsApiList : [ 'chooseWXPay' ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	});

	function callpay() {
		wx.chooseWXPay({
			timestamp: <%=wxJsApiParam.GetValue("timeStamp")%>, 
		    nonceStr: '<%=wxJsApiParam.GetValue("nonceStr")%>',
		    package: '<%=wxJsApiParam.GetValue("package")%>', 
		    signType : '<%=wxJsApiParam.GetValue("signType")%>',
		    paySign: '<%=wxJsApiParam.GetValue("paySign")%>',
		    success: function(res) {
		    	alert('支付成功');
		    }
		});
	}
</script>
</head>
<body>
	<div align="center">
			<br /> <br /> <br /> <input type="button" ID="submit" value="立即支付" onclick="callpay()"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
	</div>
</body>
</html>