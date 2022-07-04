package com.bmore.hyperius.web.rest;

import javax.servlet.http.HttpServletRequest;
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

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.NewSecureLoginDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;
import com.bmore.hyperius.web.repository.old.LoginRepository;
import com.bmore.hyperius.web.rest.response.TransportesResponse;
import com.bmore.hyperius.web.service.TransportesService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Transportes.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/transportes")
public class TransportesRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private TransportesService transportesService;

	/**
	 * Post Rest para la validación de la entrada de transporte.
	 * 
	 * @param token         JWT del usuario.
	 * @param transporteDTO Datos del transporte.
	 * @return {@link TransportesDTO} con la información del transporte obtenida.
	 */
	@PostMapping(path = "/validar-transporte-entrada", produces = MediaType.APPLICATION_JSON_VALUE)
	public TransportesResponse validarTransporteEntrada(@RequestHeader("Authorization") String token,
			@RequestBody TransportesDTO transporteDTO) {
		
		log.info("token: " + token);

		transporteDTO.setWerks(Utils.getWerksFromJwt(token));
		log.info(transporteDTO.getWerks());

		TransportesResponse response = new TransportesResponse();
		response.setData(transportesService.existeTransporteEntrada(transporteDTO));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/validar-transporte-salida", produces = MediaType.APPLICATION_JSON_VALUE)
	public TransportesResponse validarTransporteSalida(@RequestHeader("Authorization") String token,
			@RequestBody TransportesDTO transporteDTO) {

		transporteDTO.setWerks(Utils.getWerksFromJwt(token));
		log.info(transporteDTO.getWerks());

		TransportesResponse response = new TransportesResponse();
		response.setData(transportesService.existeTransporteSalida(transporteDTO));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/registrar-transporte", produces = MediaType.APPLICATION_JSON_VALUE)
	public TransportesResponse registrarTransporte(@RequestHeader("Authorization") String token,
			@RequestBody TransportesDTO transporteDTO) {

		ResultDTO result = new ResultDTO();

		transporteDTO.setWerks(Utils.getWerksFromJwt(token));
		result = transportesService.updateStatusTransporte(transporteDTO, transporteDTO.getWerks());
		TransportesResponse response = new TransportesResponse();
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/registrar-transporte-salida", produces = MediaType.APPLICATION_JSON_VALUE)
	public TransportesResponse registrarTransporteSalida(@RequestHeader("Authorization") String token,
			@RequestBody TransportesDTO transporteDTO) {

		transporteDTO.setWerks(Utils.getWerksFromJwt(token));

		TransportesResponse response = new TransportesResponse();
		ResultDTO result = transportesService.updateStatusTransporte(transporteDTO, transporteDTO.getWerks());
		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
		
		
	}

	@PostMapping(path = "/check-web-session", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse checkWebSession(@RequestHeader("Authorization") String token, @RequestBody String id) {

		DefaultResponse response = new DefaultResponse();

		response.setResponseCode(Utils.hasTokenExpired(token));

		return response;
	}

	@Deprecated
	@PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResultDTO login(HttpServletRequest request, @RequestBody NewSecureLoginDTO newSecureLogin) {

		ResultDTO resultDT = new ResultDTO();
		log.error("Login RSTF: " + newSecureLogin.getUser());

		HttpSession session = request.getSession();

		String werks = (String) session.getAttribute("werks");

		if (werks != null) {

			if (session.getAttribute("user").equals(newSecureLogin.getUser())) {

				resultDT.setId(1);

			} else {

				resultDT.setId(2);
				resultDT.setMsg("Ya se encuentra otro usuario logueado, abra otra sesión");

			}

		} else {
			resultDT = LoginRepository.newLogin(newSecureLogin);

			if (resultDT.getId() == 1) {

				session = request.getSession();
				log.error("USUARIO: " + newSecureLogin.getUser());
				log.error("werks: " + resultDT.getMsg());
				log.error("admin: " + resultDT.getTypeI());

				session.setAttribute("user", newSecureLogin.getUser());
				session.setAttribute("werks", resultDT.getMsg());
				session.setAttribute("admin", resultDT.getTypeI());
				resultDT.setId(1);
			}
		}

		return resultDT;
	}

	@Deprecated
	@PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultDTO logout(HttpServletRequest request, @RequestBody String id) {

		ResultDTO resultDT = new ResultDTO();
		HttpSession session = request.getSession();

		try {
			session.removeAttribute("user");
			session.removeAttribute("werks");
			session.removeAttribute("admin");
			session.invalidate();
			resultDT.setId(1);
			resultDT.setMsg("Sesion cerrada");
		} catch (Exception e) {
			resultDT.setMsg("Error al cerrar sesion");
			resultDT.setId(2);
		}

		return resultDT;
	}
}
