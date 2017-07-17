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
import com.jx.finder.def.Constants;
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
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(20);
		String sql = "SELECT MSG_ID, STATUS, SENT_RECV_TIME, ACTION_NAME, MSG_BYTES FROM BC_MSG "
				+ "WHERE ACTION_NAME IN ("
				+ "'SI_SD134_ECC_Out_Asy',"
				+ "'SI_SD135_ECC_Out_Asy',"
				+ "'SI_SD136_ECC_Out_Asy',"
				+ "'SI_SD137_ECC_Out_Asy',"
				+ "'SI_SD138_ECC_Out_Asy',"
				+ "'SI_SD139_ECC_Out_Asy',"
				+ "'SI_SD140_In_Asy',"
				+ "'SI_SD145_ECC_Out_Asy',"
				+ "'SI_SD154_ECC_Out_Asy',"
				+ "'SI_SD155_ECC_Out_Asy',"
				+ "'SI_SD162_ECC_Out_Asy',"
				+ "'SI_SD173_ECC_Out_Asy',"
				+ "'SI_SD174_ECC_Out_Asy',"
				+ "'SI_SD175_ECC_Out_Asy',"
				+ "'SI_SD180_ECC_Out_Asy'" + ")";
		
		long time1 = System.currentTimeMillis();
		
		ResultSet rs = statement.executeQuery(sql);
		
		long time2 = System.currentTimeMillis();
		System.out.println("SQL: " + sql);
		System.out.println("Query Time: " + ((float)(time2 - time1) / 1000) + "s");
		
		while (rs.next()) {
			String actionName = rs.getString("ACTION_NAME");
			Message message = null;
			if (Constants.ACTION_NAME_SD134.equals(actionName)) {
				message = sd134(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD135.equals(actionName)) {
				message = sd135(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD136.equals(actionName)) {
				message = sd136(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD137.equals(actionName)) {
				message = sd137(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD138.equals(actionName)) {
				message = sd138(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD139.equals(actionName)) {
				message = sd139(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD140.equals(actionName)) {
				message = sd140(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD145.equals(actionName)) {
				message = sd145(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD154.equals(actionName)) {
				message = sd154(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD155.equals(actionName)) {
				message = sd155(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD162.equals(actionName)) {
				message = sd162(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD173.equals(actionName)) {
				message = sd173(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD174.equals(actionName)) {
				message = sd174(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD175.equals(actionName)) {
				message = sd175(rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD180.equals(actionName)) {
				message = sd180(rs, orderNumber);
			}

			if (message != null) {
				set.add(message);
			}
		}


		long time3 = System.currentTimeMillis();
		System.out.println("Parse Time: " + ((float)(time3 - time2) / 1000) + "s");
		
		rs.close();
		statement.close();
		
		return set;
	}

	private static Message sd134(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA")
					.element("HEADER");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("order_type") + " "
						+ element.elementText("message_purpose");
				return Message.create(guid, "SD134", code, status, time);
			}
		}
		return null;
	}

	private static Message sd135(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA")
					.element("HEADER");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZDSI0005";
				return Message.create(guid, "SD135", code, status, time);
			}
		}
		return null;
	}

	private static Message sd136(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA")
					.element("HEADER");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZDSI0006";
				return Message.create(guid, "SD136", code, status, time);
			}
		}
		return null;
	}

	private static Message sd137(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA")
					.element("HEADER");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZWMI0070";
				return Message.create(guid, "SD137", code, status, time);
			}
		}
		return null;
	}

	private static Message sd138(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA")
					.element("HEADER");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZBILL";
				return Message.create(guid, "SD138", code, status, time);
			}
		}
		return null;
	}

	private static Message sd139(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZDELVRY";
				return Message.create(guid, "SD139", code, status, time);
			}
		}
		return null;
	}

	private static Message sd140(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<?xml");
		int endIndex = msgBytes.lastIndexOf(">") + 1;

		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("arg1")
					.element("ORDER");
			if (orderNumber.equalsIgnoreCase(element.elementText("BSTKD"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("ZSTATUS") + " "
						+ element.elementText("STATXT");
				return Message.create(guid, "SD140", code, status, time);
			}
		}
		return null;
	}

	private static Message sd145(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA").element("SD145");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = "送货 " + element.elementText("vtweg");
				return Message.create(guid, "SD145", code, status, time);
			}
		}
		return null;
	}

	private static Message sd154(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA").element("SD154");
			if (orderNumber.equalsIgnoreCase(element.elementText("BSTKD"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = "安装 " + element.elementText("BSARK");
				return Message.create(guid, "SD154", code, status, time);
			}
		}
		return null;
	}

	private static Message sd155(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;
	
		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA").element("SD155").element("header");
			if (orderNumber.equalsIgnoreCase(element.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("order_type") + " " + element.elementText("message_purpose");
				return Message.create(guid, "SD154", code, status, time);
			}
		}
		return null;
	}

	private static Message sd162(ResultSet rs, String orderNumber)
			throws Exception {

		SAXReader reader = new SAXReader();
		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;

		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("XML_DATA")
					.element("SD162").element("header");
			if (orderNumber.equalsIgnoreCase(element
					.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("order_type") + "  "
						+ element.elementText("message_purpose");
				return Message.create(guid, "SD162", code, status, time);
			}
		}
		return null;
	}

	private static Message sd173(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();

		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;

		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("SD173")
					.element("header");
			if (orderNumber.equalsIgnoreCase(element
					.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("order_type") + "  "
						+ element.elementText("message_purpose");
				return Message.create(guid, "SD173", code, status, time);
			}
		}
		return null;
	}

	private static Message sd174(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();

		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;

		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("SD174")
					.element("header");
			if (orderNumber.equalsIgnoreCase(element
					.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZDELVRY";
				return Message.create(guid, "SD174", code, status, time);
			}
		}
		return null;
	}

	private static Message sd175(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();

		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;

		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("SD175");
			if (orderNumber.equalsIgnoreCase(element
					.elementText("order_number"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("sales_type") + " ZDELVRY";
				return Message.create(guid, "SD175", code, status, time);
			}

		}

		return null;
	}

	private static Message sd180(ResultSet rs, String orderNumber)
			throws Exception {
		SAXReader reader = new SAXReader();

		Blob MSG_BYTES = rs.getBlob("MSG_BYTES");
		String msgBytes = new String(MSG_BYTES.getBytes(1,
				(int) MSG_BYTES.length()), "UTF-8");
		int beginIndex = msgBytes.indexOf("<n");
		int endIndex = msgBytes.lastIndexOf(">") + 1;

		if (beginIndex >= 0) {
			String xml = msgBytes.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			Element element = document.getRootElement().element("SD180");
			if (orderNumber.equalsIgnoreCase(element
					.elementText("ORDER_NUNBER"))) {
				String guid = rs.getString("MSG_ID");
				String status = rs.getString("STATUS");
				String time = DataUtil
						.parseTime(rs.getString("SENT_RECV_TIME"));
				String code = element.elementText("SALES_TYPE") + " ZBILL";
				return Message.create(guid, "SD180", code, status, time);
			}
		}

		return null;
	}

}
