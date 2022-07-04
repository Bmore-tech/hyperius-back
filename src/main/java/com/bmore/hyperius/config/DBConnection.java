/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmore.hyperius.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Conexión a la Base de Datos temporal, se migrará a Spring JDBC la
 * configuración para JNDI.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 10-08-2020
 */
public class DBConnection {

	/**
	 * Obtienen una instancia para la conexión a la Base de Datos.
	 * 
	 * @return Instancia de {@link Connection}.
	 */
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
