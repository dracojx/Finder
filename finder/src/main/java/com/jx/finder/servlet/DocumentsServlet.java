package com.jx.finder.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.gson.Gson;
import com.jx.finder.def.ConnectionUtil;
import com.jx.finder.def.Constants;
import com.jx.finder.def.ConvertUtil;
import com.jx.finder.def.Message;
import com.jx.finder.def.SD134Handler;
import com.jx.finder.def.SD135Handler;
import com.jx.finder.def.SD136Handler;
import com.jx.finder.def.SD137Handler;
import com.jx.finder.def.SD138Handler;
import com.jx.finder.def.SD139Handler;
import com.jx.finder.def.SD140Handler;
import com.jx.finder.def.SD145Handler;
import com.jx.finder.def.SD154Handler;
import com.jx.finder.def.SD155Handler;
import com.jx.finder.def.SD162Handler;
import com.jx.finder.def.SD173Handler;
import com.jx.finder.def.SD174Handler;
import com.jx.finder.def.SD175Handler;
import com.jx.finder.def.SD180Handler;
import com.jx.finder.def.WMS023Handler;
import com.jx.finder.def.WMS025Handler;
import com.jx.finder.def.WMS027Handler;
import com.jx.finder.def.WMS095Handler;

/**
 * Servlet implementation class DataServlet
 */
