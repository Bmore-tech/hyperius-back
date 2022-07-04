package com.bmore.prueba.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.EntregaDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.rest.response.ValidarEntregaResponse;
import com.bmore.prueba.web.service.RecepcionEnvaseService;
import com.bmore.prueba.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Recepción Envase.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/recepcion-envase")
public class RecepcionEnvaseRest {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	@Autowired
	private RecepcionEnvaseService recepcionEnvaseService;

	@PostMapping(path = "/validar-entrega", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidarEntregaResponse validarEntrega(@RequestHeader("Authorization") String token,
			@RequestBody EntregaDTO request) {

		ValidarEntregaResponse response = new ValidarEntregaResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		response.setData(recepcionEnvaseService.validaEntrega(request));
		response.getData().setWerks(Utils.getWerksFromJwt(token));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/ingresa-detalle-envase", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse ingresaDetalleEnvase(@RequestHeader("Authorization") String token,
			@RequestBody CarrilesUbicacionDTO request) {
		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		resultDT = recepcionEnvaseService.ingresaDetalleEnvaseBO(request, Utils.getUsuarioFromToken(token),
				Utils.getWerksFromJwt(token));
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/contabilizar-entrega-entrante", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse contabilizarEntregaEntrante(@RequestHeader("Authorization") String token,
			@RequestBody EntregaDTO request) {
		ResultDTO result = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		result = recepcionEnvaseService.contabilizarEntregaEntrante(request, Utils.getUsuarioFromToken(token));
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}
}
