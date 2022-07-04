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

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.HUsRepository;
import com.bmore.hyperius.web.repository.old.HUsRepositoryOld;
import com.bmore.hyperius.web.rest.resquest.CreateEtiquetaReportRequest;
import com.bmore.hyperius.web.service.EtiquetaViaEtiquetaService;
import com.bmore.hyperius.web.utils.print.EtiquetaDatasource;
import com.bmore.hyperius.web.utils.print.Etiquetas;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
public class EtiquetaViaEtiquetaServiceImpl implements EtiquetaViaEtiquetaService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HUsRepository hUsRepository;

	private HUsRepositoryOld husRepositoryOld = new HUsRepositoryOld();

	@Autowired
	private ServletContext servletContext;

	@Override
	public ResponseEntity<Resource> createEtiquetaReport(CreateEtiquetaReportRequest request, String token) {
		String jasper = "";
		HttpHeaders headers = new HttpHeaders();
		ResultDTO resultDT = new ResultDTO();
		String proceso = "";
		String entrega = "";

		String hus = "";
		String hu = "";

		for (int x = 0; x < request.getItem().size(); x++) {
			entrega = request.getItem().get(x).getVblen();
			proceso = request.getItem().get(x).getId();
			hus += "'" + request.getItem().get(x).getHu() + "',";

			hu = request.getItem().get(x).getHu();
		}

		if (hus.length() > 0) {
			hus = hus.substring(0, hus.length() - 1);
		}

		Etiquetas etiquetas = new Etiquetas();
		resultDT.setId(2);
		resultDT.setMsg("Falta parametro de proceso");

		logger.info("proceso: " + proceso);

		if (proceso.equals("1") || proceso.equals("3") || proceso.equals("5") || proceso.equals("6")) {
			// hus de VEKP, entrega standar sap o entrega saliente de
			// vidrio
			HUsRepositoryOld husRepositoryOldAux = new HUsRepositoryOld();

			etiquetas = husRepositoryOldAux.obtieneDatosHusVekp(hus);

			// intenta recuperar etiquetas de LQUA, entrega saliente de
			// envases si no existen en VEKP
			if (proceso.equals("1") && etiquetas.getResultDT().getId() != 1)
				etiquetas = husRepositoryOld.obtieneDatosHusLqua(hus);

		} else if (proceso.equals("2") || proceso.equals("4") || proceso.equals("900")) {

			// Hus LQUA
			etiquetas = hUsRepository.obtieneDatosHusLqua(hus);
		}

		// if (etiquetas.getResultDT().getId() == 1) {

		EtiquetaDatasource datasource = new EtiquetaDatasource();

		for (int x = 0; x < etiquetas.getItems().size(); x++) {

			if (proceso.equals("1") || proceso.equals("2") || proceso.equals("900")) {
				// Etiquetas envase

				jasper = "jasper/EtiquetaEnvase.jrxml";

				etiquetas.getItems().get(x).setEntrega(entrega);
				etiquetas.getItems().get(x).setBarCode(etiquetas.getItems().get(x).getEXIDV_HU());

			} else if (proceso.equals("3") || proceso.equals("4") || proceso.equals("5") || proceso.equals("900")
					|| proceso.equals("6")) {
				jasper = "jasper/EtiquetaPT.jrxml";
			}

			if (!proceso.equals("1"))
				etiquetas.getItems().get(x).setEntrega("");

			datasource.addEtiqueta(etiquetas.getItems().get(x));
		}

		// GEneración

		// ***
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		logger.info("Iniciando");
		String filePath = "Etiqueta" + hu + ".pdf";
		JasperReport jasperReport = null;
		JasperPrint jasperPrint = null;
		JasperDesign jasperDesign = null;

		try {
			jasperDesign = JRXmlLoader.load(servletContext.getRealPath(jasper));
			jasperReport = JasperCompileManager.compileReport(jasperDesign);
			jasperPrint = JasperFillManager.fillReport(jasperReport, null, datasource);
		} catch (JRException e) {
			e.printStackTrace();
		}

		File file = new File(filePath);

		logger.info("Datos del archivo:");
		logger.info("AbsolutePath: " + file.getAbsolutePath());
		logger.info("Name: " + file.getName());

		try {
			logger.info("Exportando a PDF...");
			JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
		} catch (JRException e) {
			logger.error("Error al exportar: " + e.toString());
			e.printStackTrace();
		}

		// Mostrar en pantalla PDF concatenado
		// 0906637273
		// File file = new File(filePath);
		// ***

		logger.info("Cargando archivo para descarga...");
		InputStreamResource resource = null;
		try {
			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Error al cargar: " + e.toString());
			e.printStackTrace();
		}

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}
}
