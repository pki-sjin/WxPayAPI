package com.wx.pay.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wx.pay.lib.WxPayConfig;
import com.wx.pay.lib.WxPayException;

/// <summary>
/// 微信支付协议接口数据类，所有的API接口通信都依赖这个数据结构，
/// 在调用接口之前先填充各个字段的值，然后进行接口通信，
/// 这样设计的好处是可扩展性强，用户可随意对协议进行更改而不用重新设计数据结构，
/// 还可以随意组合出不同的协议数据包，不用为每个协议设计一个数据包结构
/// </summary>
public class WxPayData {

	private static Logger Log = Logger.getLogger(WxPayData.class);

	public WxPayData() {

	}

	// 采用排序的Dictionary的好处是方便对数据包进行签名，不用再签名之前再做一次排序
	private TreeMap<String, Object> m_values = new TreeMap<String, Object>();

	/**
	 * 设置某个字段的值
	 * 
	 * @param key
	 *            字段名
	 * @param value
	 *            字段值
	 */
	public void SetValue(String key, Object value) {
		m_values.put(key, value);
	}

	/**
	 * 根据字段名获取某个字段的值
	 * 
	 * @param key
	 *            字段名
	 * @return key对应的字段值
	 */
	public Object GetValue(String key) {
		return m_values.get(key);
	}

	/**
	 * 判断某个字段是否已设置
	 * 
	 * @param key
	 *            字段名
	 * @return 若字段key已被设置，则返回true，否则返回false
	 */
	public boolean IsSet(String key) {
		return m_values.containsKey(key);
	}

	/**
	 * @将Dictionary转成xml
	 * @return 经转换得到的xml串
	 * @throws WxPayException
	 **/
	public String ToXml() throws WxPayException {
		// 数据为空时不能转化为xml格式
		if (0 == m_values.size()) {
			Log.error("WxPayData数据为空!");
			throw new WxPayException("WxPayData数据为空!");
		}

		String xml = "<xml>";
		for (Entry<String, Object> pair : m_values.entrySet()) {
			// 字段值不能为null，会影响后续流程
			if (pair.getValue() == null) {
				Log.error("WxPayData内部含有值为null的字段!");
				throw new WxPayException("WxPayData内部含有值为null的字段!");
			}

			if (pair.getValue() instanceof Integer) {
				xml += "<" + pair.getKey() + ">" + pair.getValue() + "</"
						+ pair.getKey() + ">";
			} else if (pair.getValue() instanceof String) {
				xml += "<" + pair.getKey() + ">" + "<![CDATA["
						+ pair.getValue() + "]]></" + pair.getKey() + ">";
			} else// 除了string和int类型不能含有其他数据类型
			{
				Log.error("WxPayData字段数据类型错误!");
				throw new WxPayException("WxPayData字段数据类型错误!");
			}
		}
		xml += "</xml>";
		return xml;
	}

