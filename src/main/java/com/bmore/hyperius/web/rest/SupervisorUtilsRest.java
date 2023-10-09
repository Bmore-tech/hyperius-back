package com.bmore.hyperius.web.rest;

import java.util.Map;

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
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDTO;
import com.bmore.hyperius.web.dto.HUsEnTransporteDetalleDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;
import com.bmore.hyperius.web.dto.UsuarioDTO;
import com.bmore.hyperius.web.repository.old.ZContingenciaRepository;
import com.bmore.hyperius.web.rest.response.BuscarUsuarioResponse;
import com.bmore.hyperius.web.rest.response.CargaBcpsResponse;
import com.bmore.hyperius.web.rest.response.HusEnTransporteResponse;
import com.bmore.hyperius.web.rest.response.ObtieneBloqueoCarrilesResponse;
import com.bmore.hyperius.web.rest.response.ObtieneEntregasAgenciasDetalleResponse;
import com.bmore.hyperius.web.rest.response.ObtieneEntregasAgenciasResponse;
import com.bmore.hyperius.web.rest.response.ObtieneEntregasResponse;
import com.bmore.hyperius.web.rest.response.ObtieneHusBloqueadosResponse;
import com.bmore.hyperius.web.rest.response.ObtieneInventarioResponse;
import com.bmore.hyperius.web.rest.resquest.BuscarUsuarioRequest;
import com.bmore.hyperius.web.rest.resquest.ObtieneEntregasAgenciasDetalleRequest;
import com.bmore.hyperius.web.rest.resquest.SwitchUbsBcpsRequest;
import com.bmore.hyperius.web.rest.resquest.SwitchUbsSapRequest;
import com.bmore.hyperius.web.service.SupervisorUtilsService;
import com.bmore.hyperius.web.utils.SAPConector;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controlador Rest para las operaciones de Supervisor.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 28-07-2020
 */
