package com.bmore.prueba.web.service.impl;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.LoginDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.old.LoginRepository;
import com.bmore.prueba.web.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

	private static final Logger LOCATION = LoggerFactory.getLogger(LoginServiceImpl.class);

	@Override
	public ResultDTO Login(String idRed, String password, HttpSession session, int opc) {

		ResultDTO resultDt = new ResultDTO();
		LoginDTO loginDTO = new LoginDTO();

		boolean isValidUMEUser = true;

		String werks = "";
		int admin = 0;
		// resultDt = isValidUMEUser(idRed, password);
		//
		// if (resultDt.getId() == 1 || resultDt.getId() == 4) {
		//
		// if (resultDt.getId() == 4 && opc != 0) {//OPC == 0 no valida
		// password, deja pasar con cualquier cosa, escenario montacarguistas
		// isValidUMEUser = false;
		// }
		//
		// } else {
		// isValidUMEUser = false;
		// }

		if (isValidUMEUser) {

			resultDt = LoginRepository.login(idRed);

			if (resultDt.getId() == 1) {

				werks = resultDt.getMsg();
				admin = resultDt.getTypeI();

				LOCATION.error("Usuario: " + idRed);

				loginDTO = LoginRepository.existeRegistroUsuario(idRed);

				if (loginDTO.getResult().getId() == 1) {// Existe un registro
					// previo
					// del usuario

					// Revisar si han pasado mas de 15 min desde ultima
					// operacion,
					// planchar sessionId sin preguntar, caso contrario avisar

					java.util.Date date = new java.util.Date();
					long miliseconds = date.getTime();
					long lastOperation = 0;

					try {
						lastOperation = Long.parseLong(loginDTO.getLastOperation());
					} catch (Exception e) {

					}

					lastOperation = miliseconds - lastOperation;

					LOCATION.error("logout:." + loginDTO.getLogOut() + ".");

					// Session valida 0
					if ((lastOperation > 900000 || session.getId().equals(loginDTO.getSessionId())
							|| loginDTO.getLogOut().equals("1"))) {// Planchar

						LOCATION.error("logout OK");
						date = new java.util.Date();
						miliseconds = date.getTime();

						loginDTO.setIdRed(idRed);
						loginDTO.setSessionId(session.getId());
						loginDTO.setLastOperation(miliseconds + "");
						loginDTO.setLastLogin(miliseconds + "");

						resultDt = LoginRepository.actualizaRegistroUsuario(loginDTO);

					} else {// avisar que se requiere el planchado de session
						resultDt.setId(998);
						resultDt.setMsg("Existe un registro de sesión en otra máquina, "
								+ "ya sea porque otro usuario está dentro del sistema o porque la aplicación "
								+ "cerro de manera inesperada, ¿Desea terminar la sesión remota?");
					}

				} else if (loginDTO.getResult().getId() == 3) {// No existe un
					// registro previo
					// de la session

					java.util.Date date = new java.util.Date();
					long miliseconds = date.getTime();

					loginDTO.setIdRed(idRed);
					loginDTO.setSessionId(session.getId());
					loginDTO.setLastLogin(miliseconds + "");
					loginDTO.setLastOperation(miliseconds + "");

					resultDt = LoginRepository.ingresaRegistroUsuario(loginDTO);

				} else {
					loginDTO.getResult().setId(2);// error
				}

			}
		}

		resultDt.setTypeS(werks);
		resultDt.setTypeI(admin);

		return resultDt;

	}

	@Override
	public ResultDTO newLogin(String idRed, String password, HttpSession session, int opc) {

		ResultDTO resultDt = new ResultDTO();
		LoginDTO loginDTO = new LoginDTO();

		boolean isValidUMEUser = true;

		String werks = "";
		int admin = 0;
		if (isValidUMEUser) {

			resultDt = LoginRepository.login(idRed);

			if (resultDt.getId() == 1) {

				werks = resultDt.getMsg();
				admin = resultDt.getTypeI();

				LOCATION.error("Usuario: " + idRed);

				loginDTO = LoginRepository.existeRegistroUsuario(idRed);

				if (loginDTO.getResult().getId() == 1) {// Existe un registro
					// previo
					// del usuario

					// Revisar si han pasado mas de 15 min desde ultima
					// operacion,
					// planchar sessionId sin preguntar, caso contrario avisar

					java.util.Date date = new java.util.Date();
					long miliseconds = date.getTime();
					long lastOperation = 0;

					try {
						lastOperation = Long.parseLong(loginDTO.getLastOperation());
					} catch (Exception e) {

					}

					lastOperation = miliseconds - lastOperation;

					LOCATION.error("logout:." + loginDTO.getLogOut() + ".");

					// Session valida 0
					if ((lastOperation > 900000 || session.getId().equals(loginDTO.getSessionId())
							|| loginDTO.getLogOut().equals("1"))) {// Planchar

						LOCATION.error("logout OK");
						date = new java.util.Date();
						miliseconds = date.getTime();

						loginDTO.setIdRed(idRed);
						loginDTO.setSessionId(session.getId());
						loginDTO.setLastOperation(miliseconds + "");
						loginDTO.setLastLogin(miliseconds + "");

						resultDt = LoginRepository.actualizaRegistroUsuario(loginDTO);

					} else {// avisar que se requiere el planchado de session
						resultDt.setId(998);
						resultDt.setMsg("Existe un registro de sesión en otra máquina, "
								+ "ya sea porque otro usuario está dentro del sistema o porque la aplicación "
								+ "cerro de manera inesperada, ¿Desea terminar la sesión remota?");
					}

				} else if (loginDTO.getResult().getId() == 3) {// No existe un
					// registro previo
					// de la session

					java.util.Date date = new java.util.Date();
					long miliseconds = date.getTime();

					loginDTO.setIdRed(idRed);
					loginDTO.setSessionId(session.getId());
					loginDTO.setLastLogin(miliseconds + "");
					loginDTO.setLastOperation(miliseconds + "");

					resultDt = LoginRepository.ingresaRegistroUsuario(loginDTO);

				} else {
					loginDTO.getResult().setId(2);// error
				}

			}
		}

		resultDt.setTypeS(werks);
		resultDt.setTypeI(admin);

		return resultDt;

	}

	@Override
	public ResultDTO actualizaHoraUltimaOperacion(String idRed) {

		LoginDTO loginDTO = new LoginDTO();

		Date date = new java.util.Date();
		long miliseconds = date.getTime();

		loginDTO.setIdRed(idRed);
		loginDTO.setLogOut("");
		loginDTO.setLastOperation(miliseconds + "");

		return LoginRepository.actualizaHoraUltimaOperacion(idRed);

	}

	@Override
	public ResultDTO actualizaRegistroUsuario(HttpSession session, String idRed) {

		LoginDTO loginDTO = new LoginDTO();

		java.util.Date date = new java.util.Date();
		long miliseconds = date.getTime();

		loginDTO.setIdRed(idRed);
		loginDTO.setSessionId(session.getId());
		loginDTO.setLastLogin(miliseconds + "");
		loginDTO.setLastOperation(miliseconds + "");

		return LoginRepository.actualizaRegistroUsuario(loginDTO);

	}

	@Override
	public ResultDTO checkValidSession(HttpSession session) {

		ResultDTO resultDt = new ResultDTO();
		LoginDTO loginDTO = new LoginDTO();

		loginDTO = LoginRepository.existeRegistroUsuario((String) session.getAttribute("user"));

		if (loginDTO.getResult().getId() == 1) {// Existe un registro previo

			if (session.getId().equals(loginDTO.getSessionId())) {
				resultDt.setId(1);
			} else {

				if (loginDTO.getLogOut().equals("0")) {

					resultDt.setId(2);
					resultDt.setMsg("Su sesión fue cerrada porque alguien ingreso desde otra terminal con su usuario");

				} else {
					resultDt.setId(2);
					resultDt.setMsg("Su sesión expiro, vuelva a ingresar al sistema");
				}

				// session.removeAttribute("user");
				// session.removeAttribute("werks");
				// session.invalidate();
				// LOCATION.error("Cerrando session");
			}
		}

		return resultDt;
	}

	/*
	 * public ResultDT isValidUMEUser(String user, String password) {
	 * 
	 * ResultDT resultDT = new ResultDT();
	 * 
	 * try { IUserAccount userAccount = UMFactory.getUserAccountFactory()
	 * .getUserAccountByLogonId(user); if (!userAccount.isPasswordDisabled()) { if
	 * (!userAccount.isUserAccountLocked()) {
	 * 
	 * if (userAccount.checkPassword(password)) { resultDT.setId(1);
	 * resultDT.setMsg("Usuario validado correctamente"); } else {
	 * resultDT.setId(4); resultDT.setMsg("Password incorrecto"); }
	 * 
	 * } else { LOCATION.error("Password disabled for user: " + user);
	 * resultDT.setId(4); resultDT.setMsg("Password disabled for user: " + user); }
	 * } else { LOCATION.error("Account blocked for user: " + user);
	 * resultDT.setId(4); resultDT.setMsg("Account blocked for user: " + user); } }
	 * catch (UMException e) {
	 * 
	 * LOCATION.error("Hubo un error al validar el usuario. " + e.getMessage());
	 * 
	 * resultDT.setId(2); resultDT.setMsg("Hubo un error al validar el usuario. " +
	 * e.getMessage()); e.printStackTrace(); }
	 * 
	 * return resultDT;
	 * 
	 * }
	 */

	@Override
	public ResultDTO loginWebApp(String IdRed) {
		ResultDTO resultDt = new ResultDTO();
		resultDt = LoginRepository.loginAppWeb(IdRed);
		return resultDt;
	}
}
