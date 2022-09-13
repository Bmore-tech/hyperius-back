package com.bmore.hyperius.mobile.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bmore.hyperius.config.DBConnectionMob;

@Component
public class LoginDB {

  @Autowired
  private DBConnectionMob connectionMob;
  
	private static final Logger LOCATION = LoggerFactory.getLogger(LoginDB.class);
	static String EXISTE_USUARIO = "SELECT WERKS, ZADMIN FROM  HCMDB.dbo.zUsuario WHERE IDRED = ?";
	static String EXISTE_REGITRO_USUARIO = "SELECT * FROM HCMDB.dbo.zSession WHERE idRed = ?";
	static String INGRESA_REGISTRO_USUARIO = "INSERT INTO  HCMDB.dbo.zSession (idRed,sessionId,lastLogin,lastOperation,logOut) VALUES (?,?,?,?,0)";
	static String ACTUALIZA_REGISTRO_USUARIO = "UPDATE  HCMDB.dbo.zSession SET sessionId = ? , lastLogin = ?, lastOperation = ?,  logOut = 0 WHERE idRed = ?";
	static String ACTUALIZA_HORA_ULTIMA_OPERACION = "UPDATE  HCMDB.dbo.zSession SET lastOperation = ? WHERE idRed = ?";
	static String LOG_OUT = "UPDATE  HCMDB.dbo.zSession SET logOut = '1' WHERE idRed = ?";
	static String RETRIVE_DATA= "SELECT * FROM HCMDB.dbo.zSession WHERE idRed= ?";

	public  ResultDT login(String entry) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(EXISTE_USUARIO);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {
				result.setMsg(rs.getString("WERKS"));
				LOCATION.info("ENTERO: " + rs.getInt("ZADMIN"));
				result.setTypeI(rs.getInt("ZADMIN"));
				LOCATION.info("ENTERO: " + result.getTypeI());
				result.setId(1);
			} else {
				result.setId(2);
				result.setMsg("No se tiene registrado un centro para el usuario: " + entry);
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error: " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}
	public  LoginDTO existeRegistroUsuario(String entry) throws ClassNotFoundException {
		LoginDTO loginDTO = new LoginDTO();
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(EXISTE_REGITRO_USUARIO);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {
				loginDTO.setLastLogin(rs.getString("lastLogin"));
				loginDTO.setIdRed(rs.getString("idRed"));
				loginDTO.setLastOperation(rs.getString("lastOperation"));
				loginDTO.setLogOut(rs.getString("logOut"));
				loginDTO.setSessionId(rs.getString("sessionId"));
				result.setId(1);
			} else {
				result.setId(3);
				result.setMsg("Usuario no existe desde DB: " + entry);
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error: " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		LOCATION.info("ID: " + result.getId());
		loginDTO.setResult(result);
		return loginDTO;
	}
	public  ResultDT ingresaRegistroUsuario(LoginDTO loginDTO) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			LOCATION.error("Ingresar a ZSESSION");
			stmn = con.prepareStatement(INGRESA_REGISTRO_USUARIO);
			stmn.setString(1, loginDTO.getIdRed());
			stmn.setString(2, loginDTO.getSessionId());
			stmn.setString(3, loginDTO.getLastLogin());
			stmn.setString(4, loginDTO.getLastOperation());
			int res = 0;
			res = stmn.executeUpdate();
			if (res > 0) {
				LOCATION.info("UPDATE ->" + res);
				result.setId(1);
			} else {
				result.setId(2);
				result.setMsg("No fue posible ingresar al usuario en el control de logueos.");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error: " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}
	public ResultDT actualizaHoraUltimaOperacion(String idRed) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		java.util.Date date = new java.util.Date();
		long miliseconds = date.getTime();
		try {
			stmn = con.prepareStatement(ACTUALIZA_HORA_ULTIMA_OPERACION);
			stmn.setString(1, miliseconds + "");
			stmn.setString(2, idRed);
			int res = 0;
			res = stmn.executeUpdate();
			if (res > 0) {
				LOCATION.info("Se ingreso usuario en tabla de logueo");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error: " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}
	public ResultDT actualizaRegistroUsuario(LoginDTO loginDTO) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			stmn = con.prepareStatement(ACTUALIZA_REGISTRO_USUARIO);
			stmn.setString(1, loginDTO.getSessionId());
			stmn.setString(2, loginDTO.getLastLogin() + "");
			stmn.setString(3, loginDTO.getLastOperation() + "");
			stmn.setString(4, loginDTO.getIdRed());
			LOCATION.info("Usuario: " + loginDTO.getIdRed());
			int res = 0;
			res = stmn.executeUpdate();
			if (res == 1) {
				LOCATION.info("Se actualizo el usuario en tabla de logueo");
				result.setId(1);
			} else {
				LOCATION.error("No fue posible actualizar el usuario en tabla de logueo");
				result.setMsg("No fue posible actualizar el usuario en tabla de logueo");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error: " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}
	public  ResultDT logOut(String idRed) throws ClassNotFoundException {

		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			stmn = con.prepareStatement(LOG_OUT);
			stmn.setString(1, idRed);
			int res = 0;
			res = stmn.executeUpdate();
			result.setId(res);
			if (res == 1)
				result.setMsg("Sesión cerrada con éxito.");
			else
				result.setMsg("Error al cerrar sesión.");

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
			LOCATION.error("Error: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			LOCATION.error("Error: " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}
	public LoginDTO retriveData(String idRed) throws ClassNotFoundException {
		LoginDTO data= new LoginDTO();
		ResultSet rs= null;
		PreparedStatement stmn = null;
		Connection con= connectionMob.createConnection();
		try {
			stmn= con.prepareStatement(RETRIVE_DATA);
			stmn.setString(1, idRed);
			rs= stmn.executeQuery();
			if(rs.next()) {
				data.setIdRed(rs.getString("idRed"));
				data.setSessionId(rs.getString("sessionId"));
				data.setLastLogin(rs.getString("lastLogin"));
				data.setLastOperation(rs.getString("lastOperation"));
				data.setLogOut(rs.getString("logOut"));
				data.setReloadLogin(true);
			}else {
				data.setIdRed(idRed);
			}
			
		}catch(SQLException e) {
			LOCATION.error(e.toString());
		}finally{
			try {
				DBConnectionMob.closeConnection(con);
			}catch(Exception e) {
				LOCATION.error(e.toString());
			}
		}
		return data;
	}
}
