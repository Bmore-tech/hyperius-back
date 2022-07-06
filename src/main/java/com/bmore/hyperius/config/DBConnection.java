/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmore.hyperius.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * Conexión a la Base de Datos temporal, se migrará a Spring JDBC la
 * configuración para JNDI.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 10-08-2020
 */

 @Slf4j
@Component
@Repository
public class DBConnection {

  @Autowired
  private JdbcTemplate jdbcTemplate;

	/**
	 * Obtienen una instancia para la conexión a la Base de Datos.
	 * 
	 * @return Instancia de {@link Connection}.
	 */
	public Connection createConnection() {
    try {
      return jdbcTemplate.getDataSource().getConnection();
    } catch (SQLException e) {
      log.error("Error", e);
    }

    return null;
		// Context initContext = null;
		// Connection connection = null;
		// DataSource dataSource = null;

		// try {
		// 	initContext = new InitialContext();
		// 	dataSource = (DataSource) initContext.lookup("java:/BCPS");
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

	/**
	 * Cierra la instancia de {@link Connection} previamente abierta.
	 * 
	 * @param connection instancia de {@link Connection}.
	 */
	static public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {

			}
		}
	}
}
