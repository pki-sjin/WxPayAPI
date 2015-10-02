package com.wx.pay.business;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.WxPayConfig;
import com.wx.pay.lib.WxPayException;

public class Refund {

	private static Logger Log = Logger.getLogger(Refund.class);

	/***
	 * 申请退款完整业务流程逻辑
	 * 
	 * @param transaction_id
	 *            微信订单号（优先使用）
	 * @param out_trade_no
	 *            商户订单号
	 * @param total_fee
	 *            订单总金额
	 * @param refund_fee
	 *            退款金额
	 * @return 退款结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public static String Run(String transaction_id, String out_trade_no,
			String total_fee, String refund_fee)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		Log.info("Refund is processing...");

		WxPayData data = new WxPayData();
		if (transaction_id != null && !transaction_id.isEmpty())// 微信订单号存在的条件下，则已微信订单号为准
		{
			data.SetValue("transaction_id", transaction_id);
		} else// 微信订单号不存在，才根据商户订单号去退款
		{
			data.SetValue("out_trade_no", out_trade_no);
		}

		data.SetValue("total_fee", Integer.parseInt(total_fee));// 订单总金额
		data.SetValue("refund_fee", Integer.parseInt(refund_fee));// 退款金额
		data.SetValue("out_refund_no", WxPayApi.GenerateOutTradeNo());// 随机生成商户退款单号
		data.SetValue("op_user_id", WxPayConfig.MCHID);// 操作员，默认为商户号

		WxPayData result = WxPayApi.Refund(data, 0);// 提交退款申请给API，接收返回数据

		Log.info("Refund process complete, result : " + result.ToXml());
		return result.ToPrintStr();
	}
}
