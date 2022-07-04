package com.bmore.hyperius.mobile.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.rest.request.ValidarEntregaEmbarqueImRequest;
import com.bmore.hyperius.mobile.rest.response.LimpiarPendientesXUsuarioEmbarqueIMResponse;
import com.bmore.hyperius.mobile.service.impl.EmbarquePTIMBO;
import com.bmore.hyperius.mobile.utils.ResultDT;

/**
 * Controlador para la gestión de los Embarques.
 * 
 * @author Eduardo Chombo
 * @version 1.0
 * @since 07-11-2020
 */
@RestController
@RequestMapping("${mobile.uri}/embarque-pti")
public class EmbarquePTIMobileRest {

	@PostMapping(value = "validar-entrega-embarque-im", produces = MediaType.APPLICATION_JSON_VALUE)
	public EntregaInput validarEntregaEmbarqueIM(@RequestBody ValidarEntregaEmbarqueImRequest request)
			throws ClassNotFoundException {
		return (ValidarEntregaEmbarqueImRequest) EmbarquePTIMBO.validarEntrega(request);
	}

	@PostMapping(value = "/consumir-hu-embarque-im", produces = MediaType.APPLICATION_JSON_VALUE)
	public EntregaInput consumirHUEmbarqueIM(@RequestBody ValidarEntregaEmbarqueImRequest request)
			throws ClassNotFoundException {

		return (ValidarEntregaEmbarqueImRequest) EmbarquePTIMBO.consumirHU(request);
	}

	@PostMapping(value = "limpiar-pendientes-usuario-embarque-im", produces = MediaType.APPLICATION_JSON_VALUE)
	public LimpiarPendientesXUsuarioEmbarqueIMResponse limpiarPendientesXUsuarioEmbarqueIM(
			@RequestBody ValidarEntregaEmbarqueImRequest request) throws ClassNotFoundException {
		LimpiarPendientesXUsuarioEmbarqueIMResponse response = new LimpiarPendientesXUsuarioEmbarqueIMResponse();

		ResultDT resultDT = EmbarquePTIMBO.limpiaPendientesXUsuario(request.getEntrega(),
				request.getUsuarioMontacarga());

		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}