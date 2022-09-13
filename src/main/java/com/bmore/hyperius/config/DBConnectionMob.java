package com.bmore.hyperius.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class DBConnectionMob {
	// private static final Logger log = LoggerFactory.getLogger(DBConnectionMob.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

	public Connection createConnection() {
		// Context initContext = null;
		// Connection connection = null;
		// DataSource dataSource = null;

    try {
      return jdbcTemplate.getDataSource().getConnection();
    } catch (SQLException e) {
      log.error("Error", e);
    }

    return null;
		// try {
			// initContext = new InitialContext();
			// dataSource = (DataSource) initContext.lookup("java:/BCPS");
		// } catch (NamingException e) {
		// 	e.printStackTrace();
		// 	return null;
		// }
		// if (dataSource != null) {
		// 	try {
		// 		connection = dataSource.getConnection();
		// 		return connection;
		// 	} catch (SQLException e) {
		// 		return null;
		// 	}
		// } else {
		// 	return null;
		// }
	}
	static public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error(e.toString());
			}
		}
	}
}
