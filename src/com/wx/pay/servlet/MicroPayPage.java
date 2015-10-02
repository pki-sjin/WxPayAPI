package com.wx.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wx.pay.business.MicroPay;
import com.wx.pay.lib.WxPayException;

/**
 * Servlet implementation class MicroPayPage
 */
@WebServlet("/example/MicroPayPage")
public class MicroPayPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MicroPayPage() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String auth_code = request.getParameter("auth_code");
		String body = request.getParameter("body");
		String fee = request.getParameter("fee");

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter print = response.getWriter();

		if (auth_code == null || auth_code.isEmpty()) {
			print.write("请输入授权码！");
			print.flush();
			print.close();
			return;
		}
		if (body == null || body.isEmpty()) {
			print.write("请输入商品描述！");
			print.flush();
			print.close();
			return;
		}
		if (fee == null || fee.isEmpty()) {
			print.write("请输入商品总金额！");
			print.flush();
			print.close();
			return;
		}
		// 调用刷卡支付,如果内部出现异常则在页面上显示异常原因
		try {
			String result = MicroPay.Run(body, fee, auth_code);
			print.write(result);
		} catch (WxPayException ex) {
			print.write(ex.toString());
		} catch (Exception ex) {
			print.write(ex.toString());
		}

		print.flush();
		print.close();
	}
}
