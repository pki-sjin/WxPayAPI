package com.wx.pay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ProductPage
 */
@WebServlet("/example/ProductPage")
public class ProductPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProductPage() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String total_fee = request.getParameter("total_fee");
		if (request.getSession().getAttribute("openid") != null) {
			String openid = request.getSession().getAttribute("openid")
					.toString();
			String url = "example/JsApiPayPage.jsp?openid="
					+ openid + "&total_fee=" + total_fee;
			response.sendRedirect(url);
		} else {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter print = response.getWriter();
			print.write("页面缺少参数，请返回重试");
			print.flush();
			print.close();
		}
	}
}
