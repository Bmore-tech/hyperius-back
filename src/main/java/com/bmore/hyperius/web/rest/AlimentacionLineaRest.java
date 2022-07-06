package com.bmore.hyperius.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.ValidaOrdenProduccionResponse;
import com.bmore.hyperius.web.service.AlimentacionLineaService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Alimentación en Línea.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/alimentacion-linea")
public class AlimentacionLineaRest {

	@Autowired
	private AlimentacionLineaService alimentacionLineaService;

	@PostMapping(path = "/valida-orden-produccion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidaOrdenProduccionResponse validaOrdenProduccion(@RequestHeader("Auth") String token,
			@RequestBody OrdenProduccionDTO request) {

		ValidaOrdenProduccionResponse response = new ValidaOrdenProduccionResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		response.setData(alimentacionLineaService.validaOrden(request));
		response.getData().setWerks(request.getWerks());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/alimenta-envase", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse alimentaEnvase(@RequestHeader("Auth") String token,
			@RequestBody CarrilesUbicacionDTO request) {

		ResultDTO result = alimentacionLineaService.ingresaDetalleEnvaseBO(request, Utils.getUsuarioFromToken(token),
				Utils.getWerksFromJwt(token));

		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/liberar-pendientes", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse liberarPendientes(@RequestBody OrdenProduccionDTO ordenProduccionDTO) {

		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(alimentacionLineaService.limpiarPendientes(ordenProduccionDTO.getOrdenProduccion()));

		return response;
	}
}
