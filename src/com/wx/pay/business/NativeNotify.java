package com.wx.pay.business;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.Notify;
import com.wx.pay.lib.WxPayConfig;
import com.wx.pay.lib.WxPayException;

/// <summary>
/// 扫码支付模式一回调处理类
/// 接收微信支付后台发送的扫码结果，调用统一下单接口并将下单结果返回给微信支付后台
/// </summary>
public class NativeNotify extends Notify {

	private static Logger Log = Logger.getLogger(NativeNotify.class);

	public NativeNotify(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}

	public void ProcessNotify() throws NoSuchAlgorithmException,
			WxPayException, IOException, ParserConfigurationException,
			SAXException {
		WxPayData notifyData = GetNotifyData();

		// 检查openid和product_id是否返回
		if (!notifyData.IsSet("openid") || !notifyData.IsSet("product_id")) {
			WxPayData res = new WxPayData();
			res.SetValue("return_code", "FAIL");
			res.SetValue("return_msg", "回调数据异常");
			Log.info("The data WeChat post is error : " + res.ToXml());
			PrintWriter print = response.getWriter();
			print.write(res.ToXml());
			print.flush();
			print.close();
		}

		// 调统一下单接口，获得下单结果
		String openid = notifyData.GetValue("openid").toString();
		String product_id = notifyData.GetValue("product_id").toString();
		WxPayData unifiedOrderResult = new WxPayData();
		try {
			unifiedOrderResult = UnifiedOrder(openid, product_id);
		} catch (Exception ex)// 若在调统一下单接口时抛异常，立即返回结果给微信支付后台
		{
			WxPayData res = new WxPayData();
			res.SetValue("return_code", "FAIL");
			res.SetValue("return_msg", "统一下单失败");
			Log.error("UnifiedOrder failure : " + res.ToXml());
			PrintWriter print = response.getWriter();
			print.write(res.ToXml());
			print.flush();
			print.close();
		}

		// 若下单失败，则立即返回结果给微信支付后台
		if (!unifiedOrderResult.IsSet("appid")
				|| !unifiedOrderResult.IsSet("mch_id")
				|| !unifiedOrderResult.IsSet("prepay_id")) {
			WxPayData res = new WxPayData();
			res.SetValue("return_code", "FAIL");
			res.SetValue("return_msg", "统一下单失败");
			Log.error("UnifiedOrder failure : " + res.ToXml());
			PrintWriter print = response.getWriter();
			print.write(res.ToXml());
			print.flush();
			print.close();
		}

		// 统一下单成功,则返回成功结果给微信支付后台
		WxPayData data = new WxPayData();
		data.SetValue("return_code", "SUCCESS");
		data.SetValue("return_msg", "OK");
		data.SetValue("appid", WxPayConfig.APPID);
		data.SetValue("mch_id", WxPayConfig.MCHID);
		data.SetValue("nonce_str", WxPayApi.GenerateNonceStr());
		data.SetValue("prepay_id", unifiedOrderResult.GetValue("prepay_id"));
		data.SetValue("result_code", "SUCCESS");
		data.SetValue("err_code_des", "OK");
		data.SetValue("sign", data.MakeSign());

		Log.info("UnifiedOrder success , send data to WeChat : " + data.ToXml());
		PrintWriter print = response.getWriter();
		print.write(data.ToXml());
		print.flush();
		print.close();
	}

	private WxPayData UnifiedOrder(String openId, String productId)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		// 统一下单
		WxPayData req = new WxPayData();
		req.SetValue("body", "test");
		req.SetValue("attach", "test");
		req.SetValue("out_trade_no", WxPayApi.GenerateOutTradeNo());
		req.SetValue("total_fee", 1);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		req.SetValue("time_start", dateFormat.format(c.getTime()));
		c.add(Calendar.MINUTE, 10);
		req.SetValue("time_expire", dateFormat.format(c.getTime()));

		req.SetValue("goods_tag", "test");
		req.SetValue("trade_type", "NATIVE");
		req.SetValue("openid", openId);
		req.SetValue("product_id", productId);
		WxPayData result = WxPayApi.UnifiedOrder(req, 0);
		return result;
	}
}
