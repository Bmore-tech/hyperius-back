package com.bmore.prueba.web.service;

import javax.servlet.http.HttpSession;

import com.bmore.prueba.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Login.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface LoginService {

	public ResultDTO actualizaHoraUltimaOperacion(String idRed);

	public ResultDTO actualizaRegistroUsuario(HttpSession session, String idRed);

	public ResultDTO checkValidSession(HttpSession session);

	public ResultDTO Login(String idRed, String password, HttpSession session, int opc);

	public ResultDTO loginWebApp(String IdRed);

	public ResultDTO newLogin(String idRed, String password, HttpSession session, int opc);
}
