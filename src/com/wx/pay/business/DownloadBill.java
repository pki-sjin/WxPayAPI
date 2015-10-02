package com.wx.pay.business;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.WxPayException;

public class DownloadBill {

	private static Logger Log = Logger.getLogger(DownloadBill.class);

	/***
	 * 下载对账单完整业务流程逻辑
	 * 
	 * @param bill_date
	 *            下载对账单的日期（格式：20140603，一次只能下载一天的对账单）
	 * @param bill_type
	 *            账单类型 ALL，返回当日所有订单信息，默认值 SUCCESS，返回当日成功支付的订单 REFUND，返回当日退款订单
	 *            REVOKED，已撤销的订单
	 * @return 对账单结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public static String Run(String bill_date, String bill_type)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		Log.info("DownloadBill is processing...");

		WxPayData data = new WxPayData();
		data.SetValue("bill_date", bill_date);// 账单日期
		data.SetValue("bill_type", bill_type);// 账单类型
		WxPayData result = WxPayApi.DownloadBill(data, 0);// 提交下载对账单请求给API，接收返回结果

		Log.info("DownloadBill process complete, result : " + result.ToXml());
		return result.ToPrintStr();
	}
}