public class DocumentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public DocumentsServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/documents.html").forward(request,
				response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String client = request.getParameter("client");
		String type = request.getParameter("type");
		String keyword = request.getParameter("keyword").trim();

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		Writer out = response.getWriter();
		try {
			Gson gson = new Gson();
			String json = "";
			if (Constants.TYPE_ORDER.equals(type)) {
				json = gson.toJson(findOrder(client, keyword));
			} else if (Constants.TYPE_DELIVERY.equals(type)) {
				json = gson.toJson(findDelivery(client, keyword));
			}
			out.write(json);
		} catch (Exception e) {
			String error = "{\"errorMessage\": \"" + e.getMessage() + "\"}";
			e.printStackTrace();
			out.write(error);
		}

		out.close();
	}

	private static List<Message> findOrder(String client, String orderNumber)
			throws Exception {
		List<Message> list = new ArrayList<Message>();

		Connection conn = ConnectionUtil.getConnect(client);
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(20);
		String sql = "SELECT "
				+ " MSG_ID, "
				+ " STATUS,	"
				+ " SENT_RECV_TIME,	"
				+ " ACTION_NAME,	"
				+ " MSG_BYTES	"
				+ "FROM BC_MSG	"
				+ "WHERE "
				+ "(TO_SERVICE_NAME = 'BS_OTM' AND ACTION_NAME = 'SI_SD140_In_Asy') "
				+ " OR (FROM_SERVICE_NAME  = 'BS_OTM' AND ACTION_NAME IN ( "
				+ "	'SI_SD134_ECC_Out_Asy', "
				+ "	'SI_SD135_ECC_Out_Asy', "
				+ " 'SI_SD136_ECC_Out_Asy', "
				+ "	'SI_SD137_ECC_Out_Asy', "
				+ "	'SI_SD138_ECC_Out_Asy', "
				+ "	'SI_SD139_ECC_Out_Asy', "
				+ "	'SI_SD145_ECC_Out_Asy', "
				+ "	'SI_SD154_ECC_Out_Asy', "
				+ "	'SI_SD155_ECC_Out_Asy', "
				+ "	'SI_SD162_ECC_Out_Asy', "
				+ "	'SI_SD173_ECC_Out_Asy', "
				+ "	'SI_SD174_ECC_Out_Asy', "
				+ "	'SI_SD175_ECC_Out_Asy', "
				+ "	'SI_SD180_ECC_Out_Asy')) ";

		long time1 = System.currentTimeMillis();

		ResultSet rs = statement.executeQuery(sql);

		long time2 = System.currentTimeMillis();
		System.out.println("SQL: " + sql);
		System.out.println("Query Time: " + ((float) (time2 - time1) / 1000)
				+ "s");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		while (rs.next()) {
			String actionName = rs.getString("ACTION_NAME");
			Message message = null;
			if (Constants.ACTION_NAME_SD134.equals(actionName)) {
				message = sd134(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD135.equals(actionName)) {
				message = sd135(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD136.equals(actionName)) {
				message = sd136(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD137.equals(actionName)) {
				message = sd137(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD138.equals(actionName)) {
				message = sd138(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD139.equals(actionName)) {
				message = sd139(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD140.equals(actionName)) {
				message = sd140ByOrderNumber(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD145.equals(actionName)) {
				message = sd145(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD154.equals(actionName)) {
				message = sd154(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD155.equals(actionName)) {
				message = sd155(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD162.equals(actionName)) {
				message = sd162(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD173.equals(actionName)) {
				message = sd173(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD174.equals(actionName)) {
				message = sd174(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD175.equals(actionName)) {
				message = sd175(parser, rs, orderNumber);
			} else if (Constants.ACTION_NAME_SD180.equals(actionName)) {
				message = sd180(parser, rs, orderNumber);
			}

			if (message != null) {
				list.add(message);
			}
		}

		long time3 = System.currentTimeMillis();
		System.out.println("Parse Time: " + ((float) (time3 - time2) / 1000)
				+ "s");

		rs.close();
		statement.close();

		Collections.sort(list);
		return list;
	}

	private static List<Message> findDelivery(String client, String delivery)
			throws Exception {
		List<Message> list = new ArrayList<Message>();

		Connection conn = ConnectionUtil.getConnect(client);
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(20);
		
		String sql = "SELECT "
				+ " MSG_ID,	"
				+ " STATUS,	"
				+ " SENT_RECV_TIME,	"
				+ " ACTION_NAME,	"
				+ " MSG_BYTES	"
				+ " FROM BC_MSG	"
				+ " WHERE "
				+ " (TO_SERVICE_NAME = 'BC_LMIS' AND ACTION_NAME IN ('SI_WMS023_In_Asy', 'SI_WMS025_In_Asy', 'SI_WMS027_In_Asy')) "
				+ " OR (TO_SERVICE_NAME = 'BC_3CDRG' AND ACTION_NAME = 'SI_WMS095_In_Asy')";

		long time1 = System.currentTimeMillis();

		ResultSet rs = statement.executeQuery(sql);

		long time2 = System.currentTimeMillis();
		System.out.println("SQL: " + sql);
		System.out.println("Query Time: " + ((float) (time2 - time1) / 1000)
				+ "s");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		while (rs.next()) {
			String actionName = rs.getString("ACTION_NAME");
			Message message = null;
			
			if(Constants.ACTION_NAME_WMS023.equals(actionName)) {
				message = wms023(parser, rs, delivery);
			} else if(Constants.ACTION_NAME_WMS025.equals(actionName)) {
				message = wms025(parser, rs, delivery);
			} else if(Constants.ACTION_NAME_WMS027.equals(actionName)) {
				message = wms027(parser, rs, delivery);
			} else if (Constants.ACTION_NAME_WMS095.equals(actionName)) {
				message = wms095(parser, rs, delivery);
			}
			
			if (message != null) {
				list.add(message);
			}
		}

		long time3 = System.currentTimeMillis();
		System.out.println("Parse Time: " + ((float) (time3 - time2) / 1000)
				+ "s");

		rs.close();
		statement.close();

		Collections.sort(list);
		return list;
	}

	private static Message sd134(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD134Handler handler = SD134Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = handler.getOrderType() + " " + handler.getMessagePurpose();
			return Message.create(guid, "SD134", code, status, time);
		}
		
		return null;
	}

	private static Message sd135(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD135Handler handler = SD135Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZDSI0005 " + handler.getSalesType();
			return Message.create(guid, "SD135", code, status, time);
		}
		return null;
	}

	private static Message sd136(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD136Handler handler = SD136Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZDSI0006 " + handler.getSalesType();
			return Message.create(guid, "SD136", code, status, time);
		}
		return null;
	}

	private static Message sd137(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD137Handler handler = SD137Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZWMI0070 " + handler.getSalesType();
			return Message.create(guid, "SD137", code, status, time);
		}
		return null;
	}

	private static Message sd138(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD138Handler handler = SD138Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZBILL " + handler.getSalesType();
			return Message.create(guid, "SD138", code, status, time);
		}
		return null;
	}

	private static Message sd139(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD139Handler handler = SD139Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZDELVRY " + handler.getSalesType();
			return Message.create(guid, "SD139", code, status, time);
		}
		return null;
	}

	private static Message sd140ByOrderNumber(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"));
		SD140Handler handler = SD140Handler.create(Constants.TAG_SD140_ORDER_NUMBER, orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = handler.getStatus() + " " + handler.getText();
			return Message.create(guid, "SD140", code, status, time);
		}
		
		return null;
	}

	private static Message sd145(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD145Handler handler = SD145Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "送货";
			return Message.create(guid, "SD145", code, status, time);
		}
		return null;
	}

	private static Message sd154(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD154Handler handler = SD154Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "安装";
			return Message.create(guid, "SD154", code, status, time);
		}
		return null;
	}

	private static Message sd155(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD155Handler handler = SD155Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = handler.getOrderType() + " " + handler.getMessagePurpose();
			return Message.create(guid, "SD155", code, status, time);
		}
		return null;
	}

	private static Message sd162(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD162Handler handler = SD162Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = handler.getOrderType() + " " + handler.getMessagePurpose();
			return Message.create(guid, "SD162", code, status, time);
		}
		return null;
	}

	private static Message sd173(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD173Handler handler = SD173Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = handler.getOrderType() + " " + handler.getMessagePurpose();
			return Message.create(guid, "SD173", code, status, time);
		}
		return null;
	}

	private static Message sd174(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD174Handler handler = SD174Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZDELVRY " + handler.getSalesType();
			return Message.create(guid, "SD174", code, status, time);
		}
		return null;
	}

	private static Message sd175(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD175Handler handler = SD175Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZDELVRYDS " + handler.getSalesType();
			return Message.create(guid, "SD175", code, status, time);
		}
		return null;
	}
	
	private static Message sd180(SAXParser parser, ResultSet rs, String orderNumber) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"), "<n", ">");
		SD180Handler handler = SD180Handler.create(orderNumber);
		parser.parse(is, handler);
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "ZBILL " + handler.getSalesType();
			return Message.create(guid, "SD180", code, status, time);
		}
		return null;
	}

	private static Message wms023(SAXParser parser, ResultSet rs, String delivery) throws Exception {
		InputStream is = ConvertUtil.blobToInputStreamSub(rs.getBlob("MSG_BYTES"));
		WMS023Handler handler = WMS023Handler.create(delivery);
		parser.parse(is, handler);
		
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "创建交货单";
			String keyword = "订单  " + handler.getOrderNumber();
			return Message.create(guid, "WMS023", code, status, time,
					keyword);
		}
		
		return null;
	}

	private static Message wms025(SAXParser parser, ResultSet rs, String delivery)
			throws Exception {
		InputStream is = ConvertUtil.blobToInputStreamSub(rs.getBlob("MSG_BYTES"));
		WMS025Handler handler = WMS025Handler.create(delivery);
		parser.parse(is, handler);
		
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "交货单取消";
			return Message.create(guid, "WMS025", code, status, time);
		}
		
		return null;
	}

	private static Message wms027(SAXParser parser, ResultSet rs, String delivery)
			throws Exception {
		InputStream is = ConvertUtil.blobToInputStreamSub(rs.getBlob("MSG_BYTES"));
		WMS027Handler handler = WMS027Handler.create(delivery);
		parser.parse(is, handler);
		
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "发货";
			return Message.create(guid, "WMS027", code, status, time);
		}
		
		return null;
	}

	private static Message wms095(SAXParser parser, ResultSet rs, String delivery) throws Exception {
		InputStream is = ConvertUtil.blobToInputStream(rs.getBlob("MSG_BYTES"));
		WMS095Handler handler = WMS095Handler.create(delivery);
		parser.parse(is, handler);
		
		if(handler.isRight()) {
			String guid = rs.getString("MSG_ID");
			String status = rs.getString("STATUS");
			String time = rs.getString("SENT_RECV_TIME");
			String code = "创建交货单";
			String keyword = "订单  " + handler.getOrderNumber();
			return Message.create(guid, "WMS095", code, status, time,
					keyword);
		}
		
		return null;
	}

}
