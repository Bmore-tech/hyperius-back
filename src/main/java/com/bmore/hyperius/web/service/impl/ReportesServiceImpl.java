package com.bmore.hyperius.web.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.rest.resquest.ReportesRequest;
import com.bmore.hyperius.web.service.ReporteService;
import com.bmore.hyperius.web.service.ReportesService;
import com.bmore.hyperius.web.utils.Utils;

@Service
public class ReportesServiceImpl implements ReportesService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ReporteService reporteService;

	@Override
	public ResponseEntity<Resource> reportes(ReportesRequest request, String token) {
		String werks = Utils.getWerksFromJwt(token);
		Calendar calendar = new GregorianCalendar();
		String timeStamp = new Timestamp(calendar.getTimeInMillis()) + "";
		String mytimeStamp = timeStamp.replaceAll(":", "_").replaceAll(" ", "").replaceAll("-", "_");
		HttpHeaders headers = new HttpHeaders();
		String reporte = request.getReporte();
		String vbeln = "";

		if (reporte.equals("EDI")) {
			vbeln = request.getEntrega();
		}

		logger.error("REPORTE: " + reporte + " VBELN: " + vbeln);
		File file = reporteService.getDocument(reporte, mytimeStamp, werks, vbeln);

		InputStreamResource resource = null;

		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		try {
			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

}
