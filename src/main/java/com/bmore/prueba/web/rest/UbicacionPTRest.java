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
import com.bmore.prueba.web.dto.OrdenProduccionDTO;
import com.bmore.prueba.web.dto.OrdenProduccionInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.rest.response.ValidaOrdenProduccionResponse;
import com.bmore.prueba.web.service.IMUbicacionPTService;
import com.bmore.prueba.web.service.UbicacionPTService;
import com.bmore.prueba.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Ubicación.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/ubicacion-pt")
public class UbicacionPTRest {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	@Autowired
	private IMUbicacionPTService iMUbicacionPTService;

	@Autowired
	private UbicacionPTService ubicacionPTService;

	@PostMapping(path = "/valida-orden-produccion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidaOrdenProduccionResponse validaOrdenProduccion(@RequestHeader("Authorization") String token,
			@RequestBody OrdenProduccionDTO request) {

		ValidaOrdenProduccionResponse response = new ValidaOrdenProduccionResponse();

		request.setWerks(Utils.getWerksFromJwt(token));

		if (request.getTipoAlmacen().equals("im")) {

			response.setData(iMUbicacionPTService.validaOrden(request));

		} else if (request.getTipoAlmacen().equals("wm")) {
			response.setData(ubicacionPTService.validaOrden(request));
		}
		response.getData().setWerks(Utils.getWerksFromJwt(token));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/grabar-ubicacion-pt", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse grabarUbicacionPT(@RequestHeader("Authorization") String token,
			@RequestBody CarrilesUbicacionDTO request) {
		DefaultResponse response = new DefaultResponse();
		ResultDTO resultDT = ubicacionPTService.ingresaDetalleEnvaseBO(request, Utils.getUsuarioFromToken(token),
				Utils.getWerksFromJwt(token));
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/vidrio-ubica-pt", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse vidrioUbicaPT(@RequestHeader("Authorization") String token,
			@RequestBody OrdenProduccionInputDTO request) {

		request.setUsuarioMontacarga(Utils.getUsuarioFromToken(token));
		request.setWerks(Utils.getWerksFromJwt(token));

		ResultDTO result = iMUbicacionPTService.vidrioUbicaPT(request);
		DefaultResponse response = new DefaultResponse();

		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}
}
