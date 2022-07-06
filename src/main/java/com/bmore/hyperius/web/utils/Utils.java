/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bmore.hyperius.web.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.config.JwtTokenUtil;
import com.bmore.hyperius.web.dto.ResultDTO;

public class Utils {

	private static final Logger LOCATION = LoggerFactory.getLogger(Utils.class);

	@Autowired
	private static JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

	private static final String LETTER_PATTERN = "^([a-zA-Z])+";
	private static final String DIGIT_PATTERN = "([0-9])+$";
	static String JAVA_SCRITP_FILE = "script_1.0.31.js";
	static String LIMPIA_CARRIL = "exec sp_bcps_wm_actualizaInventario ?, ?, ?";

	public static String zeroFill(String entry, int tamanio) {

		String tChart = entry;
		try {
			for (int i = entry.length(); i < tamanio; i++) {
				tChart = "0" + tChart;
			}
		} catch (Exception e) {

		}
		return tChart;
	}

	public static String isNull(String isNull) {
		isNull = isNull == null ? "" : isNull;
		return isNull;
	}

	public static String zeroClean(String number) {

		try {

			number = new BigDecimal(number).longValue() + "";

		} catch (Exception e) {
			// NA
		}

		return number;

	}
	
	public static void waitForHH(long miliseconds1, long sleep) {}

	public static String regexSplit(String cadena, int posicion) {

		String toreturn = "";

		if ((cadena != null) && (cadena.length() > 0)) {

			switch (posicion) {
			case 1:
				Pattern letter_pattern = Pattern.compile(LETTER_PATTERN);
				Matcher letter_matcher = letter_pattern.matcher(cadena);
				if (letter_matcher.find()) {
					toreturn = letter_matcher.group(0);
				}
				break;

			case 2:
				Pattern digit_pattern = Pattern.compile(DIGIT_PATTERN);
				Matcher digit_matcher = digit_pattern.matcher(cadena);
				if (digit_matcher.find()) {
					toreturn = digit_matcher.group(0);
				}
				break;
			}

		}

		return toreturn;
	}

	public static ResultDTO actualizarInventarioCarriles(String LGNUM, String LGTYP, String LGPLA) {
		ResultDTO result = new ResultDTO();
		Connection con = new DBConnection().createConnection();
		PreparedStatement stmn = null;
		try {
			stmn = con.prepareStatement(LIMPIA_CARRIL);
			stmn.setString(1, LGNUM);
			stmn.setString(2, LGTYP);
			stmn.setString(3, LGPLA);
			int star = stmn.executeUpdate();
			if (star > 0) {
				result.setId(1);
				result.setMsg("Ejecutado Correctamente");
			} else {
				result.setId(1);
				result.setMsg("Ejecutado Correctamente");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error SQLException al ejecutar store procedure LIMPIA_CARRIL: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error Exception al ejecutar store procedure LIMPIA_CARRIL: " + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error al ejecutar store procedure LIMPIA_CARRIL: " + e.toString());
			}
		}
		return result;

	}

	public static String returnJS() {
		return JAVA_SCRITP_FILE;
	}

	public static String listToString(List<String> listToString) {
		String stringForList = "";
		for (String ltoStr : listToString) {
			stringForList += ltoStr + "\n";
		}
		return stringForList;
	}

	public static String getKeyTimeStamp() {

		java.util.Date date = new java.util.Date();
		String timeStamp = new Timestamp(date.getTime()) + "";

		return (timeStamp.replaceAll(":", "_").replaceAll(" ", "").replaceAll("-", "_"));

	}

	/**
	 * Obtiene el werks del Token.
	 * 
	 * @param token de la sesión de usuario
	 * @return {@link String} con el werks o <code>null</code> en caso contrario.
	 */
	public static String getWerksFromJwt(String token) {
		try {
			return jwtTokenUtil.getWerksFromToken(token);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Obtiene el usuario del Token.
	 * 
	 * @param token de la sesión del usuario.
	 * @return {@link String} con el usuario o <code>null</code> en caso contrario.
	 */
	public static String getUsuarioFromToken(String token) {
		try {
			return jwtTokenUtil.getUsuarioFromToken(token);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Obtiene el admin del Token.
	 * 
	 * @param token de la sesión del usuario.
	 * @return {@link String} con el admin o <code>null</code> en caso contrario.
	 */
	public static String getAdminFromToken(String token) {
		try {
			return jwtTokenUtil.getAdminFromToken(token);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Valida si el Token ha expirado.
	 * 
	 * @param token con la sesión del usario.
	 * @return <code>false</code> si el token no ha expirado o <code>true</code> en
	 *         cualquier otro caso.
	 */
	public static int hasTokenExpired(String token) {
		try {
			return jwtTokenUtil.hasTokenExpired(token) ? 2 : 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