	/**
	 * @将xml转为WxPayData对象并返回对象内部的数据
	 * @param string
	 *            待转换的xml串
	 * @return 经转换得到的Dictionary
	 * @throws WxPayException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public TreeMap<String, Object> FromXml(String xml) throws WxPayException,
			ParserConfigurationException, SAXException, IOException {
		if (xml == null || xml.isEmpty()) {
			Log.error("将空的xml串转换为WxPayData不合法!");
			throw new WxPayException("将空的xml串转换为WxPayData不合法!");
		}

		DocumentBuilderFactory documentBuildFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder doccumentBuilder = documentBuildFactory
				.newDocumentBuilder();
		Document document = doccumentBuilder.parse(new ByteArrayInputStream(xml
				.getBytes("UTF-8")));
		Node xmlNode = document.getFirstChild();// 获取到根节点<xml>
		NodeList nodes = xmlNode.getChildNodes();
		for (int i = 0, length = nodes.getLength(); i < length; i++) {
			Node xn = nodes.item(i);
			if (xn instanceof Element) {
				Element xe = (Element) xn;
				m_values.put(xe.getNodeName(), xe.getTextContent());// 获取xml的键值对到WxPayData内部的数据中
			}
		}

		try {
			// 2015-06-29 错误是没有签名
			if (m_values.get("return_code") == null
					|| !m_values.get("return_code").toString()
							.equalsIgnoreCase("SUCCESS")) {
				return m_values;
			}
			CheckSign();// 验证签名,不通过会抛异常
		} catch (WxPayException | NoSuchAlgorithmException
				| UnsupportedEncodingException ex) {
			throw new WxPayException(ex.getMessage());
		}

		return m_values;
	}

	/**
	 * @throws WxPayException
	 * @throws UnsupportedEncodingException
	 * @Dictionary格式转化成url参数格式 @ return url格式串, 该串不包含sign字段值
	 */
	public String ToUrl() throws WxPayException, UnsupportedEncodingException {
		String buff = "";
		for (Entry<String, Object> pair : m_values.entrySet()) {
			if (pair.getValue() == null) {
				Log.error("WxPayData内部含有值为null的字段!");
				throw new WxPayException("WxPayData内部含有值为null的字段!");
			}

			if (!pair.getKey().equalsIgnoreCase("sign")
					&& !pair.getValue().toString().equalsIgnoreCase("")) {
				buff += pair.getKey() + "=" + pair.getValue().toString() + "&";
			}
		}
		String regpattern = "&+$";
		Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(buff);
		if (m.find()) {
			buff = buff.substring(0, buff.length() - 1);
		}
		return buff;
	}

	/**
	 * @Dictionary格式化成Json
	 * @return json串数据
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public String ToJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = mapper.writeValueAsString(m_values);
		return jsonStr;
	}

	/**
	 * @throws WxPayException
	 * @values格式化成能在Web页面上显示的结果（因为web页面上不能直接输出xml格式的字符串）
	 */
	public String ToPrintStr() throws WxPayException {
		String str = "";
		for (Entry<String, Object> pair : m_values.entrySet()) {
			if (pair.getValue() == null) {
				Log.error("WxPayData内部含有值为null的字段!");
				throw new WxPayException("WxPayData内部含有值为null的字段!");
			}

			str += pair.getKey() + "=" + pair.getValue().toString() + "<br>";
		}

		return str;
	}

	/**
	 * @生成签名，详见签名生成算法
	 * @return 签名, sign字段不参加签名
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String MakeSign(String type) throws WxPayException, NoSuchAlgorithmException,
			UnsupportedEncodingException {
		// 转url格式
		String str = ToUrl();
		// 在string后加入API KEY
		str += "&key=" + WxPayConfig.KEY;
		// MD5加密
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(str.getBytes("UTF-8"));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			String hex = Integer.toHexString(0xFF & array[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		// 所有字符转为大写
		return sb.toString().toUpperCase();
	}

	/**
	 * 
	 * 检测签名是否正确 正确返回true，错误抛异常
	 * 
	 * @throws WxPayException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public boolean CheckSign() throws WxPayException, NoSuchAlgorithmException,
			UnsupportedEncodingException {
		// 如果没有设置签名，则跳过检测
		if (!IsSet("sign")) {
			Log.error("WxPayData签名存在但不合法!");
			throw new WxPayException("WxPayData签名存在但不合法!");
		}
		// 如果设置了签名但是签名为空，则抛异常
		else if (GetValue("sign") == null
				|| GetValue("sign").toString().equalsIgnoreCase("")) {
			Log.error("WxPayData签名存在但不合法!");
			throw new WxPayException("WxPayData签名存在但不合法!");
		}

		// 获取接收到的签名
		String return_sign = GetValue("sign").toString();

		// 在本地计算新的签名
		String cal_sign = MakeSign("MD5");

		if (cal_sign.equalsIgnoreCase(return_sign)) {
			return true;
		}

		Log.error("WxPayData签名验证错误!");
		throw new WxPayException("WxPayData签名验证错误!");
	}

	/**
	 * @获取Dictionary
	 */
	public TreeMap<String, Object> GetValues() {
		return m_values;
	}
}
