package com.wx.pay.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;

import com.wx.pay.cert.Cert;

/// <summary>
/// http连接基础类，负责底层的http通信
/// </summary>
public class HttpService {

	private static Logger Log = Logger.getLogger(HttpService.class);

	public static String Post(String xml, String url, boolean isUseCert,
			int timeout) throws WxPayException {
		System.gc(); // 垃圾回收，回收没有正常关闭的http连接

		String result = "";// 返回结果

		HttpURLConnection request = null;
		InputStream response = null;
		OutputStream reqStream = null;

		try {
			/***************************************************************
			 * 下面设置HttpWebRequest的相关属性
			 * ************************************************************/
			if (url.toLowerCase().startsWith("https")) {
				HttpsURLConnection httpsRequest = (HttpsURLConnection) new URL(
						url).openConnection();

				// 是否使用证书
				if (isUseCert) {
					KeyStore ks = KeyStore.getInstance("PKCS12");
					ks.load(new Cert().getClass().getResourceAsStream(
							WxPayConfig.SSLCERT_PATH),
							WxPayConfig.SSLCERT_PASSWORD.toCharArray());
					KeyManagerFactory kmf = KeyManagerFactory
							.getInstance("SunX509");
					kmf.init(ks, WxPayConfig.SSLCERT_PASSWORD.toCharArray());
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(kmf.getKeyManagers(), null, null);
					httpsRequest.setSSLSocketFactory(sc.getSocketFactory());
				}
				request = httpsRequest;
			} else {
				request = (HttpURLConnection) new URL(url).openConnection();
			}

			request.setRequestMethod("POST");
			request.setDoOutput(true);
			request.setConnectTimeout(timeout * 1000);

			// 设置POST的数据类型和长度
			request.addRequestProperty("Content-Type", "text/xml");
			byte[] data = xml.getBytes("UTF-8");

			// 往服务器写入数据
			reqStream = request.getOutputStream();
			reqStream.write(data, 0, data.length);

			// 获取服务端返回
			response = request.getInputStream();

			StringBuffer sb = new StringBuffer();
			byte[] d = new byte[1024];
			int chunk = 0;

			while ((chunk = response.read(d)) != -1) {
				sb.append(new String(d, 0, chunk, "UTF-8"));
			}

			result = sb.toString();
		} catch (Exception e) {
			Log.error(e.toString());
			throw new WxPayException(e.toString());
		} finally {
			// 关闭连接和流
			if (reqStream != null) {
				try {
					reqStream.close();
				} catch (IOException e) {
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}

	// / <summary>
	// / 处理http GET请求，返回数据
	// / </summary>
	// / <param name="url">请求的url地址</param>
	// / <returns>http GET成功后返回的数据，失败抛WebException异常</returns>
	public static String Get(String url) throws WxPayException {
		System.gc();
		String result = "";

		HttpURLConnection request = null;
		InputStream response = null;

		// 请求url以获取数据
		try {
			/***************************************************************
			 * 下面设置HttpWebRequest的相关属性
			 * ************************************************************/
			request = (HttpURLConnection) new URL(url).openConnection();

			request.setRequestMethod("GET");

			// 获取服务端返回
			response = request.getInputStream();

			StringBuffer sb = new StringBuffer();
			byte[] d = new byte[1024];
			int chunk = 0;

			while ((chunk = response.read(d)) != -1) {
				sb.append(new String(d, 0, chunk, "UTF-8"));
			}

			result = sb.toString();
		} catch (Exception e) {
			Log.error(e.toString());
			throw new WxPayException(e.toString());
		} finally {
			// 关闭连接和流
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}
}
