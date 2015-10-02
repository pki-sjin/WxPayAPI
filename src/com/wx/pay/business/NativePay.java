package com.wx.pay.business;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.WxPayConfig;
import com.wx.pay.lib.WxPayException;

public class NativePay {

	private static Logger Log = Logger.getLogger(NativePay.class);

	/**
	 * 生成扫描支付模式一URL
	 * 
	 * @param productId
	 *            商品ID
	 * @return 模式一URL
	 * @throws WxPayException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public String GetPrePayUrl(String productId)
			throws NoSuchAlgorithmException, UnsupportedEncodingException,
			WxPayException {
		WxPayData data = new WxPayData();
		data.SetValue("appid", WxPayConfig.APPID);// 公众帐号id
		data.SetValue("mch_id", WxPayConfig.MCHID);// 商户号
		data.SetValue("time_stamp", WxPayApi.GenerateTimeStamp());// 时间戳
		data.SetValue("nonce_str", WxPayApi.GenerateNonceStr());// 随机字符串
		data.SetValue("product_id", productId);// 商品ID
		data.SetValue("sign", data.MakeSign());// 签名
		String str = ToUrlParams(data.GetValues());// 转换为URL串
		String url = "weixin://wxpay/bizpayurl?" + str;

		Log.info("Get native pay mode 1 url : " + url);
		return url;
	}

	/**
	 * 生成直接支付url，支付url有效期为2小时,模式二
	 * 
	 * @param productId
	 *            商品ID
	 * @return 模式二URL
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public String GetPayUrl(String productId) throws NoSuchAlgorithmException,
			WxPayException, ParserConfigurationException, SAXException,
			IOException {
		Log.info("Native pay mode 2 url is producing...");

		WxPayData data = new WxPayData();
		data.SetValue("body", "test");// 商品描述
		data.SetValue("attach", "test");// 附加数据
		data.SetValue("out_trade_no", WxPayApi.GenerateOutTradeNo());// 随机字符串
		data.SetValue("total_fee", 1);// 总金额
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		data.SetValue("time_start", dateFormat.format(c.getTime()));// 交易起始时间
		c.add(Calendar.MINUTE, 10);
		data.SetValue("time_expire", dateFormat.format(c.getTime()));// 交易结束时间

		data.SetValue("goods_tag", "jjj");// 商品标记
		data.SetValue("trade_type", "NATIVE");// 交易类型
		data.SetValue("product_id", productId);// 商品ID

		WxPayData result = WxPayApi.UnifiedOrder(data, 0);// 调用统一下单接口
		String url = result.GetValue("code_url").toString();// 获得统一下单接口返回的二维码链接

		Log.info("Get native pay mode 2 url : " + url);
		return url;
	}

	/**
	 * 参数数组转换为url格式
	 * 
	 * @param map
	 *            参数名与参数值的映射表
	 * @return URL字符串
	 */
	private String ToUrlParams(TreeMap<String, Object> map) {
		String buff = "";
		for (Entry<String, Object> pair : map.entrySet()) {
			buff += pair.getKey() + "=" + pair.getValue() + "&";
		}
		String regpattern = "&+$";
		Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(buff);
		if (m.find()) {
			buff = buff.substring(0, buff.length() - 1);
		}
		return buff;
	}
}
