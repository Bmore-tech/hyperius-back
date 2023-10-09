package com.bmore.hyperius.web.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bmore.hyperius.web.dto.SAP.JCOConnDTO;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

@Service
public class ConnectionManager {

	private final RestTemplate restTemplate;

	// public static ConnectionBean connectionBean = new ConnectionBean();
	private Logger log = Logger.getLogger(this.getClass().getName());

	public ConnectionManager(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public JCoDestination getSapConnection(String url, String destinationName) {
		JCoDestination destination = null;
		try {
			if (!Environment.isDestinationDataProviderRegistered()) {
				log.info("Registering Custom Data Provider");
				DestinationDataProvider destinationAux = new DestinationDataProvider() {
					@Override
					public Properties getDestinationProperties(String s) throws DataProviderException {
						Properties props = new Properties();
//						props.setProperty(DestinationDataProvider.JCO_ASHOST, "20.98.240.147"); // Host de SAP
						props.setProperty(DestinationDataProvider.JCO_ASHOST, "10.1.0.5"); // Host de SAP
						props.setProperty(DestinationDataProvider.JCO_SYSNR, "00"); // Número de sistema
						props.setProperty(DestinationDataProvider.JCO_CLIENT, "800"); // Cliente de SAP
						props.setProperty(DestinationDataProvider.JCO_USER, "ECCBMORE001S"); // Usuario de SAP
						props.setProperty(DestinationDataProvider.JCO_PASSWD,"Temporal.01"); // Contraseña de SAP
//						props.setProperty(DestinationDataProvider.JCO_LANG, "es"); // Idioma del usuario
						return props;
					}

					@Override
					public boolean supportsEvents() {
						return false;
					}

					@Override
					public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {

					}
				};
				Environment.registerDestinationDataProvider(destinationAux);
			} else {
				log.info("Already Registered Data Provider");
			}
			destination = JCoDestinationManager.getDestination("SAPConnection");
			log.info( destination.getDestinationName());

			destination.ping();
			log.info( "hace ping");
		} catch (Exception e) {
			log.info("Error al conectar a sap " + e.getMessage());
		}
		return destination;
	}

	private void createDestinationDataFile(String destinationName, Properties connectProperties) {
		File destCfg = new File(destinationName + ".jcoDestination");
		log.info("crea el file");
		try {
			if (destCfg.exists() && !destCfg.isDirectory())
				destCfg.delete();
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, "for tests only !!!");
			fos.close();
			log.info("crea el file2");
		} catch (Exception e) {
			log.info("error en file " + e);
			throw new RuntimeException("Unable to create the destination files", e);
		}
	}

	public JCoDestination getSapConnection2(String destinationName) throws JCoException, RuntimeException, Exception {
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
