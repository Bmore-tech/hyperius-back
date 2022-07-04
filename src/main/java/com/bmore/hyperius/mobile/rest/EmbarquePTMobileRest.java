package com.bmore.hyperius.mobile.rest;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.rest.request.ConfirmaHusEnCamionFurgonRequest;
import com.bmore.hyperius.mobile.rest.request.LimpiarPendientesEmbarqueRequest;
import com.bmore.hyperius.mobile.rest.request.PickearHuEmbarqueRequest;
import com.bmore.hyperius.mobile.rest.request.ValidaEntregaEmbarqueRequest;
import com.bmore.hyperius.mobile.rest.response.ConfirmaHusEnCamionFurgonResponse;
import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.mobile.rest.response.PickearHuEmbarqueResponse;
import com.bmore.hyperius.mobile.rest.response.ValidaEntregaEmbarqueResponse;
import com.bmore.hyperius.mobile.service.impl.EmbarquePTBO;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@RestController
@RequestMapping("${mobile.uri}/embarque-pt")
public class EmbarquePTMobileRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@PostMapping(value = "/valida-entrega-embarque", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidaEntregaEmbarqueResponse validaEntregaEmbarque(@RequestBody ValidaEntregaEmbarqueRequest request)
			throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		EmbarquePTBO embarquePTBO = new EmbarquePTBO();
		entregaInput.setUsuarioMontacarga(request.getIdRed());
		entregaInput.setEntrega(request.getEntrega());
		entregaInput.setWerks(request.getWerks());

		log.info(entregaInput.getWerks());

		entregaInput = embarquePTBO.validarEntrega(entregaInput);

		ValidaEntregaEmbarqueResponse response = new ValidaEntregaEmbarqueResponse();
		response.setData(entregaInput);
		response.setResponseCode(entregaInput.getResultDT().getId());
		response.setMessage(entregaInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/pickear-hu-embarque", produces = MediaType.APPLICATION_JSON_VALUE)
	public PickearHuEmbarqueResponse pickearHuEmbarque(@RequestBody PickearHuEmbarqueRequest request)
			throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		entregaInput.setUsuarioMontacarga(request.getIdRed());
		HashMap<String, String> hashhMap = new HashMap<String, String>();
		entregaInput.setMatnr(request.getMaterial());

		if (request.getHu1Ohu2() == 1) {
			entregaInput.setHu1(request.getHu().trim());
		} else {
			entregaInput.setHu1(request.getHu().subSequence(0, 20).toString());
			entregaInput.setHu2(request.getHu().substring(20));
		}

		entregaInput.setWerks(request.getWerks());
		entregaInput.setMatnr(request.getMaterial());
		entregaInput.setuOrigen1(request.getOrigen1());
		entregaInput.setuOrigen2(request.getOrigen2());
		hashhMap.put(Utils.zeroFill(entregaInput.getMatnr(), 18), Utils.zeroFill(entregaInput.getMatnr(), 18));
		entregaInput.setMateriales(hashhMap);
		
		EmbarquePTBO embarquePTBO = new EmbarquePTBO();

		entregaInput = embarquePTBO.pickearHU(entregaInput, request.getHu1Ohu2());

		PickearHuEmbarqueResponse response = new PickearHuEmbarqueResponse();
		response.setData(entregaInput);
		response.setResponseCode(entregaInput.getResultDT().getId());
		response.setMessage(entregaInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/confirmar-hus-en-camion-furgon", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConfirmaHusEnCamionFurgonResponse confirmaHusEnCamionFurgon(
			@RequestBody ConfirmaHusEnCamionFurgonRequest request) throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		entregaInput.setUsuarioMontacarga(request.getIdRed());

		if (request.getHu().length() == 20)
			entregaInput.setHu1(request.getHu());
		else {
			entregaInput.setHu1(request.getHu().subSequence(0, 20).toString());
			entregaInput.setHu2(request.getHu().substring(20));
		}

		entregaInput.setEntrega(request.getEntrega());
		entregaInput.setMatnr(request.getMatnr());
		entregaInput.setWerks(request.getWerks());
		entregaInput.setuOrigen0(request.getOrigen0());
		entregaInput.setuOrigen1(request.getOrigen1());
		entregaInput.setuOrigen2(request.getOrigen2());
		EmbarquePTBO embarquePTBO = new EmbarquePTBO();

		entregaInput = embarquePTBO.confirmaHusEnCamionFurgon(entregaInput);

		ConfirmaHusEnCamionFurgonResponse response = new ConfirmaHusEnCamionFurgonResponse();
		response.setData(entregaInput);
		response.setResponseCode(entregaInput.getResultDT().getId());
		response.setMessage(entregaInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/limpiar-pendientes-embarque", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse limpiarPendientesEmbarque(@RequestBody LimpiarPendientesEmbarqueRequest request)
			throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		ResultDT resultDT = new ResultDT();
		entregaInput.setUsuarioMontacarga(request.getIdRed());
		entregaInput.setEntrega(request.getEntrega());
		EmbarquePTBO embarquePTBO = new EmbarquePTBO();

		resultDT = embarquePTBO.limpiarPendientesXUsuario(entregaInput.getEntrega(),
				entregaInput.getUsuarioMontacarga());

		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}
