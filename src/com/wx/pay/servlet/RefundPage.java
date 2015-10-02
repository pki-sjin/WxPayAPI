package com.wx.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wx.pay.business.Refund;
import com.wx.pay.lib.WxPayException;

/**
 * Servlet implementation class RefundPage
 */
@WebServlet("/example/RefundPage")
public class RefundPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RefundPage() {
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
		String total_fee = request.getParameter("total_fee");
		String refund_fee = request.getParameter("refund_fee");

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter print = response.getWriter();

		if ((transaction_id == null || transaction_id.isEmpty())
				&& (out_trade_no == null || out_trade_no.isEmpty())) {
			print.write("微信订单号和商户订单号至少填一个！");
			print.flush();
			print.close();
			return;
		}
		if (total_fee == null || total_fee.isEmpty()) {
			print.write("订单总金额必填！");
			print.flush();
			print.close();
			return;
		}
		if (refund_fee == null || refund_fee.isEmpty()) {
			print.write("退款金额必填！");
			print.flush();
			print.close();
			return;
		}

		// 调用订单退款接口,如果内部出现异常则在页面上显示异常原因
		try {
			String result = Refund.Run(transaction_id, out_trade_no, total_fee,
					refund_fee);
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
