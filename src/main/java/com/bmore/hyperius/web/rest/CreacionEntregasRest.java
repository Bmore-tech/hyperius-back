package com.bmore.hyperius.web.rest;

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
import com.bmore.hyperius.web.dto.CreacionEntregaItemDTO;
import com.bmore.hyperius.web.dto.CrecionEntregaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.CreacionEntregasResponse;
import com.bmore.hyperius.web.rest.resquest.CreacionEntregasRequest;
import com.bmore.hyperius.web.service.CreacionEntregasService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Creación de Entregas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/creacion-entregas")
public class CreacionEntregasRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CreacionEntregasService creacionEntregasService;

	@PostMapping(path = "/obtiene-materiales", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse obtieneMateriales() {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.obtieneMateriales());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-tarimas", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse obtieneTarimas(@RequestBody CrecionEntregaDTO materialDTO) {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.obtieneTarimas(materialDTO));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-centros", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse obtieneCentros() {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.obtieneCentros());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-agencias", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse obtieneAgencias() {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.obtieneAgencias());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-transportes", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse obtieneTransportes(@RequestBody CrecionEntregaDTO transporteDTO) {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.obtieneTransportes(transporteDTO));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/crear-entrega", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse crearEntrega(@RequestHeader("Authorization") String token,
			@RequestBody CreacionEntregaItemDTO crearEntrega) {
		ResultDTO resultDT = creacionEntregasService.crearEntrega(crearEntrega, Utils.getWerksFromJwt(token),
				Utils.getUsuarioFromToken(token));

		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/get-entregas-bcps", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse getEntregasBCPS() {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.getEntregas());
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/get-entrega-bcps", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreacionEntregasResponse getEntregaBCPS(@RequestBody CreacionEntregasRequest request) {
		CreacionEntregasResponse response = new CreacionEntregasResponse();

		response.setData(creacionEntregasService.getEntrega(request.getVbeln()));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/eliminar-entrega-bcps", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse eliminarEntregaBCPS(@RequestHeader("Authorization") String token,
			@RequestBody CrecionEntregaDTO eliminarEntrega) {
		ResultDTO resultDT = new ResultDTO();

		resultDT = creacionEntregasService.eliminarEntrega(eliminarEntrega, Utils.getWerksFromJwt(token),
				Utils.getUsuarioFromToken(token));

		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(response.getMessage());

		return response;
	}
}
