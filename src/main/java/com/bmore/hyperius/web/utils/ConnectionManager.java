package com.bmore.hyperius.web.utils;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

public class ConnectionManager {
	@Resource
	private DataSource dataSource;

	//public static ConnectionBean connectionBean = new ConnectionBean();
	private Logger log = Logger.getLogger( this.getClass().getName());

	static {
		//connectionBean.setDatasource("CUTOVERDS");
		// connectionBean.setDbName("INV_CIC_DB");
		// connectionBean.setHostname("localhost");
		// connectionBean.setUser("sa");
		// connectionBean.setPassword("1234");
		// connectionBean.setPort("1433");
		
	}



	public JCoDestination getSapConnection(String destinationName) throws JCoException, RuntimeException, Exception {
		JCoDestination destination = null;
		try {
			destination = JCoDestinationManager.getDestination(destinationName);
		} catch (JCoException e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return destination;
	}

}
