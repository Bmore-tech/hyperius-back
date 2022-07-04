package com.bmore.hyperius.config;

import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class DBConnectionMob {
	private static final Logger LOCATION = LoggerFactory.getLogger(DBConnectionMob.class);

	static public Connection createConnection() {
		Context initContext = null;
		Connection connection = null;
		DataSource dataSource = null;
		try {
			initContext = new InitialContext();
			dataSource = (DataSource) initContext.lookup("java:/BCPS");
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		if (dataSource != null) {
			try {
				connection = dataSource.getConnection();
				return connection;
			} catch (SQLException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	static public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOCATION.error(e.toString());
			}
		}
	}
}
