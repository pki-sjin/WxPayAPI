package com.wx.pay.business;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.WxPayException;

public class OrderQuery {

	private static Logger Log = Logger.getLogger(OrderQuery.class);

	/***
	 * 订单查询完整业务流程逻辑
	 * 
	 * @param transaction_id
	 *            微信订单号（优先使用）
	 * @param out_trade_no
	 *            商户订单号
	 * @return 订单查询结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public static String Run(String transaction_id, String out_trade_no)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		Log.info("OrderQuery is processing...");

		WxPayData data = new WxPayData();
		if (transaction_id != null && !transaction_id.isEmpty())// 如果微信订单号存在，则以微信订单号为准
		{
			data.SetValue("transaction_id", transaction_id);
		} else// 微信订单号不存在，才根据商户订单号去查单
		{
			data.SetValue("out_trade_no", out_trade_no);
		}

		WxPayData result = WxPayApi.OrderQuery(data, 0);// 提交订单查询请求给API，接收返回数据

		Log.info("OrderQuery process complete, result : " + result.ToXml());
		return result.ToPrintStr();
	}
}
