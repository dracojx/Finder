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
	
	private Connection connectDP1;
	private Connection connectQP1;
	private Connection connectPP1;
	
	private ConnectionUtil() {
		try {
			connectDP1 = connectDP1();
			connectQP1 = connectQP1();
			connectPP1 = connectPP1();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Connection connectDP1() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/dp1");
		return ds.getConnection();
	}
	
	private static Connection connectQP1() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/qp1");
		return ds.getConnection();
	}
	
	private static Connection connectPP1() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/pp1");
		return ds.getConnection();
	}
	
	private Connection getConnectDP1() throws Exception {
		if(connectDP1.isClosed()) {
			connectDP1 = connectDP1();
		}
		return connectDP1;
	}

	private Connection getConnectQP1() throws Exception {
		if(connectQP1.isClosed()) {
			connectQP1 = connectQP1();
		}
		return connectQP1;
	}

	private Connection getConnectPP1() throws Exception {
		if(connectPP1.isClosed()) {
			connectPP1 = connectQP1();
		}
		return connectPP1;
	}

	public static Connection getConnect(String client) throws Exception {
		if(Constants.CLIENT_DP1.equals(client)) {
			return instance.getConnectDP1();
		} else if(Constants.CLIENT_QP1.equals(client)) {
			return instance.getConnectQP1();
		} else if(Constants.CLIENT_PP1.equals(client)) {
			return instance.getConnectPP1();
		}
		return null;
	}
	
}
