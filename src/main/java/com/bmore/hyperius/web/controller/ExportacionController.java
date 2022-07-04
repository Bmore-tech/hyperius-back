package com.bmore.hyperius.web.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.EmbarquePTRepository;
import com.bmore.hyperius.web.rest.resquest.ExportacionRequest;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.export.Export_BO;
import com.bmore.hyperius.web.utils.export.Export_DTO;
import com.bmore.hyperius.web.utils.export.Export_Exception;
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
 * Controller encargado de la Esportación.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 07-08-2020
 */
@Controller
public class ExportacionController {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private EmbarquePTRepository embarquePTRepository;

	private static final int BUFSIZE = 4096;

	@RequestMapping(value = "/exportacion", method = RequestMethod.POST)
	protected void exportacion(@RequestHeader("Authorization") String token, @RequestBody ExportacionRequest request,
			HttpServletResponse response) throws IOException {
		String werks = Utils.getWerksFromJwt(token);

		if (werks != null) {
			String nEntrega = request.getNumEntrega();
			String selloTransporte = request.getSelloTransporte();
			String talon = request.getTalon();
			String transportista = request.getTransportista();
			String noCaja = request.getNoCaja();
			String selloImportador = request.getSelloImportador();

			String exportacion = nEntrega;
			nEntrega = Utils.zeroFill(nEntrega, 10);

			// llamado a querys para datos unicos en reporte jasper
			Export_DTO exp_DTO = new Export_DTO();
			Export_BO exp_BO = new Export_BO();
			ResultDTO resultDT = new ResultDTO();

			try {
				exp_DTO = exp_BO.remisionFill(nEntrega);

				String planta = embarquePTRepository.obtenerPlanta(nEntrega);
				String dirPlanta = embarquePTRepository.obtenerDirPlanta(nEntrega);

				// exp_DTO.setReferenciaImportador(nEntrega+"_"+exp_DTO.getDistribuidor()+"_"+werks);
				exp_DTO.setReferenciaImportador(werks + " " + exp_DTO.getDistribuidor() + " " + nEntrega);
				exp_DTO.setPlantaHeader(planta);
				exp_DTO.setPlantaBody(dirPlanta);
				exp_DTO.setNumeroSello(selloTransporte);
				exp_DTO.setSelloImportador(selloImportador);
				exp_DTO.setTalonEmbarque(talon);
				exp_DTO.setOperador(transportista);
				exp_DTO.setNumeroCaja(noCaja);
				exp_DTO.setDistribuidor(Utils.zeroClean(exp_DTO.getDistribuidor()));

				exp_DTO.setPedido(Utils.zeroClean(exp_DTO.getPedido()));
				resultDT = Export_BO.remisionExist(exp_DTO);
				LOCATION.error("REMISION VALUES TO GET: " + exp_DTO.toString());
				LOCATION.error("Remision Exist: " + resultDT.toString());
				Export_BO.remisionZcont(exp_DTO);
				resultDT = Export_BO.remisionXMlExist(exp_DTO);
				LOCATION.error("Remision XML Exist: " + resultDT.toString());
				if (resultDT.getId() > 0) {
					// Nothing to do Here
					LOCATION.error("Remision XML Already Exists!!");
				} else {
					LOCATION.error("Creating New");
					// (new XMLThread(nEntrega, werks)).start();
				}

				String jasperFile = "entrega_export.jasper";
				String jasperFile2 = "entrega_export.jasper";

				exp_DTO.setLogo(servletContext.getRealPath("img/" + werks + ".jpg"));
				Map<String, Object> param = Export_DTO.Export_DTO_MAP(exp_DTO);

				LOCATION.error("MAPEO DE DATOS: " + param.toString());
				String bandera = "";

				// Generar reporte Jasper
				try {
					JasperReport jasperReport = null;
					JasperPrint jasperPrint = null;

					JasperReport jReportCopy = null;
					JasperPrint jPrintCopy = null;

					LOCATION.error("Exportacion File");

					jasperReport = (JasperReport) JRLoader
							.loadObjectFromFile(servletContext.getRealPath("jasper/" + jasperFile));// JasperCompileManager.compileReport(jasperDesign);
					jasperPrint = JasperFillManager.fillReport(jasperReport, param,
							embarquePTRepository.obtenerTablaExp(nEntrega));

					jReportCopy = (JasperReport) JRLoader
							.loadObjectFromFile(servletContext.getRealPath("jasper/" + jasperFile2));
					jPrintCopy = JasperFillManager.fillReport(jReportCopy, param,
							embarquePTRepository.obtenerTablaExp(nEntrega));

					JasperExportManager.exportReportToPdfFile(jasperPrint, "ExportacionOriginal.pdf");
					JasperExportManager.exportReportToPdfFile(jPrintCopy, "ExportacionCopia.pdf");

					LOCATION.error("GENERADO");
				} catch (JRException ex) {

					bandera = ex.toString();

					LOCATION.error(servletContext.getServletContextName(), ex);
				}
				// FIN Generar reporte Jasper
				if (bandera.length() == 0) {
					// Concatenar PDF Original y Copia
					try {
						PdfReader reader1 = new PdfReader("ExportacionOriginal.pdf");
						PdfReader reader2 = new PdfReader("ExportacionCopia.pdf");
						PdfCopyFields copy = new PdfCopyFields(
								new FileOutputStream("Exportacion" + exportacion + ".pdf"));
						copy.addDocument(reader1);
						copy.addDocument(reader2);
						copy.close();
						// FIN Concatenar PDF Original y Copia

						// Mostrar en pantalla PDF concatenado
						String filePath = "Exportacion" + exportacion + ".pdf";
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
						LOCATION.error(resultDT.toString());
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
			} catch (Export_Exception e) {
				response.getWriter().write(e.getMessage());
			}
		} else {
			response.getWriter().write("Usuario no logueado");
		}
	}
}
