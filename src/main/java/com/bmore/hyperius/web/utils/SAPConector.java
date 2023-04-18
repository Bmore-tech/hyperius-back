package com.bmore.hyperius.web.utils;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bmore.hyperius.web.service.SAPConectorService;
import com.bmore.hyperius.web.service.impl.SAPConectorServiceImpl;
import com.sap.conn.jco.JCoDestination;

public class SAPConector extends Thread {

	private JCoDestination asyncDestination;
	private Connection asyncConnection;
	// private DocInvBean docInvBean;

	//private Logger log = Logger.getLogger(SAPConector.class.getName());
	
	public SAPConector(JCoDestination asyncDestination// , Connection asyncConnection , DocInvBean docInvBean
	) {
	
		super();
		this.asyncDestination = asyncDestination;
		this.asyncConnection = asyncConnection;
		// this.docInvBean = docInvBean;
	}

	@Override
	public void run() {
		// log.log(Level.INFO, "ZiacmfMovInitialRuntime - onInit - Create runtime connection - docId "+docInvBean.getDocInvId());
		ConnectionManager connectionManager = new ConnectionManager();
		//Connection con =  connectionManager.createConnection();

		//log.log(Level.INFO, "ZiacmfMovInitialRuntime - onInit docId "+docInvBean.getDocInvId());
		// new SapConciliationWorkService().inventorySnapShot(docInvBean, asyncConnection, asyncDestination);
		new SAPConectorServiceImpl().inventorySnapShot( asyncDestination);//docInvBean, con,
		// log.log(Level.INFO, "ZiacmfMovInitialRuntime - onEnd docId"+docInvBean.getDocInvId());
		
		// log.log(Level.INFO, "ZiacmfMovInitialRuntime - onInit - Close runtime connection - docId "+docInvBean.getDocInvId());
		// connectionManager.CloseConnection(con);
	}

}
