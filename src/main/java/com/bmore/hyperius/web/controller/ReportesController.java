package com.bmore.hyperius.web.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bmore.hyperius.web.rest.resquest.ReportesRequest;
import com.bmore.hyperius.web.service.ReporteService;
import com.bmore.hyperius.web.service.ReportesService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controller encargado de los Reportes.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 09-08-2020
 */
@Controller
public class ReportesController {

	private static final String ext = ".csv";

	private static final String ext2 = ".txt";

	private static final int BUFSIZE = 4096;

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ReportesService reportesService;

	@RequestMapping(value = "/reportes", method = RequestMethod.POST)
	public ResponseEntity<Resource> etiquetaViaEtiqueta(@RequestHeader("Auth") String token,
			@RequestBody ReportesRequest request) {
		return reportesService.reportes(request, token);
	}

	@RequestMapping(value = "/reportes2", method = RequestMethod.POST)
	protected void reportes2(@RequestHeader("Auth") String token, ReportesRequest request,
			HttpServletResponse response) throws IOException {
		String werks = Utils.getWerksFromJwt(token);
		Calendar calendar = new GregorianCalendar();
		String timeStamp = new Timestamp(calendar.getTimeInMillis()) + "";
		String mytimeStamp = timeStamp.replaceAll(":", "_").replaceAll(" ", "").replaceAll("-", "_");

		String reporte = request.getReporte();
		String vbeln = "";
		if (reporte.equals("EDI")) {
			vbeln = request.getEntrega();
		}

		LOCATION.error("REPORTE: " + reporte + " VBELN: " + vbeln);
		File file = reporteService.getDocument(reporte, mytimeStamp, werks, vbeln);
		ServletOutputStream outputStream = response.getOutputStream();
		String mimetype = null;
		if (reporte.equals("EDI")) {
			mimetype = servletContext.getMimeType(reporteService.filePath(reporte + vbeln, mytimeStamp, werks, ext2));
		} else {
			mimetype = servletContext.getMimeType(reporteService.filePath(reporte, mytimeStamp, werks, ext));
		}
		mimetype = "application/octet-stream";

		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		response.setContentType(mimetype);
		response.setContentLength((int) file.length());
		int length = 0;
		byte[] byteBuffer = new byte[BUFSIZE];
		DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
		while ((dataInputStream != null) && ((length = dataInputStream.read(byteBuffer)) != -1)) {
			outputStream.write(byteBuffer, 0, length);
		}
		dataInputStream.close();
		outputStream.close();

	}
}