@RestController
@RequestMapping("${web.uri}/supervisor-utils")
public class SupervisorUtilsRest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SupervisorUtilsService supervisorUtilsService;

	@PostMapping(path = "/obtiene-entregas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtieneEntregasResponse obtieneEntrega(@RequestHeader("Auth") String token,
			@RequestBody TransportesDTO transporteDTO) {

		ObtieneEntregasResponse response = new ObtieneEntregasResponse();

    log.info("CONSUMIENDO /obtiene-entregas");

		if (transporteDTO.getIdTransporte() == null || transporteDTO.getIdTransporte().trim().equals(""))
			response.setData(supervisorUtilsService.obtieneEntregas(Utils.getWerksFromJwt(token)));
		else {
			response.setData(supervisorUtilsService.obtieneEntrega(Utils.zeroFill(transporteDTO.getIdTransporte(), 10),
					Utils.getWerksFromJwt(token)));
		}

		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-inventario", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtieneInventarioResponse obtieneInventario(@RequestHeader("Auth") String token,
			@RequestBody Map<String, String> values) {
		ObtieneInventarioResponse response = new ObtieneInventarioResponse();

		response.setData(supervisorUtilsService.obtieneInventario(Utils.getWerksFromJwt(token), values.get("opc")));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	/**
	 * 
	 * @param token
	 * @param request
	 * @param husEnTransporteDetalleDTO
	 * @return
	 */
	@PostMapping(path = "/obtiene-carriles-bloqueados", produces = MediaType.APPLICATION_JSON_VALUE)
	public HusEnTransporteResponse obtieneCarrilesBloqueados(@RequestHeader("Auth") String token,
			HttpServletRequest request, @RequestBody HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO) {

		HusEnTransporteResponse response = new HusEnTransporteResponse();

		response.setData(supervisorUtilsService.obtieneCarrilesBloqueados(husEnTransporteDetalleDTO,
				Utils.getWerksFromJwt(token)));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	/**
	 * 
	 * @param request
	 * @param carriles
	 * @return
	 */
	@PostMapping(path = "/liberar-carriles", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse liberarCarriles(@RequestHeader("Auth") String token,
			@RequestBody CarrilesUbicacionDTO carriles) {
		ResultDTO result = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		result = supervisorUtilsService.desbloquearCarril(carriles, Utils.getWerksFromJwt(token));

		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-hus-bloqueados", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtieneHusBloqueadosResponse obtieneHusBloqueados(@RequestHeader("Auth") String token,
			@RequestBody HUsEnTransporteDetalleDTO request) {
		ObtieneHusBloqueadosResponse response = new ObtieneHusBloqueadosResponse();

		response.setData(supervisorUtilsService.obtieneHusBloqueados(request, Utils.getWerksFromJwt(token)));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	/**
	 * 
	 * @param request
	 * @param carriles
	 * @return
	 */
	@PostMapping(path = "/libera-hus-bloqueados", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse liberaHusBloqueados(@RequestHeader("Auth") String token,
			@RequestBody CarrilesUbicacionDTO carriles) {
		ResultDTO result = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		result = supervisorUtilsService.liberarHusEnTransporte(carriles, Utils.getWerksFromJwt(token));

		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/valida-carril", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse validaCarril(@RequestBody CarrilUbicacionDTO carril) {
		DefaultResponse response = new DefaultResponse();
		ResultDTO result = supervisorUtilsService.validaCarril(carril);

		response.setResponseCode(result.getId());
		response.setMessage(result.getMsg());

		return response;
	}

	@PostMapping(path = "/carga-bcps", produces = MediaType.APPLICATION_JSON_VALUE)
	public CargaBcpsResponse cargaBCPS(@RequestHeader("Auth") String token) {
		CargaBcpsResponse response = new CargaBcpsResponse();
		response.setData(
				supervisorUtilsService.cargaBCPS(Utils.getWerksFromJwt(token), Utils.getUsuarioFromToken(token)));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/carga-sap", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse cargaSAP(@RequestHeader("Auth") String token) {
		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();
		ZContingenciaRepository zContingencia = new ZContingenciaRepository();

		resultDT = zContingencia.zContingencia(Utils.getUsuarioFromToken(token), Utils.getWerksFromJwt(token));
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/switch-ubs-sap", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse switchUbsSAP(@RequestHeader("Auth") String token,
			@RequestBody SwitchUbsSapRequest request) {
		ResultDTO resultDT = new ResultDTO();
		DefaultResponse response = new DefaultResponse();

		resultDT = supervisorUtilsService.switchUbsSAP(Utils.getWerksFromJwt(token), request.getPassword());
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/switch-ubs-bcps", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse switchUbsBCPS(@RequestHeader("Auth") String token,
			@RequestBody SwitchUbsBcpsRequest request) {
		ResultDTO resultDT = supervisorUtilsService.switchUbsBCPS(Utils.getWerksFromJwt(token), request.getPassword());
		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/buscar-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public BuscarUsuarioResponse buscarUsuario(@RequestHeader("Auth") String token,
			@RequestBody BuscarUsuarioRequest request) {

		BuscarUsuarioResponse response = new BuscarUsuarioResponse();
		response.setData(supervisorUtilsService.buscarUsuario(request.getIdUser()));
		response.setResponseCode(response.getData().getResult().getId());
		response.setMessage(response.getData().getResult().getMsg());

		return response;
	}

	@PostMapping(path = "/eliminar-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse eliminarUsuario(@RequestHeader("Auth") String token,
			@RequestBody UsuarioDTO request) {
		ResultDTO resultDT = supervisorUtilsService.eliminarUsuario(request, Utils.getWerksFromJwt(token));
		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/crear-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse crearUsuario(@RequestHeader("Auth") String token, @RequestBody UsuarioDTO user) {
		user.setWerks(Utils.getWerksFromJwt(token));

		ResultDTO resultDT = supervisorUtilsService.crearUsuario(user, Utils.getWerksFromJwt(token));
		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/modificar-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public DefaultResponse modificarUsuario(@RequestHeader("Auth") String token,
			@RequestBody UsuarioDTO user) {
		ResultDTO resultDT = supervisorUtilsService.modificarUsuario(user, Utils.getWerksFromJwt(token));
		DefaultResponse response = new DefaultResponse();
		response.setResponseCode(resultDT.getId());
		response.setMessage(resultDT.getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-entregas-agencias", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtieneEntregasAgenciasResponse obtieneEntregasAgencias(@RequestHeader("Auth") String token) {
		/**
		 * Se obtienen entregas en base a lo que el usuario teclea (idUsuario, werks),
		 * no tenemos la lista de usuarios de agencias y no se cuenta con la figura para
		 * que administre a estos usuarios
		 **/
		UsuarioDTO usuario = new UsuarioDTO();
		usuario.setWerks(Utils.getWerksFromJwt(token));
		usuario.setIdUsuario(Utils.getUsuarioFromToken(token));

		EntregasTransportesDTO entregasTransporteDTO = supervisorUtilsService.obtieneEntregasAgencias(usuario);

		ObtieneEntregasAgenciasResponse response = new ObtieneEntregasAgenciasResponse();
		response.setData(entregasTransporteDTO);
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-entregas-agencias-detalle", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtieneEntregasAgenciasDetalleResponse obtieneEntregasAgenciasDetalle(
			@RequestBody ObtieneEntregasAgenciasDetalleRequest request) {
		/**
		 * Se obtiene el detalle de las entregas en base a lo que el usuario teclea
		 * (idUsuario, werks), no tenemos la lista de usuarios de agencias y no se
		 * cuenta con la figura para que administre a estos usuarios
		 **/
		ObtieneEntregasAgenciasDetalleResponse response = new ObtieneEntregasAgenciasDetalleResponse();
		response.setData(supervisorUtilsService.obtieneEntregasAgenciasDetalle(request.getVbeln()));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}

	@PostMapping(path = "/obtiene-bloqueo-carriles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ObtieneBloqueoCarrilesResponse obtieneBloqueoCarriles(@RequestHeader("Auth") String token) {

		UsuarioDTO usuario = new UsuarioDTO();
		usuario.setWerks(Utils.getWerksFromJwt(token));
		usuario.setIdUsuario(Utils.getUsuarioFromToken(token));

		ObtieneBloqueoCarrilesResponse response = new ObtieneBloqueoCarrilesResponse();
		response.setData(supervisorUtilsService.obtieneCarrilBloqueado(usuario));
		response.setResponseCode(response.getData().getResultDT().getId());
		response.setMessage(response.getData().getResultDT().getMsg());

		return response;
	}
}
