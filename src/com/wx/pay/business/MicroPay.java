package com.wx.pay.business;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayApi;
import com.wx.pay.api.WxPayData;
import com.wx.pay.lib.WxPayException;

public class MicroPay {

	private static Logger Log = Logger.getLogger(MicroPay.class);

	/**
	 * 刷卡支付完整业务流程逻辑
	 * 
	 * @param body
	 *            商品描述
	 * @param total_fee
	 *            总金额
	 * @param auth_code
	 *            支付授权码
	 * @throws WxPayException
	 * @return 刷卡支付结果
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws NoSuchAlgorithmException
	 * @throws InterruptedException
	 */
	public static String Run(String body, String total_fee, String auth_code)
			throws WxPayException, NoSuchAlgorithmException,
			ParserConfigurationException, SAXException, IOException,
			InterruptedException {
		Log.info("Micropay is processing...");

		WxPayData data = new WxPayData();
		data.SetValue("auth_code", auth_code);// 授权码
		data.SetValue("body", body);// 商品描述
		data.SetValue("total_fee", Integer.parseInt(total_fee));// 总金额
		data.SetValue("out_trade_no", WxPayApi.GenerateOutTradeNo());// 产生随机的商户订单号

		WxPayData result = WxPayApi.Micropay(data, 10); // 提交被扫支付，接收返回结果

		// 如果提交被扫支付接口调用失败，则抛异常
		if (!result.IsSet("return_code")
				|| result.GetValue("return_code").toString()
						.equalsIgnoreCase("FAIL")) {
			String returnMsg = result.IsSet("return_msg") ? result.GetValue(
					"return_msg").toString() : "";
			Log.error("Micropay API interface call failure, result : "
					+ result.ToXml());
			throw new WxPayException(
					"Micropay API interface call failure, return_msg : "
							+ returnMsg);
		}

		// 签名验证
		result.CheckSign();
		Log.debug("Micropay response check sign success");

		// 刷卡支付直接成功
		if (result.GetValue("return_code").toString()
				.equalsIgnoreCase("SUCCESS")
				&& result.GetValue("result_code").toString()
						.equalsIgnoreCase("SUCCESS")) {
			Log.debug("Micropay business success, result : " + result.ToXml());
			return result.ToPrintStr();
		}

		/******************************************************************
		 * 剩下的都是接口调用成功，业务失败的情况
		 * ****************************************************************/
		// 1）业务结果明确失败
		if (!result.GetValue("err_code").toString()
				.equalsIgnoreCase("USERPAYING")
				&& !result.GetValue("err_code").toString()
						.equalsIgnoreCase("SYSTEMERROR")) {
			Log.error("micropay API interface call success, business failure, result : "
					+ result.ToXml());
			return result.ToPrintStr();
		}

		// 2）不能确定是否失败，需查单
		// 用商户订单号去查单
		String out_trade_no = data.GetValue("out_trade_no").toString();

		// 确认支付是否成功,每隔一段时间查询一次订单，共查询10次
		int queryTimes = 10;// 查询次数计数器
		while (queryTimes-- > 0) {
			ResultCode succCode = new ResultCode();// 查询结果
			WxPayData queryResult = Query(out_trade_no, succCode);
			// 如果需要继续查询，则等待2s后继续
			if (succCode.succResult == 2) {
				Thread.sleep(2000);
				continue;
			}
			// 查询成功,返回订单查询接口返回的数据
			else if (succCode.succResult == 1) {
				Log.debug("Mircopay success, return order query result : "
						+ queryResult.ToXml());
				return queryResult.ToPrintStr();
			}
			// 订单交易失败，直接返回刷卡支付接口返回的结果，失败原因会在err_code中描述
			else {
				Log.error("Micropay failure, return micropay result : "
						+ result.ToXml());
				return result.ToPrintStr();
			}
		}

		// 确认失败，则撤销订单
		Log.error("Micropay failure, Reverse order is processing...");
		if (!Cancel(out_trade_no, 0)) {
			Log.error("Reverse order failure");
			throw new WxPayException("Reverse order failure！");
		}

		return result.ToPrintStr();
	}

	/**
	 * 
	 * 查询订单情况
	 * 
	 * @param string
	 *            out_trade_no 商户订单号
	 * @param int succCode 查询订单结果：0表示订单不成功，1表示订单成功，2表示继续查询
	 * @return 订单查询接口返回的数据，参见协议接口
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public static WxPayData Query(String out_trade_no, ResultCode succCode)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		WxPayData queryOrderInput = new WxPayData();
		queryOrderInput.SetValue("out_trade_no", out_trade_no);
		WxPayData result = WxPayApi.OrderQuery(queryOrderInput, 0);

		if (result.GetValue("return_code").toString()
				.equalsIgnoreCase("SUCCESS")
				&& result.GetValue("result_code").toString()
						.equalsIgnoreCase("SUCCESS")) {
			// 支付成功
			if (result.GetValue("trade_state").toString()
					.equalsIgnoreCase("SUCCESS")) {
				succCode.succResult = 1;
				return result;
			}
			// 用户支付中，需要继续查询
			else if (result.GetValue("trade_state").toString()
					.equalsIgnoreCase("USERPAYING")) {
				succCode.succResult = 2;
				return result;
			}
		}

		// 如果返回错误码为“此交易订单号不存在”则直接认定失败
		if (result.GetValue("err_code").toString()
				.equalsIgnoreCase("ORDERNOTEXIST")) {
			succCode.succResult = 0;
		} else {
			// 如果是系统错误，则后续继续
			succCode.succResult = 2;
		}
		return result;
	}

	/**
	 * 
	 * 撤销订单，如果失败会重复调用10次
	 * 
	 * @param string
	 *            out_trade_no 商户订单号
	 * @param depth
	 *            调用次数，这里用递归深度表示
	 * @return false表示撤销失败，true表示撤销成功
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean Cancel(String out_trade_no, int depth)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		if (depth > 10) {
			return false;
		}

		WxPayData reverseInput = new WxPayData();
		reverseInput.SetValue("out_trade_no", out_trade_no);
		WxPayData result = WxPayApi.Reverse(reverseInput, 0);

		// 接口调用失败
		if (!result.GetValue("return_code").toString()
				.equalsIgnoreCase("SUCCESS")) {
			return false;
		}

		// 如果结果为success且不需要重新调用撤销，则表示撤销成功
		if (!result.GetValue("result_code").toString()
				.equalsIgnoreCase("SUCCESS")
				&& result.GetValue("recall").toString().equalsIgnoreCase("N")) {
			return true;
		} else if (result.GetValue("recall").toString().equalsIgnoreCase("Y")) {
			return Cancel(out_trade_no, ++depth);
		}
		return false;
	}

	static class ResultCode {
		int succResult = 0;
	}
}
