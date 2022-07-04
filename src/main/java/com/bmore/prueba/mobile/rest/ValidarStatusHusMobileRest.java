package com.bmore.prueba.mobile.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.prueba.mobile.dto.HuDTO;
import com.bmore.prueba.mobile.dto.HusDTO;
import com.bmore.prueba.mobile.rest.request.ValidarHuVidrioRequest;
import com.bmore.prueba.mobile.rest.request.ValidarHuWMRequest;
import com.bmore.prueba.mobile.rest.response.ValidarHuVidrioResponse;
import com.bmore.prueba.mobile.rest.response.ValidarHuWMResponse;
import com.bmore.prueba.mobile.service.impl.ValidarHUBO;

/**
 * Controladore Rest para validación de HU's.
 * 
 * @author Eduardo Chombo
 * @version 1.0
 * @since 07-11-2020
 */
@RestController
@RequestMapping("${mobile.uri}/valida-status-hus")
public class ValidarStatusHusMobileRest {

	@PostMapping(value = "/validar-hu-wm", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidarHuWMResponse validarHuWM(@RequestBody ValidarHuWMRequest request) throws ClassNotFoundException {
		HuDTO huDTO = new HuDTO();

		ValidarHuWMResponse response = new ValidarHuWMResponse();

		huDTO = ValidarHUBO.validarHU(request.getHu());

		response.setResponseCode(huDTO.getResultDT().getId());
		response.setMessage(huDTO.getResultDT().getMsg());
		response.setData(huDTO);

		return response;
	}

	@PostMapping(value = "/validar-hu-vidrio", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidarHuVidrioResponse validarHuVidrio(@RequestBody ValidarHuVidrioRequest request)
			throws ClassNotFoundException {
		HusDTO husDTO = new HusDTO();
		ValidarHuVidrioResponse response = new ValidarHuVidrioResponse();

		husDTO = ValidarHUBO.validarHUVidrio(request.getHu(), request.getWerks());

		response.setData(husDTO);
		response.setResponseCode(husDTO.getResultDT().getId());
		response.setMessage(husDTO.getResultDT().getMsg());

		return response;
	}
}