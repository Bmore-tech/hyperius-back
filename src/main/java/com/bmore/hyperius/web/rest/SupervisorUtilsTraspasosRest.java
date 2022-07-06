package com.bmore.hyperius.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.AlmacenDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.LgortPermitidosResponse;
import com.bmore.hyperius.web.rest.response.LquaBusquedaTraspasosResponse;
import com.bmore.hyperius.web.service.SupervisorUtilsTraspasosService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Supervisor y Traspasos.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/supervisor-utils-traspasos")
public class SupervisorUtilsTraspasosRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SupervisorUtilsTraspasosService supervisorUtilsTraspasosService;

	@PostMapping(path = "/lgort-permitidos", produces = MediaType.APPLICATION_JSON_VALUE)
	public LgortPermitidosResponse lgortPermitidos(@RequestHeader("Auth") String token) {
		LgortPermitidosResponse response = new LgortPermitidosResponse();
		response.setData(supervisorUtilsTraspasosService.lgortPermitidos(Utils.getWerksFromJwt(token)));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/lgnum-permitidos", produces = MediaType.APPLICATION_JSON_VALUE)
	public LgortPermitidosResponse lgnumPermitidos(@RequestHeader("Auth") String token,
			@RequestBody AlmacenDTO request) {
		request.setWerks(Utils.getWerksFromJwt(token));
		LgortPermitidosResponse response = new LgortPermitidosResponse();
		response.setData(supervisorUtilsTraspasosService.lgnumPermitidos(request));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/lgtyp-permitidos", produces = MediaType.APPLICATION_JSON_VALUE)
	public LgortPermitidosResponse lgtypPermitidos(@RequestHeader("Auth") String token,
			@RequestBody AlmacenDTO request) {
		LgortPermitidosResponse response = new LgortPermitidosResponse();
		request.setWerks(Utils.getWerksFromJwt(token));
		response.setData(supervisorUtilsTraspasosService.lgtypPermitidos(request));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/lgpla-permitidos", produces = MediaType.APPLICATION_JSON_VALUE)
	public LgortPermitidosResponse lgplaPermitidos(@RequestHeader("Auth") String token,
			@RequestBody AlmacenDTO request) {
		LgortPermitidosResponse response = new LgortPermitidosResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		response.setData(supervisorUtilsTraspasosService.lgplaPermitidos(request));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/lqua-busqueda-traspasos", produces = MediaType.APPLICATION_JSON_VALUE)
	public LquaBusquedaTraspasosResponse lquaBusquedaTraspasos(@RequestHeader("Auth") String token,
			@RequestBody AlmacenDTO request) {
		LquaBusquedaTraspasosResponse response = new LquaBusquedaTraspasosResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		response.setData(supervisorUtilsTraspasosService.lquaBusquedaTraspasos(request));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/traspaso", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse traspaso(@RequestHeader("Auth") String token,
			@RequestBody InventarioDetalleDTOItem request) {
		ResultDTO resultDT = supervisorUtilsTraspasosService.traspaso(request, Utils.getUsuarioFromToken(token));
		DefaultResponse response = new DefaultResponse();

		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}
