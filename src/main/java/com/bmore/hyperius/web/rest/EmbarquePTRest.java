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
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.EmbarquePTResponse;
import com.bmore.hyperius.web.service.EmbarquePTService;
import com.bmore.hyperius.web.service.IMEmbarquePTService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Creación de Entregas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/embarque-pt")
public class EmbarquePTRest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IMEmbarquePTService iMEmbarquePTService;

	@Autowired
	private EmbarquePTService embarquePTService;

	@PostMapping(path = "/validar-embarque-pt", produces = MediaType.APPLICATION_JSON_VALUE)
	public EmbarquePTResponse validaEmbarquePT(@RequestHeader("Authorization") String token,
			@RequestBody EmbarqueDTO embarqueDTO) {

		EmbarquePTResponse response = new EmbarquePTResponse();

		embarqueDTO.setWerks(Utils.getWerksFromJwt(token));
		embarqueDTO.setAdmin(Utils.getAdminFromToken(token));

		if (embarqueDTO.getTipoAlmacen().equalsIgnoreCase("im")) {
			response.setData(iMEmbarquePTService.validaEmbarque(embarqueDTO));
		}
		if (embarqueDTO.getTipoAlmacen().equalsIgnoreCase("wm")) {
			response.setData(embarquePTService.validaEmbarque(embarqueDTO));
		}

		response.getData().setWerks(Utils.getWerksFromJwt(token));
		response.getData().setAdmin(Utils.getAdminFromToken(token));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/guardar-embarque-pt", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse guardarEmbarquePT(@RequestHeader("Authorization") String token, HttpServletRequest request,
			@RequestBody CarrilesUbicacionDTO carriles) {

		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		if (carriles.getItem().get(0).getTipoAlmacen().equalsIgnoreCase("im")) {
			resultDT = iMEmbarquePTService.ingresaDetalleEnvaseBO(carriles, Utils.getUsuarioFromToken(token),
					Utils.getWerksFromJwt(token));
		} else if (carriles.getItem().get(0).getTipoAlmacen().equals("wm")) {
			resultDT = embarquePTService.ingresaDetalleEnvaseBO(carriles, Utils.getUsuarioFromToken(token),
					Utils.getWerksFromJwt(token));
		}

		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/contabilizar-embarque", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse contabilizarEmbarque(@RequestHeader("Authorization") String token,
			@RequestBody EmbarqueDTO embarqueDTO) {
		ResultDTO result = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		embarqueDTO.setWerks(Utils.getWerksFromJwt(token));
		if (embarqueDTO.getTipoAlmacen().equals("im")) {
			result = iMEmbarquePTService.contabilizarEntregaEntrante(embarqueDTO, Utils.getUsuarioFromToken(token));
		}
		if (embarqueDTO.getTipoAlmacen().equals("wm")) {
			result = embarquePTService.contabilizarEntregaEntrante(embarqueDTO, Utils.getUsuarioFromToken(token));
		}

		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/liberar-pendientes", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse liberarPendientes(@RequestHeader("Authorization") String token,
			@RequestBody OrdenProduccionDTO ordenProduccionDTO) {
		ResultDTO result = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		result = embarquePTService.limpiarPendientes(ordenProduccionDTO.getOrdenProduccion());
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/cambiar-cantidad-orden", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse cambiarCantidadOrden(@RequestHeader("Authorization") String token,
			@RequestBody EmbarqueDetalleDTO embarqueDetalleDTO) {
		ResultDTO result = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		result = embarquePTService.cambiarCantidadOrdenProduccion(embarqueDetalleDTO, Utils.getUsuarioFromToken(token),
				Utils.getWerksFromJwt(token));
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/ingresar-restos-embarque", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse ingresarRestosEmbarque(@RequestBody HuDTO huDTO) {

		DefaultResponse response = new DefaultResponse();

		if (embarquePTService.isWerksAllowed(huDTO)) {
			response.setResponseCode(1);
			response.setMessage("Werk allowed");

		} else {
			response.setResponseCode(2);
			response.setMessage("Werk not allowed");
		}

		return response;
	}
}
