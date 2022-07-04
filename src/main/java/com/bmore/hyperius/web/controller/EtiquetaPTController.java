package com.bmore.hyperius.web.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.ControlPaletizadoraRepository;
import com.bmore.hyperius.web.rest.resquest.EtiquetaPtRequest;
import com.bmore.hyperius.web.service.ControlPaletizadoraService;
import com.bmore.hyperius.web.service.EtiquetaPTService;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.print.EtiquetaDatasource;
import com.bmore.hyperius.web.utils.print.Etiquetas;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * Controller para las etiquetas PT.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 05-08-2020
 */
@Controller
public class EtiquetaPTController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ControlPaletizadoraRepository controlPaletizadoraRepository;

	@Autowired
	private EtiquetaPTService etiquetaPTService;

	@Autowired
	private ControlPaletizadoraService controlPaletizadoraService;

	@RequestMapping(value = "/etiqueta-pt", method = RequestMethod.POST)
	public ResponseEntity<Resource> etiquetaViaEtiqueta(@RequestHeader("Authorization") String token,
			@RequestBody EtiquetaPtRequest request) {
		log.info("Request Etiqueta PT: " + request.toString());
		
		PaletizadoraDTO data = new PaletizadoraDTO();
		data.setWerks(Utils.getWerksFromJwt(token));
		data.setCantidadEtiqueasAImprimir(request.getCantidadEtiquetasAImprimir());
		data.setAufnr(request.getAufnr());
		
		ResultDTO dataResponse = controlPaletizadoraService.marcarHusParaImprimir(data);
		
		log.info("Data Etiqueta PT: " + data.toString());
		log.info("dataResponse Etiqueta PT: " + dataResponse.toString());
		
		request.setKey(dataResponse.getTypeS());
		request.setAufnr(dataResponse.getMsg());

		return etiquetaPTService.etiquetaPt(request);
	}

	@RequestMapping(value = "/etiqueta-pt2", method = RequestMethod.POST)
	public void etiquetaPt2(@RequestHeader("Authorization") String token, @RequestBody EtiquetaPtRequest request,
			HttpServletResponse response) throws IOException {
		String aufnr = (String) request.getAufnr();
		String key = (String) request.getKey();

		String jasper = "";
		ResultDTO resultDT = new ResultDTO();

		if (aufnr != null && key != null) {
			Etiquetas etiquetas = controlPaletizadoraRepository.obtieneHusParaImprimir(aufnr, key);
			String hu = "";
			if (etiquetas.getResultDT().getId() == 1) {

				EtiquetaDatasource datasource = new EtiquetaDatasource();

				boolean continuar = true;

				for (int x = 0; x < etiquetas.getItems().size(); x++) {

					hu = etiquetas.getItems().get(x).getEXIDV_HU();
					log.info("ID ETIQUETA_:" + hu);
					jasper = "jasper/EtiquetaPT.jrxml";
					datasource.addEtiqueta(etiquetas.getItems().get(x));
				}

				if (continuar == true) {
					try {
						JasperReport jasperReport = null;
						JasperPrint jasperPrint = null;
						// JasperDesign jasperDesign = null;

						// Debug
						log.info("Contexto: " + servletContext.getRealPath(jasper));

						// jasperDesign = JRXmlLoader.load(jasper);
						jasperReport = JasperCompileManager.compileReport(servletContext.getRealPath(jasper));
						// Error
						jasperPrint = JasperFillManager.fillReport(jasperReport, null, datasource);

						JasperExportManager.exportReportToPdfFile(jasperPrint, "Etiqueta" + hu + ".pdf");

						// Mostrar en pantalla PDF concatenado
						String filePath = "Etiqueta" + hu + ".pdf";
						File file = new File(filePath);
						ServletOutputStream outputStream = response.getOutputStream();

						String mimetype = servletContext.getMimeType(filePath);
						mimetype = "application/octet-stream";
						response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
						response.setContentType(mimetype);
						response.setContentLength((int) file.length());
						int length = 0;
						byte[] byteBuffer = new byte[4096];
						DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
						while ((dataInputStream != null) && ((length = dataInputStream.read(byteBuffer)) != -1)) {
							outputStream.write(byteBuffer, 0, length);
						}
						dataInputStream.close();
						outputStream.close();

						log.error("TODO OK");

						try {
							file.delete();
						} catch (Exception e) {

						}
					} catch (JRException ex) {
						log.error("Error Jasper: " + ex.toString());
					}
				} else {
					response.getWriter().write(resultDT.getMsg());
				}
			} else {
				response.getWriter().write(etiquetas.getResultDT().getMsg());
			}
		} else {
			response.getWriter().write("No se recibió la orden de producción y la llave");
		}
	}
}
