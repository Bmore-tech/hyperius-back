package com.bmore.prueba.web.repository.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.config.DBConnection;
import com.bmore.prueba.web.dto.LoginDTO;
import com.bmore.prueba.web.dto.NewSecureLoginDTO;
import com.bmore.prueba.web.dto.ResultDTO;

public class LoginRepository {

	private static final Logger LOCATION = LoggerFactory.getLogger(LoginRepository.class);

	private static final String LOCAL = "local";
	private static final String ACTIVEDIRECTORY = "LDAP";
	private static final String OPERADOR = "operador";

	static String EXISTE_USUARIO = "select WERKS, ZADMIN, ZWebApp from  HCMDB.dbo.zUsuario where IDRED = ?";

	static String NEW_EXISTE_USUARIO = "select WERKS, ZADMIN, ZWebApp, DataSource, zPassword from  HCMDB.dbo.zUsuario where IDRED = ?";

	static String EXISTE_REGITRO_USUARIO = "select * from HCMDB.dbo.zSession where idRed = ?";

	static String INGRESA_REGISTRO_USUARIO = "insert into  HCMDB.dbo.zSession (idRed,sessionId,lastLogin,lastOperation,logOut) VALUES (?,?,?,?,0)";

	static String ACTUALIZA_REGISTRO_USUARIO = "update  HCMDB.dbo.zSession set sessionId = ? , lastLogin = ?, lastOperation = ?,  logOut = 0 where idRed = ?";

	static String ACTUALIZA_HORA_ULTIMA_OPERACION = "update  HCMDB.dbo.zSession set lastOperation = ? where idRed = ?";

	static String LOG_OUT = "update HCMDB.dbo.zSession set logOut = '1' where idRed = ?";

