package com.bmore.hyperius.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.CargaInformacionDTO;
import com.bmore.hyperius.web.dto.PlaneacionBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.CargaInformacionResponse;
import com.bmore.hyperius.web.rest.response.DescargaInformacionResponse;
import com.bmore.hyperius.web.rest.response.ReporteAvanceResponse;
import com.bmore.hyperius.web.service.CifrasControlService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Cifras de Control.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/cifras-control")
public class CifrasControlRest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CifrasControlService cifrasControlService;

	@PostMapping(path = "/descarga-informacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public DescargaInformacionResponse descargaInformacion(@RequestHeader("Auth") String token) {
		DescargaInformacionResponse response = new DescargaInformacionResponse();
		response.setData(cifrasControlService.getDescargaInformacion());
		response.setResponseCode(1);
		response.setMessage("");

		return response;
	}

	@PostMapping(path = "/carga-informacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public CargaInformacionResponse cargaInformacion(HttpServletRequest request,
			@RequestBody CargaInformacionDTO cargaInformacionDTO) {
		CargaInformacionResponse response = new CargaInformacionResponse();

		response.setData(cifrasControlService.getCargaInformacion());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/reporte-operaciones", produces = MediaType.APPLICATION_JSON_VALUE)
	public ReporteAvanceResponse reporteOperaciones(HttpServletRequest request,
			@RequestBody ReporteAvanceDTO reporteAvanceDTO) {
		ReporteAvanceResponse response = new ReporteAvanceResponse();

		response.setData(cifrasControlService.getReporteOperaciones());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/almacenar-planeacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse almacenarPlaneacion(@RequestHeader("Auth") String token,
			@RequestBody PlaneacionBodyDTO request) {
		DefaultResponse response = new DefaultResponse();

		request.setCentro(Utils.getWerksFromJwt(token));
		ResultDTO result = cifrasControlService.saveReportePlaneacion(request);
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}
}
