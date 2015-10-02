<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="com.wx.pay.business.NativePay, net.glxn.qrgen.QRCode, net.glxn.qrgen.image.ImageType,java.util.Base64"
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;image/gif;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>微信支付样例-扫码支付</title>
</head>
<body>
	<div style="margin-left: 10px; color: #00CD00; font-size: 30px; font-weight: bolder;">扫码支付模式一</div>
	<br />
	<%
		NativePay nativePay = new NativePay();
		//生成扫码支付模式一url
		String url1 = nativePay.GetPrePayUrl("123456789");
		out.println("<img src=\"data:image/png;base64,"
				+ Base64.getEncoder().encodeToString(
						QRCode.from(url1).to(ImageType.PNG).stream()
								.toByteArray()) + "\"/>");
	%>
	<br />
	<br />
	<br />
	<div style="margin-left: 10px; color: #00CD00; font-size: 30px; font-weight: bolder;">扫码支付模式二</div>
	<br />
	<%
		//生成扫码支付模式二url
		String url2 = nativePay.GetPayUrl("123456789");
		out.println("<img src=\"data:image/png;base64,"
				+ Base64.getEncoder().encodeToString(
						QRCode.from(url2).to(ImageType.PNG).stream()
								.toByteArray()) + "\"/>");
	%>
</body>
</html>