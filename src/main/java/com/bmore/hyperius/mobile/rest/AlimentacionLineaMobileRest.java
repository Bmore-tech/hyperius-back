package com.bmore.hyperius.mobile.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.rest.request.ConfirmaHusDepaRequest;
import com.bmore.hyperius.mobile.rest.request.LimpiarPendientesRequest;
import com.bmore.hyperius.mobile.rest.request.PickearHuRequest;
import com.bmore.hyperius.mobile.rest.request.ValidaOrdenProduccionRequest;
import com.bmore.hyperius.mobile.rest.response.ConfirmaHusDepaResponse;
import com.bmore.hyperius.mobile.rest.response.LimpiarPendientesResponse;
import com.bmore.hyperius.mobile.rest.response.PickearHuResponse;
import com.bmore.hyperius.mobile.rest.response.ValidaOrdenProduccionResponse;
import com.bmore.hyperius.mobile.service.impl.AlimentacionLineaBO;
import com.bmore.hyperius.mobile.utils.ResultDT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${mobile.uri}/alimentacion-linea")
public class AlimentacionLineaMobileRest {

  @Autowired
  private AlimentacionLineaBO alimentacionLineaBO;

	@PostMapping(value = "/valida-orden-produccion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ValidaOrdenProduccionResponse validaOrdenProduccion(@RequestBody ValidaOrdenProduccionRequest request)
			throws ClassNotFoundException {
		OrdenProduccionInput ordenProduccionInput = new OrdenProduccionInput();
		ordenProduccionInput.setOrdeProduccion(request.getOrdProd().trim());
		ordenProduccionInput.setUsuarioMontacarga(request.getIdRed().trim());

		ordenProduccionInput = alimentacionLineaBO.validaOrdenProduccion(ordenProduccionInput);

		log.info(ordenProduccionInput.toString());

		ValidaOrdenProduccionResponse response = new ValidaOrdenProduccionResponse();
		response.setResponseCode(ordenProduccionInput.getResultDT().getId());
		response.setMessage(ordenProduccionInput.getResultDT().getMsg());
		response.setData(ordenProduccionInput);

		return response;
	}

	@PostMapping(value = "/pickear-hu", produces = MediaType.APPLICATION_JSON_VALUE)
	public PickearHuResponse pickearHU(@RequestBody PickearHuRequest request) throws ClassNotFoundException {
		OrdenProduccionInput ordenProduccionInput = new OrdenProduccionInput();

		if (request.getHu1OHu2() == 1)
			ordenProduccionInput.setHu1(request.getHu().trim());
		else {
			ordenProduccionInput.setHu1(request.getHu().subSequence(0, 20).toString());
			ordenProduccionInput.setHu2(request.getHu().substring(20));
		}

		log.info("hu1:" + ordenProduccionInput.getHu1());
		log.info("hu2:" + ordenProduccionInput.getHu2());

		ordenProduccionInput.setUsuarioMontacarga(request.getIdRed());
		ordenProduccionInput.setuOrigen1(request.getOrigen1());
		ordenProduccionInput.setuOrigen2(request.getOrigen2());
		ordenProduccionInput.setWerks(request.getWerks());
		ordenProduccionInput.setOrdeProduccion(request.getOrdProd());
		ordenProduccionInput.setMatnr(request.getMaterial());

		ordenProduccionInput = alimentacionLineaBO.pickearHU(ordenProduccionInput, request.getHu1OHu2());

		log.info("Orden al pickear :" + ordenProduccionInput.toString());

		PickearHuResponse response = new PickearHuResponse();
		response.setData(ordenProduccionInput);
		response.setResponseCode(ordenProduccionInput.getResultDT().getId());
		response.setMessage(ordenProduccionInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/confirmar-hus-depa", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConfirmaHusDepaResponse confirmaHusDepa(@RequestBody ConfirmaHusDepaRequest request)
			throws ClassNotFoundException {
		OrdenProduccionInput ordenProduccionInput = new OrdenProduccionInput();
		ordenProduccionInput.setUsuarioMontacarga(request.getIdRed());

		if (request.getHu().length() == 20)
			ordenProduccionInput.setHu1(request.getHu().trim());
		else {
			ordenProduccionInput.setHu1(request.getHu().subSequence(0, 20).toString());
			ordenProduccionInput.setHu2(request.getHu().substring(20));
		}

		ordenProduccionInput.setOrdeProduccion(request.getOrdProd());
		ordenProduccionInput.setMatnr(request.getMaterial());
		ordenProduccionInput.setWerks(request.getWerks());
		ordenProduccionInput.setuOrigen0(request.getOrigen0());
		ordenProduccionInput.setuOrigen1(request.getOrigen1());
		ordenProduccionInput.setuOrigen2(request.getOrigen2());

		ordenProduccionInput = alimentacionLineaBO.confirmaHusEnDepa(ordenProduccionInput);

		ConfirmaHusDepaResponse response = new ConfirmaHusDepaResponse();
		response.setData(ordenProduccionInput);
		response.setResponseCode(ordenProduccionInput.getResultDT().getId());
		response.setMessage(ordenProduccionInput.getResultDT().getMsg());

		return response;
	}

	@PostMapping(value = "/limpiar-pendientes", produces = MediaType.APPLICATION_JSON_VALUE)
	public LimpiarPendientesResponse limpiarPendientes(@RequestBody LimpiarPendientesRequest request)
			throws ClassNotFoundException {
		ResultDT resultDT = new ResultDT();
		OrdenProduccionInput ordenProduccionInput = new OrdenProduccionInput();
		ordenProduccionInput.setUsuarioMontacarga(request.getIdRed());

		log.error("OrdenProduccion:" + ordenProduccionInput.getUsuarioMontacarga());

		resultDT = alimentacionLineaBO.limpiarPendientesXUsuario(ordenProduccionInput.getOrdeProduccion(),
				ordenProduccionInput.getUsuarioMontacarga());

		LimpiarPendientesResponse response = new LimpiarPendientesResponse();
		response.setData(ordenProduccionInput);
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}