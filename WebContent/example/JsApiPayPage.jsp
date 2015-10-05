<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="com.wx.pay.business.JsApiPay, org.apache.log4j.Logger,com.wx.pay.api.WxPayData"
%>
<%
	Logger Log = Logger.getLogger(this.getClass());
	String wxJsApiParam = null;
	Log.info("page load");
	if (!("POST".equalsIgnoreCase(request.getMethod()) && (request
			.getRemoteHost() != null && request
			.getRemoteHost()
			.toString()
			.equalsIgnoreCase(
					request.getScheme() + "://"
							+ request.getServerName()))))
	{
	    String openid = request.getParameter("openid");
	    String total_fee = request.getParameter("total_fee");
	    //检测是否给当前页面传递了相关参数
	    if (openid==null || openid.isEmpty() || total_fee == null || total_fee.isEmpty())
	    {
	        out.println("<span style='color:#FF0000;font-size:20px'>" + "页面传参出错,请返回重试" + "</span>");
	        Log.error("This page have not get params, cannot be inited, exit...");
	        return;
	    }
	
	    //若传递了相关参数，则调统一下单接口，获得后续相关接口的入口参数
	    JsApiPay jsApiPay = new JsApiPay(request, response);
	    jsApiPay.openid = openid;
	    jsApiPay.total_fee = Integer.parseInt(total_fee);
	
	    //JSAPI支付预处理
	    try
	    {
	        WxPayData unifiedOrderResult = jsApiPay.GetUnifiedOrderResult();
	        wxJsApiParam = jsApiPay.GetJsApiParameters();//获取H5调起JS API参数                    
	        Log.debug("wxJsApiParam : " + wxJsApiParam);
	        //在页面上显示订单信息
	        out.println("<span style='color:#00CD00;font-size:20px'>订单详情：</span><br/>");
	        out.println("<span style='color:#00CD00;font-size:20px'>" + unifiedOrderResult.ToPrintStr() + "</span>");
	    }
	    catch(Exception ex)
	    {
	    	out.println("<span style='color:#FF0000;font-size:20px'>" + "下单失败，请返回重试" + "</span>");
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
	//调用微信JS api 支付
	function jsApiCall() {
		WeixinJSBridge.invoke('getBrandWCPayRequest',
<%=wxJsApiParam%>
	,//josn串
		function(res) {
			WeixinJSBridge.log(res.err_msg);
			alert(res.err_code + res.err_desc + res.err_msg);
		});
	}

	function callpay() {
		if (typeof WeixinJSBridge == "undefined") {
			if (document.addEventListener) {
				document.addEventListener('WeixinJSBridgeReady', jsApiCall,
						false);
			} else if (document.attachEvent) {
				document.attachEvent('WeixinJSBridgeReady', jsApiCall);
				document.attachEvent('onWeixinJSBridgeReady', jsApiCall);
			}
		} else {
			jsApiCall();
		}
	}
</script>
<body>
	<form>
		<br />
		<div align="center">
			<br /> <br /> <br /> <input type="submit" ID="submit" Text="立即支付" onclick="callpay()"
				style="width: 210px; height: 50px; border-radius: 15px; background-color: #00CD00; border: 0px #FE6714 solid; cursor: pointer; color: white; font-size: 16px;"
			/>
		</div>
	</form>
</body>
</html>