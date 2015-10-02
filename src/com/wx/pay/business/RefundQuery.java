package com.wx.pay.business;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.WxPayException;

public class RefundQuery {

	private static Logger Log = Logger.getLogger(RefundQuery.class);

	/***
	 * 退款查询完整业务流程逻辑
	 * 
	 * @param refund_id
	 *            微信退款单号（优先使用）
	 * @param out_refund_no
	 *            商户退款单号
	 * @param transaction_id
	 *            微信订单号
	 * @param out_trade_no
	 *            商户订单号
	 * @return 退款查询结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public static String Run(String refund_id, String out_refund_no,
			String transaction_id, String out_trade_no)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		Log.info("RefundQuery is processing...");

		WxPayData data = new WxPayData();
		if (refund_id != null && !refund_id.isEmpty()) {
			data.SetValue("refund_id", refund_id);// 微信退款单号，优先级最高
		} else if (out_refund_no != null && !out_refund_no.isEmpty()) {
			data.SetValue("out_refund_no", out_refund_no);// 商户退款单号，优先级第二
		} else if (transaction_id != null && !transaction_id.isEmpty()) {
			data.SetValue("transaction_id", transaction_id);// 微信订单号，优先级第三
		} else {
			data.SetValue("out_trade_no", out_trade_no);// 商户订单号，优先级最低
		}

		WxPayData result = WxPayApi.RefundQuery(data, 0);// 提交退款查询给API，接收返回数据

		Log.info("RefundQuery process complete, result : " + result.ToXml());
		return result.ToPrintStr();
	}
}
