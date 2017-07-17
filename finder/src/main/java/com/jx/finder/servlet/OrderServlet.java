package com.jx.finder.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.gson.Gson;
import com.jx.finder.def.ConnectionUtil;
import com.jx.finder.def.DataUtil;
import com.jx.finder.def.Message;

/**
 * Servlet implementation class DataServlet
 */
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public OrderServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/order.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String client = request.getParameter("client");
		String orderNumber = request.getParameter("order_number");

		response.setContentType("application/json;charset=UTF-8");
		Writer out = response.getWriter();
		try {
			Gson gson = new Gson();
			String json = gson.toJson(find(client, orderNumber));
			out.write(json);
		} catch (Exception e) {
			String error = "{\"errorMessage\": \"" + e.getMessage() + "\"}";
			e.printStackTrace();
			out.write(error);
		}

		out.close();
	}

	private static Set<Message> find(String client, String orderNumber)
			throws Exception {
		Set<Message> set = new TreeSet<Message>();

		Connection conn = ConnectionUtil.getConnect(client);

		set.addAll(findSD162(conn, orderNumber));
		set.addAll(findSD173(conn, orderNumber));

		if (!set.isEmpty()) {
			set.addAll(findSD140(conn, orderNumber));
		}

		return set;
	}

	private static Set<Message> findSD140(Connection conn, String orderNumber)
			throws Exception {
		Set<Message> set = new TreeSet<Message>();
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(1);
		String sql = "SELECT MSG_ID, STATUS, SENT_RECV_TIME, MSG_BYTES FROM BC_MSG WHERE ACTION_NAME='SI_SD140_In_Asy'";
		ResultSet rs = statement.executeQuery(sql);

		SAXReader reader = new SAXReader();

		while (rs.next()) {
			Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
			String msgBytes = new String(MSG_BYTES.getBytes(1,
					(int) MSG_BYTES.length()), "UTF-8");
			int beginIndex = msgBytes.indexOf("<?xml");
			int endIndex = msgBytes.lastIndexOf(">") + 1;

			if (beginIndex >= 0) {
				String xml = msgBytes.substring(beginIndex, endIndex);
				Document document = reader.read(new ByteArrayInputStream(xml
						.getBytes("UTF-8")));
				Element order = document.getRootElement().element("arg1")
						.element("ORDER");
				if (orderNumber.equalsIgnoreCase(order.elementText("BSTKD"))) {
					String guid = rs.getString("MSG_ID");
					String status = rs.getString("STATUS");
					String time = DataUtil.parseTime(rs
							.getString("SENT_RECV_TIME"));
					String code = order.elementText("ZSTATUS") + " "
							+ order.elementText("STATXT");
					set.add(Message.create(guid, "SD140", code, status, time));
				}
			}
		}

		rs.close();
		statement.close();
		return set;
	}

	private static Set<Message> findSD162(Connection conn, String orderNumber)
			throws Exception {
		Set<Message> set = new TreeSet<Message>();
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(1);
		String sql = "SELECT MSG_ID, STATUS, SENT_RECV_TIME, MSG_BYTES FROM BC_MSG WHERE ACTION_NAME='SI_SD162_ECC_Out_Asy'";
		ResultSet rs = statement.executeQuery(sql);

		SAXReader reader = new SAXReader();

		while (rs.next()) {
			Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
			String msgBytes = new String(MSG_BYTES.getBytes(1,
					(int) MSG_BYTES.length()), "UTF-8");
			int beginIndex = msgBytes.indexOf("<n");
			int endIndex = msgBytes.lastIndexOf(">") + 1;

			if (beginIndex >= 0) {
				String xml = msgBytes.substring(beginIndex, endIndex);
				Document document = reader.read(new ByteArrayInputStream(xml
						.getBytes("UTF-8")));
				Element header = document.getRootElement().element("XML_DATA")
						.element("SD162").element("header");
				if (orderNumber.equalsIgnoreCase(header
						.elementText("order_number"))) {
					String guid = rs.getString("MSG_ID");
					String status = rs.getString("STATUS");
					String time = DataUtil.parseTime(rs
							.getString("SENT_RECV_TIME"));
					String code = header.elementText("order_type") + "  "
							+ header.elementText("message_purpose");
					set.add(Message.create(guid, "SD162", code, status, time));
				}
			}
		}

		rs.close();
		statement.close();
		return set;
	}

	private static Set<Message> findSD173(Connection conn, String orderNumber)
			throws Exception {
		Set<Message> set = new TreeSet<Message>();
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(10);
		String sql = "SELECT MSG_ID, STATUS, SENT_RECV_TIME, MSG_BYTES FROM BC_MSG WHERE ACTION_NAME='SI_SD173_ECC_Out_Asy'";
		ResultSet rs = statement.executeQuery(sql);

		SAXReader reader = new SAXReader();

		while (rs.next()) {
			Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
			String msgBytes = new String(MSG_BYTES.getBytes(1,
					(int) MSG_BYTES.length()), "UTF-8");
			int beginIndex = msgBytes.indexOf("<n");
			int endIndex = msgBytes.lastIndexOf(">") + 1;

			if (beginIndex >= 0) {
				String xml = msgBytes.substring(beginIndex, endIndex);
				Document document = reader.read(new ByteArrayInputStream(xml
						.getBytes("UTF-8")));
				Element header = document.getRootElement().element("SD173")
						.element("header");
				if (orderNumber.equalsIgnoreCase(header
						.elementText("order_number"))) {
					String guid = rs.getString("MSG_ID");
					String status = rs.getString("STATUS");
					String time = DataUtil.parseTime(rs
							.getString("SENT_RECV_TIME"));
					String code = header.elementText("order_type") + "  "
							+ header.elementText("message_purpose");
					set.add(Message.create(guid, "SD173", code, status, time));
				}
			}
		}

		rs.close();
		statement.close();
		return set;
	}

}
