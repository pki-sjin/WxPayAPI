package com.wx.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wx.pay.business.RefundQuery;
import com.wx.pay.lib.WxPayException;

/**
 * Servlet implementation class RefundQueryPage
 */
@WebServlet("/example/RefundQueryPage")
public class RefundQueryPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RefundQueryPage() {
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
		String refund_id = request.getParameter("refund_id");
		String out_refund_no = request.getParameter("out_refund_no");

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter print = response.getWriter();

		if ((refund_id == null || refund_id.isEmpty())
				&& (out_refund_no == null || out_refund_no.isEmpty())
				&& (transaction_id == null || transaction_id.isEmpty())
				&& (out_trade_no == null || out_trade_no.isEmpty())) {
			print.write("微信订单号、商户订单号、商户退款单号、微信退款单号选填至少一个，微信退款单号优先！");
			print.flush();
			print.close();
			return;
		}

		// 调用退款查询接口,如果内部出现异常则在页面上显示异常原因
		try {
			String result = RefundQuery.Run(refund_id, out_refund_no,
					transaction_id, out_trade_no);
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
