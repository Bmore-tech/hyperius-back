package com.bmore.prueba.web.utils.export.xmlgeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLThread extends Thread {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	String entrega;
	String werks;

	public XMLThread() {

	}

	public XMLThread(String entrega, String werks) {
		this.entrega = entrega;
		this.werks = werks;
	}

	public void run() {
		XMLCreateBO xml_BO = new XMLCreateBO();
		LOCATION.error("GENERATE XML : " + xml_BO.generateXML(entrega, werks));

	}

}
