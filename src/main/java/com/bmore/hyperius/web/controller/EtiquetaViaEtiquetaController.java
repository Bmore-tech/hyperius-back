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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.HUsRepository;
import com.bmore.hyperius.web.rest.resquest.CreateEtiquetaReportRequest;
import com.bmore.hyperius.web.service.EtiquetaViaEtiquetaService;
import com.bmore.hyperius.web.utils.Utils;
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

/**
 * Controller para las etiquetas & etiquetas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 07-08-2020
 */
@Controller
public class EtiquetaViaEtiquetaController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HUsRepository hUsRepository;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private EtiquetaViaEtiquetaService etiquetaViaEtiquetaService;

	@RequestMapping(value = "/etiqueta-via-etiqueta", method = RequestMethod.POST)
	public ResponseEntity<Resource> etiquetaViaEtiqueta(@RequestHeader("Auth") String token,
			@RequestBody CreateEtiquetaReportRequest request) {
		return etiquetaViaEtiquetaService.createEtiquetaReport(request, token);
	}

	@RequestMapping(value = "/etiqueta-via-etiqueta2", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
	protected @ResponseBody ResponseEntity<InputStreamResource> etiquetaViaEtiqueta(
			@RequestHeader("Auth") String token, HttpServletResponse response,
			@RequestBody CreateEtiquetaReportRequest request) throws IOException {

		String jasper = "";

		String werks = Utils.getWerksFromJwt(token);
		// HuDTOItem huDTOItem = (HuDTOItem) session.getAttribute("etiquetas");
		ResultDTO resultDT = new ResultDTO();
		String proceso = "";
		String entrega = "";

		if (werks != null) {
			if (request != null) {

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

					etiquetas = hUsRepository.obtieneDatosHusVekp(hus);

					// intenta recuperar etiquetas de LQUA, entrega saliente de
					// envases si no existen en VEKP
					if (proceso.equals("1") && etiquetas.getResultDT().getId() != 1)
						etiquetas = hUsRepository.obtieneDatosHusLqua(hus);

				} else if (proceso.equals("2") || proceso.equals("4") || proceso.equals("900")) {

					// Hus LQUA
					etiquetas = hUsRepository.obtieneDatosHusLqua(hus);
				}

				if (etiquetas.getResultDT().getId() == 1) {

					EtiquetaDatasource datasource = new EtiquetaDatasource();

					boolean continuar = true;

					for (int x = 0; x < etiquetas.getItems().size(); x++) {

						if (proceso.equals("1") || proceso.equals("2") || proceso.equals("900")) {
							// Etiquetas envase

							jasper = "jasper/EtiquetaEnvase.jrxml";

							etiquetas.getItems().get(x).setEntrega(entrega);
							etiquetas.getItems().get(x).setBarCode(etiquetas.getItems().get(x).getEXIDV_HU());

						} else if (proceso.equals("3") || proceso.equals("4") || proceso.equals("5")
								|| proceso.equals("900") || proceso.equals("6")) {
							jasper = "jasper/EtiquetaPT.jrxml";
						}

						if (!proceso.equals("1"))
							etiquetas.getItems().get(x).setEntrega("");

						datasource.addEtiqueta(etiquetas.getItems().get(x));
					}

					if (continuar) {
						response.setContentType("application/pdf");
						try {
							JasperReport jasperReport = null;
							JasperPrint jasperPrint = null;
							JasperDesign jasperDesign = null;

							jasperDesign = JRXmlLoader.load(servletContext.getRealPath(jasper));
							jasperReport = JasperCompileManager.compileReport(jasperDesign);
							jasperPrint = JasperFillManager.fillReport(jasperReport, null, datasource);

							JasperExportManager.exportReportToPdfFile(jasperPrint, "Etiqueta" + hu + ".pdf");

							// Mostrar en pantalla PDF concatenado
							String filePath = "Etiqueta" + hu + ".pdf";
							File file = new File(filePath);
							ServletOutputStream outputStream = response.getOutputStream();

							String mimetype = servletContext.getMimeType(filePath);

							mimetype = "application/octet-stream";

							response.setHeader("Content-Disposition",
									"attachment; filename=\"" + file.getName() + "\"");
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
							// session.removeAttribute("etiquetas");

							try {
								file.delete();
							} catch (Exception e) {
							}

							logger.error("TODO OK");
						} catch (JRException ex) {
							logger.error("Error Jasper: " + ex.toString());
						}
					} else {
						response.getWriter().write(resultDT.getMsg());
					}
				} else {
					response.getWriter().write(etiquetas.getResultDT().getMsg());
				}
			} else {
				response.getWriter().write("No se recibió documento y Hus");
			}
		} else {
			response.getWriter().write("Usuario no logueado");
		}
		return null;
	}
}
