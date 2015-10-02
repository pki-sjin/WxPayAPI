package com.wx.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wx.pay.business.OrderQuery;
import com.wx.pay.lib.WxPayException;

/**
 * Servlet implementation class OrderQueryPage
 */
@WebServlet("/example/OrderQueryPage")
public class OrderQueryPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderQueryPage() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String transaction_id = request.getParameter("transaction_id");
		String out_trade_no = request.getParameter("out_trade_no");

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter print = response.getWriter();

		if ((transaction_id == null || transaction_id.isEmpty())
				&& (out_trade_no == null || out_trade_no.isEmpty())) {
			print.write("微信订单号和商户订单号至少填写一个,微信订单号优先！");
			print.flush();
			print.close();
			return;
		}

		// 调用订单查询接口,如果内部出现异常则在页面上显示异常原因
		try {
			String result = OrderQuery.Run(transaction_id, out_trade_no);// 调用订单查询业务逻辑
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
