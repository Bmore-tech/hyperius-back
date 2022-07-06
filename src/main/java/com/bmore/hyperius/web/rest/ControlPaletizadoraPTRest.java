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
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.rest.response.NormasEmbalajeResponse;
import com.bmore.hyperius.web.rest.response.ObtienePaletizadorasResponse;
import com.bmore.hyperius.web.rest.resquest.ObtieneCantidadHusRequest;
import com.bmore.hyperius.web.service.ControlPaletizadoraService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Control de Paletizadora.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/control-paletizadora")
public class ControlPaletizadoraPTRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ControlPaletizadoraService controlPaletizadoraService;

	@PostMapping(path = "/obtiene-paletizadoras", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtienePaletizadorasResponse obtienePaletizadoras(@RequestHeader("Auth") String token,
			@RequestBody PaletizadoraDTO request) {
		ObtienePaletizadorasResponse response = new ObtienePaletizadorasResponse();

		response.setData(controlPaletizadoraService.obtienePaletizadoras(Utils.getWerksFromJwt(token)));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/actualiza-orden-en-paletizadora", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse actualizaOrdenEnPaletizadora(@RequestHeader("Auth") String token,
			@RequestBody PaletizadoraDTO request) {
		ResultDTO resultDT = new ResultDTO();

		request.setWerks(Utils.getWerksFromJwt(token));
		resultDT = controlPaletizadoraService.actualizaOrdenEnPaletizadora(request);

		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-cantidad-hus", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse obtieneCantidadHus(@RequestHeader("Auth") String token,
			@RequestBody ObtieneCantidadHusRequest request) {
		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		resultDT = controlPaletizadoraService.obtieneCantidadHUS(request.getAufnr());
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/marcar-hus-para-imprimir", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse marcarHusParaImprimir(@RequestHeader("Auth") String token,
			@RequestBody PaletizadoraDTO request) {
		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		resultDT = controlPaletizadoraService.marcarHusParaImprimir(request);
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-normas-embalaje", produces = MediaType.APPLICATION_JSON_VALUE)
	public NormasEmbalajeResponse obtieneNormasEmbalaje(@RequestHeader("Auth") String token,
			@RequestBody PaletizadoraDTO request) {
		NormasEmbalajeResponse response = new NormasEmbalajeResponse();

		response.setData(controlPaletizadoraService.obtieneNormasEmbalaje(request.getAufnr(),
				Utils.getWerksFromJwt(token), request.getUnidadMedida(), request.getCantidadAProducir(),
				request.getOpc(), request.getMaterialPTTarima()));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/cambiar-norma-embalaje", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse cambiarNormaEmbalaje(@RequestHeader("Auth") String token,
			@RequestBody PaletizadoraDTO request) {

		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		resultDT = controlPaletizadoraService.cambiarNormaEmbalaje(request);
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/embalar-hus", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse embalarHus(@RequestHeader("Auth") String token,
			@RequestBody PaletizadoraDTO request) {
		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		request.setWerks(Utils.getWerksFromJwt(token));
		resultDT = controlPaletizadoraService.embalarHus(request, Utils.getUsuarioFromToken(token));
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}
}