	public static ResultDTO login(String entry) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(EXISTE_USUARIO);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {

				result.setMsg(rs.getString("WERKS"));
				LOCATION.error("ENTERO: " + rs.getInt("ZADMIN"));

				result.setTypeI(rs.getInt("ZADMIN"));
				LOCATION.error("ENTERO: " + result.getTypeI());
				result.setId(1);

			} else {

				result.setId(2);
				result
						.setMsg("No se tiene registrado un centro para el usuario: "
								+ entry);

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	static String OBTIENE_FUENTE_DATOS = "SELECT IDRED, DataSource, zPassword, WERKS, ZADMIN, ZWebApp from zUsuario WITH(NOLOCK) WHERE IDRED = ?";

	public static ResultDTO newLogin(NewSecureLoginDTO entry) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		String fuenteDatos = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		try {
			stm1 = con.prepareStatement(OBTIENE_FUENTE_DATOS);
			stm1.setString(1, entry.getUser());
			rs1 = stm1.executeQuery();
			LOCATION.info("entry.getUser()" + entry.getUser());
			if (rs1.next()) {
				fuenteDatos = rs1.getString("DataSource");
				if (fuenteDatos.equalsIgnoreCase(LOCAL)) {
					// LOCATION.error("Usuario: "
					// + entry.getUser()
					// + "| Contraseña: "
					// + entry.getPassword()
					// + "| Matching pass: "
					// + rs1.getString("zPassword")
					// + "  - Decoded : "
					// + new String(Base64.decodeBase64(rs1.getString(
					// "zPassword").getBytes())));
					if (entry.getPassword().equals(
							new String(Base64.decodeBase64(rs1
									.getString("zPassword"))))) {
						result.setId(1);
						result.setMsg(rs1.getString("WERKS"));
						result.setTypeI(rs1.getInt("ZADMIN"));
					} else {
						result.setId(2);
						result.setMsg("Contraseña Incorrecta");
					}
				} else if (fuenteDatos.equalsIgnoreCase(ACTIVEDIRECTORY)) {
					result = secureLdapLogin(entry);
					if (result.getId() == 1) {
						result.setMsg(rs1.getString("WERKS"));
						result.setTypeI(rs1.getInt("ZADMIN"));
					} else {
						if (result.getMsg().contains("AcceptSecurityContext")) {
							result.setMsg("Contraseña Incorrecta");
						} else if (result.getMsg().contains("timeout")) {
							result
									.setMsg("Timeout al tratar de validar las credenciales del usuario");
						}
					}
				} else if (fuenteDatos.equalsIgnoreCase(OPERADOR)) {
					result.setId(2);
					result
							.setMsg("Usuario Operador solo puede ingresar mediante HandHeld: "
									+ entry.getUser());
				} else {
					result.setId(2);
					result
							.setMsg("Usuario sin Privilegios para acceder al sistema: "
									+ entry.getUser());
				}
			} else {
				result.setId(2);
				result
						.setMsg("Usuario sin Privilegios para acceder al sistema: "
								+ entry.getUser());
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	public static ResultDTO loginAppWeb(String entry) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(EXISTE_USUARIO);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {

				if (rs.getInt("ZWebApp") == 0) {

					result.setId(2);
					result.setMsg("El usuario " + entry
							+ " no tiene permisos para la aplicacion Web");

				} else {

					result.setMsg(rs.getString("WERKS"));
					LOCATION.error("ENTERO: " + rs.getInt("ZADMIN"));

					result.setTypeI(rs.getInt("ZADMIN"));
					LOCATION.error("ENTERO: " + result.getTypeI());
					result.setId(1);

				}
			} else {

				result.setId(2);
				result
						.setMsg("No se tiene registrado un centro para el usuario: "
								+ entry);

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	public static LoginDTO existeRegistroUsuario(String entry) {

		LoginDTO loginDTO = new LoginDTO();
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		LOCATION.error("ID: " + result.getId());
		loginDTO.setResult(result);
		return loginDTO;
	}

	public static ResultDTO ingresaRegistroUsuario(LoginDTO loginDTO) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(INGRESA_REGISTRO_USUARIO);
			stmn.setString(1, loginDTO.getIdRed());
			stmn.setString(2, loginDTO.getSessionId());
			stmn.setString(3, loginDTO.getLastLogin());
			stmn.setString(4, loginDTO.getLastOperation());

			int res = 0;
			res = stmn.executeUpdate();

			if (res > 0) {

				result.setId(1);

			} else {
				result.setId(2);
				result
						.setMsg("No fue posible ingresar al usuario en el control de logueos.");
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	public static ResultDTO actualizaHoraUltimaOperacion(String idRed) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
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
				LOCATION.error("Se ingreso usuario en tabla de logueo");
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	public static ResultDTO actualizaRegistroUsuario(LoginDTO loginDTO) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(ACTUALIZA_REGISTRO_USUARIO);

			stmn.setString(1, loginDTO.getSessionId());
			stmn.setString(2, loginDTO.getLastLogin() + "");
			stmn.setString(3, loginDTO.getLastOperation() + "");
			stmn.setString(4, loginDTO.getIdRed());

			int res = 0;
			res = stmn.executeUpdate();

			if (res > 0) {
				LOCATION.error("Se actualizo el usuario en tabla de logueo");
				result.setId(1);
			} else {
				LOCATION
						.error("No fue posible actualizar el usuario en tabla de logueo");
				result.setId(2);
				result
						.setMsg("No fue posible actualizar el usuario en tabla de logueo");
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	public static ResultDTO logOut(String idRed) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			stmn = con.prepareStatement(LOG_OUT);

			stmn.setString(1, idRed);

			int res = 0;
			res = stmn.executeUpdate();

			if (res > 0) {
				LOCATION.error("LogOut con exito");
			} else {
				LOCATION.error("Error en logOut");
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				LOCATION.error("Error: " + e.toString());
			}
		}
		return result;
	}

	public static ResultDTO secureLdapLogin(NewSecureLoginDTO entry) {
		DirContext ldapContext;
		ResultDTO resultDT = new ResultDTO();
		try {
			Hashtable<String, String> ldapEnv = new Hashtable<String, String>(
					11);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL,
					"ldap://modelo.gmodelo.com.mx:389");
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, "Modelo\\"
					+ entry.getUser());
			ldapEnv.put(Context.SECURITY_CREDENTIALS, entry.getPassword());
			ldapEnv.put("java.naming.ldap.attributes.binary", "objectSid");
			ldapEnv.put(Context.REFERRAL, "follow");

			ldapContext = new InitialDirContext(ldapEnv);
			SearchControls searchCtls = new SearchControls();

			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			String searchFilter = "(&(objectClass=user)(samaccountname="
					+ entry.getUser() + "))";
			String searchBase = "DC=modelo,DC=gmodelo,DC=com,DC=mx";
			//int totalResults = 0;
			NamingEnumeration<SearchResult> answer = ldapContext.search(
					searchBase, searchFilter, searchCtls);
			// Loop through the search results
			while (answer.hasMoreElements()) {
				// SearchResult sr = (SearchResult) answer.next();
				answer.next();
				// totalResults++;
			}
			resultDT.setId(1);
			resultDT.setMsg("Successfull Login");
			ldapContext.close();
		} catch (Exception e) {
			resultDT.setId(3);
			resultDT.setMsg(e.getMessage());
		}
		return resultDT;
	}
}
