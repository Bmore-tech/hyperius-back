package com.bmore.hyperius.mobile.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.rest.request.ConfirmaPickingHuUbicacionRequest;
import com.bmore.hyperius.mobile.rest.request.LimpiarPendientesUbicacionRequest;
import com.bmore.hyperius.mobile.rest.request.PickearHuUbicacionRequest;
import com.bmore.hyperius.mobile.rest.response.ConfirmaPickingHuUbicacionResponse;
import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.mobile.rest.response.PickearHuUbicacionResponse;
import com.bmore.hyperius.mobile.service.impl.UbicacionPTBO;
import com.bmore.hyperius.mobile.utils.LoginBO;
import com.bmore.hyperius.mobile.utils.ResultDT;

@RestController
@RequestMapping("${mobile.uri}/ubicacion-pt")
public class UbicacionPTMobileRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@PostMapping(value = "/pickear-hu-ubicacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public PickearHuUbicacionResponse pickearHuUbicacion(@RequestBody PickearHuUbicacionRequest request)
			throws ClassNotFoundException {
		PickearHuUbicacionResponse response = new PickearHuUbicacionResponse();
		OrdenProduccionInput ordenProduccionInput = new OrdenProduccionInput();

		ordenProduccionInput.setUsuarioMontacarga(request.getIdRed());
		ordenProduccionInput.setWerks(request.getWerks());

		if (request.getHu1OHu2() == 1) {
			ordenProduccionInput.setHu1(request.getHu().trim());
		} else {
			ordenProduccionInput.setHu1(request.getHu().trim().subSequence(0, 20).toString());
			ordenProduccionInput.setHu2(request.getHu().trim().substring(20));
			ordenProduccionInput.setOrdeProduccion(request.getOrdProd());
			ordenProduccionInput.setMatnr(request.getMaterial());
			ordenProduccionInput.setuDestino1(request.getOrigen1());
			ordenProduccionInput.setuDestino2(request.getOrigen2());
			log.info("hu.trim().substring(20): " + request.getHu().trim().substring(20));
			log.info("Hu1: " + ordenProduccionInput.getHu1() + "\nHu2: " + ordenProduccionInput.getHu2());
		}
		ordenProduccionInput = UbicacionPTBO.pickearHU(ordenProduccionInput, request.getHu1OHu2());

		response.setData(ordenProduccionInput);
		response.setResponseCode(ordenProduccionInput.getResultDT().getId());
		response.setMessage(ordenProduccionInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/confirma-picking-hu-ubicacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConfirmaPickingHuUbicacionResponse confirmaPickingHuUbicacion(
			@RequestBody ConfirmaPickingHuUbicacionRequest request) throws ClassNotFoundException {
		OrdenProduccionInput ordenProduccionInput = new OrdenProduccionInput();
		ordenProduccionInput.setUsuarioMontacarga(request.getIdRed());
		ConfirmaPickingHuUbicacionResponse response = new ConfirmaPickingHuUbicacionResponse();

		if (request.getHu().length() == 20) {
			ordenProduccionInput.setHu1(request.getHu());
			ordenProduccionInput.setHu2("");
		} else {
			ordenProduccionInput.setHu1(request.getHu().subSequence(0, 20).toString());
			ordenProduccionInput.setHu2(request.getHu().substring(20));
		}
		ordenProduccionInput.setOrdeProduccion(request.getOrdProd());
		ordenProduccionInput.setMatnr(request.getMaterial());
		ordenProduccionInput.setWerks(request.getWerks());
		ordenProduccionInput.setuOrigen0(request.getOrigen0());
		ordenProduccionInput.setuOrigen1(request.getOrigen1());
		ordenProduccionInput.setuOrigen2(request.getOrigen2());

		ordenProduccionInput = UbicacionPTBO.confirmaPickingHU(ordenProduccionInput);

		response.setData(ordenProduccionInput);
		response.setResponseCode(ordenProduccionInput.getResultDT().getId());
		response.setMessage(ordenProduccionInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/limpiar-pendientes-ubicacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse limpiarPendientesUbicacion(@RequestBody LimpiarPendientesUbicacionRequest request)
			throws ClassNotFoundException {
		ResultDT resultDT = new ResultDT();
		resultDT = LoginBO.checkValidSession(request.getUsuarioMontacarga(), request.getSessionId());
		DefaultResponse response = new DefaultResponse();

		resultDT = UbicacionPTBO.limpiarPendientesXUsuario(request.getOrdeProduccion(), request.getUsuarioMontacarga());

		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}
