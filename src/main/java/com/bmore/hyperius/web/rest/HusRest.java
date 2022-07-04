package com.bmore.hyperius.web.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.HuDTOItem;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.HusResponse;
import com.bmore.hyperius.web.service.HUsService;

/**
 * Controlador Rest para las operaciones de HU.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/hus")
public class HusRest {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	@Autowired
	private HUsService hUsService;

	@PostMapping(path = "/obtiene-hus", produces = MediaType.APPLICATION_JSON_VALUE)
	public HusResponse obtieneHUs(@RequestHeader("Authorization") String token, @RequestBody HuDTO hu) {
		HusResponse response = new HusResponse();

		response.setData(hUsService.obtieneHus(hu));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/imprimir-hus", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultDTO imprimirHUs(HttpServletRequest request, HttpServletResponse response,
			@RequestBody HuDTOItem huDTOItem) {

		LOCATION.error("Obtiene HUS");

		HttpSession session = request.getSession();
		ResultDTO resultDT = new ResultDTO();

		LOCATION.error("Werks != null");

		if (huDTOItem != null) {
			session.setAttribute("etiquetas", huDTOItem);
			resultDT.setId(1);
			resultDT.setMsg("Etiquetas guardadas");
		} else {
			resultDT.setId(2);
			resultDT.setMsg("Etiquetas guardadas");
		}

		return resultDT;
	}
}
