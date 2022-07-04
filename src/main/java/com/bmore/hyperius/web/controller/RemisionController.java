package com.bmore.hyperius.web.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bmore.hyperius.web.repository.EmbarquePTRepository;
import com.bmore.hyperius.web.rest.resquest.RemisionRequest;
import com.bmore.hyperius.web.service.RemisionService;
import com.bmore.hyperius.web.utils.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Controller encargado de las Remisiones.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 09-08-2020
 */
@Controller
public class RemisionController {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private EmbarquePTRepository embarquePTRepository;

	@Autowired
	private RemisionService remisionService;

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	// String noEntrega = "0901089676";
	private static final int BUFSIZE = 4096;

	@RequestMapping(value = "/remision", method = RequestMethod.POST)
	public ResponseEntity<Resource> remision(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
			@RequestBody RemisionRequest request) {
		return remisionService.etiquetaPt(request, token);
	}

	@RequestMapping(value = "/remision2", method = RequestMethod.POST)
	protected void remision2(@RequestHeader("Authorization") String token, RemisionRequest request,
			HttpServletResponse response) throws IOException {
		String werks = Utils.getWerksFromJwt(token);

		String nEntrega = request.getNumEntrega();
		String selloTransporte = request.getSelloTransporte();
		String tarjetaDe = request.getTarjetaDe();
		String placas = request.getPlacas();
		String conductor = request.getConductor();

		String remision = nEntrega;
		nEntrega = Utils.zeroFill(nEntrega, 10);

		// llamado a querys para datos unicos en reporte jasper
		String planta = embarquePTRepository.obtenerPlanta(nEntrega);
		String dirPlanta = embarquePTRepository.obtenerDirPlanta(nEntrega);
		String cliente = embarquePTRepository.obtenerCliente(nEntrega, "1");
		String particular = embarquePTRepository.obtenerCliente(nEntrega, "2");

		try {
			cliente = Integer.parseInt(cliente) + "";
		} catch (Exception e) {

		}
		String datosCliente = embarquePTRepository.obtenerDatosCliente(nEntrega);
		String fecha = embarquePTRepository.obtenerFecha();

		// Mapeo de campos en reporte
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("planta", planta);
		param.put("dirPlanta", dirPlanta);
		param.put("cliente", cliente);
		param.put("remision", remision);
		param.put("datosCliente", datosCliente);
		param.put("selloTransporte", selloTransporte);
		param.put("fecha", fecha);
		param.put("tarjetaDe", tarjetaDe);
		param.put("placas", placas);
		param.put("conductor", conductor);

		String jasperFile = "";
		String jasperFile2 = "";

		try {
			Integer.parseInt(particular);
			if (werks.equals("PV11") || werks.equals("PV12") || werks.equals("PV13") || werks.equals("PV22")) {
				jasperFile = "RemisionOtra.jasper";
				jasperFile2 = "RemisionOtraCopia.jasper";
				param.put("logo", servletContext.getRealPath("img/" + werks + ".jpg"));
			} else {
				jasperFile = "RemisionOtra.jasper";
				jasperFile2 = "RemisionOtraCopia.jasper";
				param.put("logo", servletContext.getRealPath("img/particular.jpg"));
			}

		} catch (NumberFormatException e1) {
			if (werks.equals("EMZ1") || werks.equals("TMZ1") || werks.equals("PV11") || werks.equals("PV12")
					|| werks.equals("PV13") || werks.equals("PV22")) {

				jasperFile = "RemisionOtra.jasper";
				jasperFile2 = "RemisionOtraCopia.jasper";
				param.put("logo", servletContext.getRealPath("img/" + werks + ".jpg"));

			} else if (werks.equals("PC01") || werks.equals("PC03") || werks.equals("PC05") || werks.equals("PC07")
					|| werks.equals("PC11") || werks.equals("PC13") || werks.equals("PC19")) {
				jasperFile = "Remision.jasper";
				jasperFile2 = "RemisionCopia.jasper";
				param.put("logo", servletContext.getRealPath("img/" + werks + ".jpg"));

			}
		}

		String bandera = "";

		// Generar reporte Jasper
		try {
			JasperReport jasperReport = null;
			JasperPrint jasperPrint = null;

			JasperReport jReportCopy = null;
			JasperPrint jPrintCopy = null;

			LOCATION.error("Remision File");

			jasperReport = (JasperReport) JRLoader
					.loadObjectFromFile(servletContext.getRealPath("jasper/" + jasperFile));// JasperCompileManager.compileReport(jasperDesign);
			jasperPrint = JasperFillManager.fillReport(jasperReport, param,
					embarquePTRepository.obtenerTabla(nEntrega));

			jReportCopy = (JasperReport) JRLoader
					.loadObjectFromFile(servletContext.getRealPath("jasper/" + jasperFile2));
			jPrintCopy = JasperFillManager.fillReport(jReportCopy, param, embarquePTRepository.obtenerTabla(nEntrega));

			JasperExportManager.exportReportToPdfFile(jasperPrint, "RemisionOriginal.pdf");
			JasperExportManager.exportReportToPdfFile(jPrintCopy, "RemisionCopia.pdf");

			LOCATION.error("GENERADO");
		} catch (JRException ex) {

			bandera = ex.toString();

			LOCATION.error(this.getClass().getName(), ex);
		}
		// FIN Generar reporte Jasper

		if (bandera.length() == 0) {

			// Concatenar PDF Original y Copia
			try {
				PdfReader reader1 = new PdfReader("RemisionOriginal.pdf");
				PdfReader reader2 = new PdfReader("RemisionCopia.pdf");
				PdfCopyFields copy = new PdfCopyFields(new FileOutputStream("Remision" + remision + ".pdf"));
				copy.addDocument(reader1);
				copy.addDocument(reader2);
				copy.close();
				// FIN Concatenar PDF Original y Copia

				// Mostrar en pantalla PDF concatenado
				String filePath = "Remision" + remision + ".pdf";
				File file = new File(filePath);
				ServletOutputStream outputStream = response.getOutputStream();

				String mimetype = servletContext.getMimeType(filePath);
				// if (mimetype == null) {
				mimetype = "application/octet-stream";
				// }
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
				// FIN Mostrar en pantalla PDF concatenado

				try {
					file.delete();
				} catch (Exception e) {

				}

			} catch (IOException iOException) {
				response.getWriter().write("Error al concatenar PDFs IO Exception: " + iOException.toString());
				LOCATION.error("Error al concatenar PDFs IO Exception: " + iOException.toString());
			} catch (DocumentException documentException) {
				response.getWriter()
						.write("Error al concatenar PDFs Document Exception: " + documentException.toString());
				LOCATION.error("Error al concatenar PDFs Document Exception: " + documentException.toString());
			}
		} else {
			response.getWriter().write("No fue posible generar el PDF " + bandera);
			LOCATION.error("No fue posible generar el PDF " + bandera);
		}
	}
}
