package com.bmore.hyperius.mobile.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.rest.request.ConfirmarPickingHURecepcionRequest;
import com.bmore.hyperius.mobile.rest.request.LimpiarPendientesRecepcionRequest;
import com.bmore.hyperius.mobile.rest.request.PickHURecepcionRequest;
import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.mobile.rest.response.PickHURecepcionResponse;
import com.bmore.hyperius.mobile.service.impl.RecepcionEnvaseBO;
import com.bmore.hyperius.mobile.utils.ResultDT;

/**
 * Controlador Rest para el manejo de Recepciones de Envase.
 * 
 * @author Eduardo Chombo
 * @version 1.0
 * @since 06-11-2020
 */
@RestController
@RequestMapping("${mobile.uri}/recepcion")
public class RecepcionMobileRest {

  @Autowired
  private RecepcionEnvaseBO recepcionEnvaseBO;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@PostMapping(value = "/pick-hu-recepcion", produces = MediaType.APPLICATION_JSON_VALUE)
	public PickHURecepcionResponse pickearHURecepcion(@RequestBody PickHURecepcionRequest request)
			throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		PickHURecepcionResponse response = new PickHURecepcionResponse();

		if (request.getHu1OHu2() == 1)
			entregaInput.setHu1(request.getHu().trim());
		else {
			entregaInput.setHu1(request.getHu().subSequence(0, 20).toString());
			entregaInput.setHu2(request.getHu().substring(20));
			entregaInput.setEntrega(request.getEntrega());
			entregaInput.setMatnr(request.getMaterial());
			entregaInput.setuDestino1(request.getDest1());
			entregaInput.setuDestino2(request.getDest2());
		}

		entregaInput.setUsuarioMontacarga(request.getIdRed());
		entregaInput.setWerks(request.getWerks());

		log.info("werks: " + entregaInput.getWerks() + " HU: " + request.getHu());

		entregaInput = recepcionEnvaseBO.pickearHU(entregaInput, request.getHu1OHu2());

		response.setResponseCode(entregaInput.getResultDT().getId());
		response.setMessage(entregaInput.getResultDT().getMsg());
		response.setData(entregaInput);

		return response;
	}

	@PostMapping(value = "/confirmar-picking-hu-recepcion", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse confirmaPickingHURecepcion(@RequestBody ConfirmarPickingHURecepcionRequest request)
			throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		DefaultResponse response = new DefaultResponse();

		entregaInput.setUsuarioMontacarga(request.getIdRed());

		if (request.getHu().length() != 20) {
			entregaInput.setHu1(request.getHu().subSequence(0, 20).toString());
			entregaInput.setHu2(request.getHu().substring(20));
		} else {
			entregaInput.setHu1(request.getHu());
			entregaInput.setHu2("");
		}

		entregaInput.setEntrega(request.getEntrega());
		entregaInput.setMatnr(request.getMaterial());
		entregaInput.setuDestino0(request.getDestino0());
		entregaInput.setuDestino1(request.getDestino1());
		entregaInput.setuDestino2(request.getDestino2());
		entregaInput.setLfart(request.getLfart());
		entregaInput.setWerks(request.getWerks());

		entregaInput = recepcionEnvaseBO.confirmaPickingHU(entregaInput);

		response.setResponseCode(entregaInput.getResultDT().getId());
		response.setMessage(entregaInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/limpiar-pendientes-recepcion", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse limpiarPendientesRecepcion(@RequestBody LimpiarPendientesRecepcionRequest request)
			throws ClassNotFoundException {
		ResultDT resultDT = new ResultDT();
		EntregaInput entregaInput = new EntregaInput();
		DefaultResponse response = new DefaultResponse();

		entregaInput.setUsuarioMontacarga(request.getIdRed());
		entregaInput.setEntrega(request.getEntrega());

		resultDT = recepcionEnvaseBO.limpiarPendientesXUsuario(entregaInput.getEntrega(),
				entregaInput.getUsuarioMontacarga());

		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}