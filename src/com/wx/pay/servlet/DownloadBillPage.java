package com.wx.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wx.pay.business.DownloadBill;
import com.wx.pay.lib.WxPayException;

/**
 * Servlet implementation class DownloadBillPage
 */
@WebServlet("/example/DownloadBillPage")
public class DownloadBillPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadBillPage() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String bill_date = request.getParameter("bill_date");
		String bill_type = request.getParameter("bill_type");

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter print = response.getWriter();

		if (bill_date == null || bill_date.isEmpty()) {
			print.write("请输入对账单日期！");
			print.flush();
			print.close();
			return;
		}

		// 调用下载对账单接口,如果内部出现异常则在页面上显示异常原因
		try {
			String result = DownloadBill.Run(bill_date, bill_type);
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
