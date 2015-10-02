package com.wx.pay.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.wx.pay.api.WxPayData;

/// <summary>
/// 回调处理基类
/// 主要负责接收微信支付后台发送过来的数据，对数据进行签名验证
/// 子类在此类基础上进行派生并重写自己的回调处理过程
/// </summary>
public abstract class Notify {

	private static Logger Log = Logger.getLogger(Notify.class);

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	public Notify(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
	}

	// / <summary>
	// / 接收从微信支付后台发送过来的数据并验证签名
	// / </summary>
	// / <returns>微信支付后台返回的数据</returns>
	public WxPayData GetNotifyData() throws IOException,
			ParserConfigurationException, SAXException, WxPayException {
		// 接收从微信后台POST过来的数据
		InputStream s = request.getInputStream();
		int count = 0;
		byte[] buffer = new byte[1024];
		StringBuilder builder = new StringBuilder();
		while ((count = s.read(buffer, 0, 1024)) > 0) {
			builder.append(new String(buffer, 0, count, "UTF-8"));
		}
		s.close();

		Log.info("Receive data from WeChat : " + builder.toString());

		// 转换数据格式并验证签名
		WxPayData data = new WxPayData();
		try {
			data.FromXml(builder.toString());
		} catch (WxPayException ex) {
			// 若签名错误，则立即返回结果给微信支付后台
			WxPayData res = new WxPayData();
			res.SetValue("return_code", "FAIL");
			res.SetValue("return_msg", ex.getMessage());
			Log.error("Sign check error : " + res.ToXml());
			PrintWriter print = response.getWriter();
			print.write(res.ToXml());
			print.flush();
			print.close();
		}

		Log.info("Check sign success");
		return data;
	}

	// 派生类需要重写这个方法，进行不同的回调处理
	public abstract void ProcessNotify() throws NoSuchAlgorithmException,
			WxPayException, IOException, ParserConfigurationException,
			SAXException;
}
