package com.bmore.hyperius.web.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.repository.ControlPaletizadoraRepository;
import com.bmore.hyperius.web.rest.resquest.EtiquetaPtRequest;
import com.bmore.hyperius.web.service.EtiquetaPTService;
import com.bmore.hyperius.web.utils.print.EtiquetaDatasource;
import com.bmore.hyperius.web.utils.print.Etiquetas;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class EtiquetaPTServiceImpl implements EtiquetaPTService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ControlPaletizadoraRepository controlPaletizadoraRepository;

	@Autowired
	private ServletContext servletContext;

	@Override
	public ResponseEntity<Resource> etiquetaPt(EtiquetaPtRequest request) {
		String aufnr = (String) request.getAufnr();
		String key = (String) request.getKey();
		HttpHeaders headers = new HttpHeaders();

		String jasper = "";
		Etiquetas etiquetas = controlPaletizadoraRepository.obtieneHusParaImprimir(aufnr, key);
		String hu = "";
		InputStreamResource resource = null;
		File file = null;

		logger.info(etiquetas.toString());

		if (etiquetas.getResultDT().getId() == 1) {

			EtiquetaDatasource datasource = new EtiquetaDatasource();

			for (int x = 0; x < etiquetas.getItems().size(); x++) {
				hu = etiquetas.getItems().get(x).getEXIDV_HU();
				logger.info("ID ETIQUETA_:" + hu);
				jasper = "jasper/EtiquetaPT.jrxml";
				datasource.addEtiqueta(etiquetas.getItems().get(x));
			}

			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			logger.info("Iniciando...");
			String filePath = "Etiqueta" + hu + ".pdf";
			JasperReport jasperReport = null;
			JasperPrint jasperPrint = null;
			// JasperDesign jasperDesign = null;
			// Mostrar en pantalla PDF concatenado
			file = new File(filePath);

			// Debug
			logger.info("Contexto: " + servletContext.getRealPath(jasper));

			// jasperDesign = JRXmlLoader.load(jasper);
			try {
				jasperReport = JasperCompileManager.compileReport(servletContext.getRealPath(jasper));

				// Error
				jasperPrint = JasperFillManager.fillReport(jasperReport, null, datasource);

				JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
			} catch (JRException e1) {
				e1.printStackTrace();
			}

			logger.info("Datos del archivo:");
			logger.info("AbsolutePath: " + file.getAbsolutePath());
			logger.info("Name: " + file.getName());

			try {
				resource = new InputStreamResource(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				logger.error("Error al cargar: " + e.toString());
				e.printStackTrace();
			}

		}

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}
}
