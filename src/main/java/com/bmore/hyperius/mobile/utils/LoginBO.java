package com.bmore.hyperius.mobile.utils;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginBO {
	private static final Logger LOCATION = LoggerFactory.getLogger(LoginBO.class);
	public static ResultDT Login(String idRed, String sessionId) throws ClassNotFoundException {
		ResultDT resultDt = new ResultDT();
		LoginDTO loginDTO = new LoginDTO();
		boolean isValidUMEUser = true;
		String werks = "";
		int admin = 0;
		if (isValidUMEUser) {
			resultDt = LoginDB.login(idRed);
			if (resultDt.getId() == 1) {
				werks = resultDt.getMsg();
				admin = resultDt.getTypeI();
				LOCATION.info("Usuario: " + idRed);
				loginDTO = LoginDB.existeRegistroUsuario(idRed);
				if (loginDTO.getResult().getId() == 1) {// Existe un registro previo del usuario Revisar si han pasado mas de 15 min desde ultima
					java.util.Date date = new java.util.Date();// operacion, planchar sessionId sin preguntar, caso contrario avisar
					long miliseconds = date.getTime();
					long lastOperation = 0;
					try {
						lastOperation = Long.parseLong(loginDTO.getLastOperation());
					} catch (Exception e) {
					}
					lastOperation = miliseconds - lastOperation;
					LOCATION.info("logout:." + loginDTO.getLogOut() + ".");// Session valida 0
					if ((lastOperation > 900000 || sessionId.equals(loginDTO.getSessionId()) || loginDTO.getLogOut().equals("1"))) {// Planchar
						LOCATION.info("logout OK");
						date = new java.util.Date();
						miliseconds = date.getTime();
						loginDTO.setIdRed(idRed);
						loginDTO.setSessionId(sessionId);
						loginDTO.setLastOperation(miliseconds + "");
						loginDTO.setLastLogin(miliseconds + "");
						resultDt = LoginDB.actualizaRegistroUsuario(loginDTO);
					} else {// avisar que se requiere el planchado de session
						resultDt.setId(998);
						resultDt.setMsg("Existe un registro de sesión en otra máquina, "
								+ "ya sea porque otro usuario está dentro del sistema o porque la aplicación "
								+ "cerro de manera inesperada, ¿Desea terminar la sesión remota?");
					}
				} else if (loginDTO.getResult().getId() == 3) {// No existe un registro previo de la session
					LOCATION.info("ID 3 Registro nuevo en BD");
					java.util.Date date = new java.util.Date();
					long miliseconds = date.getTime();
					loginDTO.setIdRed(idRed);
					loginDTO.setSessionId(sessionId);
					loginDTO.setLastLogin(miliseconds + "");
					loginDTO.setLastOperation(miliseconds + "");
					resultDt = LoginDB.ingresaRegistroUsuario(loginDTO);
				} else {
					resultDt.setId(2);// error
					resultDt.setMsg(loginDTO.getResult().getMsg());
				}
			}
		}
		resultDt.setTypeS(werks);
		resultDt.setTypeI(admin);
		return resultDt;
	}
	public static ResultDT actualizaHoraUltimaOperacion(String idRed) throws ClassNotFoundException {
		LoginDTO loginDTO = new LoginDTO();
		Date date = new java.util.Date();
		long miliseconds = date.getTime();
		loginDTO.setIdRed(idRed);
		loginDTO.setLogOut("");
		loginDTO.setLastOperation(miliseconds + "");
		return LoginDB.actualizaHoraUltimaOperacion(idRed);
	}
	public static ResultDT actualizaRegistroUsuario(String sessionId, String idRed) throws ClassNotFoundException {
		LoginDTO loginDTO = new LoginDTO();
		java.util.Date date = new java.util.Date();
		long miliseconds = date.getTime();
		loginDTO.setIdRed(idRed);
		loginDTO.setSessionId(sessionId);
		loginDTO.setLastLogin(miliseconds + "");
		LOCATION.info("last login "+loginDTO.getLastLogin());
		loginDTO.setLastOperation(miliseconds + "");
		return LoginDB.actualizaRegistroUsuario(loginDTO);
	}

	public static ResultDT checkValidSession(String user, String sessiondId) throws ClassNotFoundException {
		ResultDT resultDt = new ResultDT();
		LoginDTO loginDTO = new LoginDTO();
		loginDTO = LoginDB.existeRegistroUsuario(user);
		if (loginDTO.getResult().getId() == 1) {// Existe un registro previo
			if (sessiondId.equals(loginDTO.getSessionId())) {
				resultDt.setId(1);
			} else {
				if (loginDTO.getLogOut().equals("0")) {
					resultDt.setId(1987);
					resultDt.setMsg("Su sesión fue cerrada porque alguien ingreso desde otra terminal con su usuario");
				} else {
					resultDt.setId(1987);
					resultDt.setMsg("Su sesión expiro, vuelva a ingresar al sistema");
				}
			}
		}
		return resultDt;
	}
	public static LoginDTO retriveUsrData(String idRed) throws ClassNotFoundException {
		LoginDTO usrData=new LoginDTO();
		usrData= LoginDB.retriveData(idRed);
		return usrData;
	}
}
