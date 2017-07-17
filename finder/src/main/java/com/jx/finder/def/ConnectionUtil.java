package com.jx.finder.def;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/** 
 * @author Draco
 * @version 1.0
 * @date 2017年7月16日
 *  
 */
public class ConnectionUtil {
	private static ConnectionUtil instance = new ConnectionUtil();
	
	private Connection connect300;
	private Connection connect500;
	
	private ConnectionUtil() {
		try {
			connect300 = connect300();
			connect500 = connect500();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Connection connect300() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/dp1");
		return ds.getConnection();
	}
	
	private static Connection connect500() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/qp1");
		return ds.getConnection();
	}
	
	private Connection getConnect300() throws Exception {
		if(connect300.isClosed()) {
			connect300 = connect300();
		}
		return connect300;
	}

	private Connection getConnect500() throws Exception {
		if(connect500.isClosed()) {
			connect500 = connect500();
		}
		return connect500;
	}

	public static Connection getConnect(String client) throws Exception {
		if("300".equals(client)) {
			return instance.getConnect300();
		} else if("500".equals(client)) {
			return instance.getConnect500();
		}
		return null;
	}
	
}
