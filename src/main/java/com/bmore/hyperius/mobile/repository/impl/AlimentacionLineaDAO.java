package com.bmore.hyperius.mobile.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.config.DBConnectionMob;
import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

public class AlimentacionLineaDAO {
	private static final Logger LOCATION = LoggerFactory.getLogger(AlimentacionLineaDAO.class);
	static String GET_DATA_HU = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx,BESTQ as BESTQ "
			+ " from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR "
			+ " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
	static String GET_HU = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
	static String VALIDA_PICK = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where VBELN = ? and WERKS = ? and idProceso='2'";	
	static String GET_WERKS="SELECT DISTINCT(WERKS) FROM HCMDB.dbo.ZPickingEntregaEntrante where VBELN = ?";
	String OBTIENE_DEPALETIZADORA = "select LGTYP,LGPLA  from HCMDB.dbo.RESB WITH(NOLOCK) where MATNR= ? and AUFNR = ?";
	static String VALIDA_PICKEO_PREVIO_HU = "select EXIDV from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where EXIDV = ? and idProceso='2'";
	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
			+ "where VBELN = ? and Status = '1' and usuarioMontacarga = ? and idProceso='2'";
	static String RESERVA_ESPACIO_HU = "exec sp_bcps_wm_reserva_espacio_hu ?,?,?,?,?,?,?,?,?";
	static String GET_LGPLA="SELECT LGPLA FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) WHERE LENUM = ?";
	// static String CONSUME_HUS =
	// "exec sp_bcps_wm_consume_hus_v2 ?,?,?,?,?,?,?,?,?,?,?,'','',?";
	static String CONSUME_HUS = "exec sp_bcps_wm_consume_hus_alimentacion ?,?,?,?,?,?,?,?,?,?,?";
	public static String getLGPLA(String hu) {
		Connection con = DBConnectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
		stmn=con.prepareStatement(GET_LGPLA);
		stmn.setString(1, hu);
		rs= stmn.executeQuery();
		if(rs.next())
			return rs.getString("LGPLA");
		}catch(Exception e) {
			LOCATION.info(e.toString());
		}
		return "";
	}
	public ResultDT validaOrden(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = DBConnectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		LOCATION.error("Orden: " + ordenProduccionInput.getOrdeProduccion());
		try {
			stmn = con.prepareStatement(VALIDA_PICK);
			stmn.setString(1, ordenProduccionInput.getOrdeProduccion());
			stmn.setString(2, ordenProduccionInput.getWerks());
			LOCATION.info("Orden:" + ordenProduccionInput.getOrdeProduccion());
			LOCATION.info("Werks:" + ordenProduccionInput.getWerks());
			rs = stmn.executeQuery();
			if (rs.next()) {
				// Se caza a que solo es un material en la orden de produccion
				// de otra manera no funcionara
				result.setTypeS(rs.getString("MATNR"));
				result.setId(1);
				result.setMsg("Orden de producción en picking");

			} else {
				result.setId(2);
				result.setMsg("Orden de producción no disponible para picking, contacte al supervisor");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		LOCATION.info("result: " + result.getId());
		return result;
	}
	public OrdenProduccionDetalleDTO getDataHU(String hu, String werks,	String lgtyp, String lgpla) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		LOCATION.info("getDataHU");
		OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();
		Connection con = DBConnectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		PreparedStatement stmn3 = null;
		ResultSet rs3 = null;
		try {
			stmn = con.prepareStatement(GET_HU);
			LOCATION.info("HU:"+hu+
					"\nWerks:"+werks+
					"\nlgtyp:"+lgtyp+
					"\nlgpla:"+lgpla);
			stmn.setString(1, hu);
			stmn.setString(2, werks);
			stmn.setString(3, lgtyp);
			stmn.setString(4, lgpla);
			rs = stmn.executeQuery();
			if (rs.next()) {
				LOCATION.info("Primer Query");
				stmn3 = con.prepareStatement(GET_DATA_HU);
				stmn3.setString(1, hu);
				stmn3.setString(2, werks);
				stmn3.setString(3, lgtyp);
				stmn3.setString(4, lgpla);
				rs3 = stmn3.executeQuery();
				if (rs3.next()) {
					LOCATION.info("Segundo Query");
					result.setId(1);
					result.setMsg("Material encontrado");
					orden.setMaterial(rs3.getString("matnr"));
					orden.setDescripcion(rs3.getString("maktx"));
					orden.setCajas(rs3.getString("vemng"));
					orden.setMe(rs3.getString("meins"));
					orden.setBestq(rs3.getString("bestq"));
				} else {
					result.setId(2);
					result.setMsg("Material no encontrado.");
				}
			} else {
				result.setId(2);
				result.setMsg("El HU no existe o no pertenece a la ubicación.");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		orden.setResultDT(result);
		return orden;
	}
	public OrdenProduccionInput obtieneDepaletizadora(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException {
		Connection con = DBConnectionMob.createConnection();
		OrdenProduccionInput ordenProduccionInputReturn = new OrdenProduccionInput();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet resultado = null;
		try {
			stmn = con.prepareStatement(OBTIENE_DEPALETIZADORA);
			stmn.setString(1, ordenProduccionInput.getMatnr());
			stmn.setString(2, ordenProduccionInput.getOrdeProduccion());
			resultado = stmn.executeQuery();
			if (resultado.next()) {
				ordenProduccionInputReturn.setuDestino2(resultado.getString("LGPLA"));
				resultDT.setId(1);
				resultDT.setMsg("DEPA encontrada");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("Depaletizadora no encontrada, acuda con supervisor");
			}
		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
			}
		}
		ordenProduccionInputReturn.setResultDT(resultDT);
		return ordenProduccionInputReturn;
	}
	public ResultDT confirmaHUsenDepa(OrdenProduccionInput ordenProduccionInput)throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		result.setId(0);
		Connection con = DBConnectionMob.createConnection();
		CallableStatement callableStatement = null;
		try {
			LOCATION.info("CONSUME HUS");
			callableStatement = con.prepareCall(CONSUME_HUS);
			// "@HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGORT, @LGNUM,
			// @LGTYP, @LGPLA, @RESULT
			callableStatement.setString(1, ordenProduccionInput.getHu1());
			callableStatement.setString(2, ordenProduccionInput.getHu2());
			callableStatement.setString(3, ordenProduccionInput.getUsuarioMontacarga());
			callableStatement.setString(4, Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12));
			callableStatement.setString(5, Utils.zeroFill(ordenProduccionInput.getMatnr(), 18));
			callableStatement.setString(6, ordenProduccionInput.getWerks());
			callableStatement.setString(7, ordenProduccionInput.getLgort());
			callableStatement.setString(8, ordenProduccionInput.getuOrigen0());
			callableStatement.setString(9, ordenProduccionInput.getuOrigen1());
			callableStatement.setString(10, ordenProduccionInput.getuOrigen2());
			callableStatement.registerOutParameter(11, java.sql.Types.INTEGER);
			callableStatement.execute();
			int id = 0;
			LOCATION.info("AFTER EXECUTE: " + id);
			id = callableStatement.getInt(11);
			result.setId(id);
			LOCATION.info("AFTER EXECUTE2: " + id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}
		return result;
	}
	public ResultDT validaPickeoPrevioHU(OrdenProduccionInput ordenProduccionInput, String hu) throws ClassNotFoundException {
		Connection con = DBConnectionMob.createConnection();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet resultado = null;
		LOCATION.info("ValidaPickeoPrevio");
		try {
			stmn = con.prepareStatement(VALIDA_PICKEO_PREVIO_HU);
			stmn.setString(1, hu);
			resultado = stmn.executeQuery();
			if (resultado.next()) {
				resultDT.setId(2);
				resultDT.setMsg("HU consumido previamente");
			} else {
				resultDT.setId(1);
				resultDT.setMsg("HU con libre utilización");
			}
		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
			}
		}
		return resultDT;
	}
	public ResultDT limpiaPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = DBConnectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			LOCATION.info("Limpia pendientes DAO :" + vbeln);
			stmn = con.prepareStatement(LIMPIA_PENDIENTE_USUARIO);
			stmn.setString(1, vbeln);
			stmn.setString(2, user);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("Limpieza ejecutada con exito");
			} else {
				result.setId(1);
				result.setMsg("Limpieza ejecutada con exito");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;
	}
	public OrdenProduccionInput reservaUbicaciones(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		result.setId(0);
		Connection con = DBConnectionMob.createConnection();
		CallableStatement callableStatement = null;
		try {
			// @WERKS, @USRMNT, @VBELN, @IDPR, @RETURN, @MATNR, @LGNUM, @LGTYP,
			// @LGPLA
			callableStatement = con.prepareCall(RESERVA_ESPACIO_HU);
			LOCATION.info("Parametros:" + ordenProduccionInput.getWerks()	+ " - " + ordenProduccionInput.getUsuarioMontacarga()+ " - " + ordenProduccionInput.getOrdeProduccion() + " - "	+ 2);
			callableStatement.setString(1, ordenProduccionInput.getWerks());
			callableStatement.setString(2, ordenProduccionInput.getUsuarioMontacarga());
			callableStatement.setString(3, ordenProduccionInput.getOrdeProduccion());
			callableStatement.setString(4, "2");
			callableStatement.registerOutParameter(5, java.sql.Types.INTEGER);
			callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
			callableStatement.execute();
			int id = 0;
			id = callableStatement.getInt(5);
			LOCATION.info("ID:" + id);
			result.setId(id);
			ordenProduccionInput.setMatnr(Utils.zeroClean(callableStatement.getString(6)));
			ordenProduccionInput.setuOrigen0(callableStatement.getString(7));
			ordenProduccionInput.setuOrigen1(callableStatement.getString(8));
			ordenProduccionInput.setuOrigen2(callableStatement.getString(9));
			LOCATION.info("OrdenInput despues de SP: "+ ordenProduccionInput.toString());
		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}
		ordenProduccionInput.setResultDT(result);
		return ordenProduccionInput;
	}
	public static String getWerks(String ordProd) throws ClassNotFoundException{
		String werks="";
		Connection con= DBConnectionMob.createConnection();
		PreparedStatement stmn=null;
		ResultSet rs=null;
		try {
			stmn= con.prepareStatement(GET_WERKS);
			stmn.setString(1,ordProd);
			rs= stmn.executeQuery();
			if(rs.next()) {
				werks= rs.getString("WERKS");
			}
		}catch(SQLException e) {
			LOCATION.error(e.toString());
		}catch(Exception e) {
			LOCATION.error(e.toString());
		}finally {
			try {
				DBConnectionMob.closeConnection(con);
			}catch(Exception e) {
				LOCATION.error(e.toString());				
			}
		}		
		return werks;
	}
}