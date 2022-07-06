package com.bmore.hyperius.mobile.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.config.DBConnectionMob;
import com.bmore.hyperius.mobile.dto.EntregaDetalleDTO;
import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

public class EmbarquePTDAO {
	private static final Logger LOCATION = LoggerFactory.getLogger(EmbarquePTDAO.class);
	static String VALIDA_PICK = "SELECT DISTINCT (VBELN),MATNR FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ? AND WERKS = ?";
	static String VALIDA_PICKEO_PREVIO_HU = "SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV = ? AND idProceso='4'";
	static String GET_DATA_HU = "SELECT LQUA.MATNR AS matnr, LQUA.VERME AS vemng, LQUA.MEINS AS meins, MAKT.MAKTX AS maktx, BESTQ AS BESTQ"
			+ " FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR "
			+ " WHERE LENUM =  ? AND WERKS = ? AND LGTYP = ? AND LGPLA = ?";
	static String GET_HU = "SELECT LQUA.MATNR AS matnr FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) WHERE LENUM =  ? AND WERKS = ? AND LGTYP = ? AND LGPLA = ?";
	static String LIMPIA_PENDIENTE_USUARIO = "UPDATE HCMDB.dbo.ZPickingEntregaEntrante SET status = null, usuarioMontacarga = null  WHERE VBELN = ? AND Status = '1' AND usuarioMontacarga = ? ";
	static String RESERVA_ESPACIO_HU = "exec sp_bcps_wm_reserva_espacio_hu ?,?,?,?,?,?,?,?,?";
	static String CONSUME_HUS = "exec sp_bcps_wm_consume_hus_embarque ?,?,?,?,?,?,?,?,?,?,?";
	static String GET_WERKS="SELECT DISTINCT werks FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ?";
	public EntregaInput validarEntregaPickin(EntregaInput entregaInput) throws ClassNotFoundException {
		EntregaInput entregaInputReturn = new EntregaInput();
		ResultDT result = new ResultDT();
		Connection con = new DBConnectionMob().createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> hashhMap = new HashMap<String, String>();
		try {
			stmn = con.prepareStatement(VALIDA_PICK);
			stmn.setString(1, Utils.zeroFill(entregaInput.getEntrega(), 10));
			stmn.setString(2, entregaInput.getWerks());
			LOCATION.info("Werks: "+entregaInput.getWerks()+"\n"+
							"entrega: "+entregaInput.getEntrega());
			rs = stmn.executeQuery();
			int cont = 0;
			while (rs.next()) {
				cont++;
				hashhMap.put(rs.getString("MATNR"), rs.getString("MATNR"));
				LOCATION.info("Query valida_pick matnr: "+rs.getString("MATNR"));
				result.setId(1);
				result.setMsg("Entrega saliente ya se encuentra en picking");
			}
			if (cont == 0) {
				result.setId(2);
				result.setMsg("Entrega saliente no disponible para picking");
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

		entregaInputReturn.setResultDT(result);
		entregaInputReturn.setMateriales(hashhMap);
		LOCATION.info("validarEntregaPickin id: "+result.getId());
		return entregaInputReturn;
	}
	public EntregaInput reservaUbicaciones(EntregaInput entregaInput) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		result.setId(0);
		Connection con = new DBConnectionMob().createConnection();
		CallableStatement callableStatement = null;
		try {
			// @WERKS, @USRMNT, @VBELN, @IDPR, @RETURN, @MATNR, @LGNUM, @LGTYP,
			// @LGPLA
			callableStatement = con.prepareCall(RESERVA_ESPACIO_HU);
			callableStatement.setString(1, entregaInput.getWerks());
			callableStatement.setString(2, entregaInput.getUsuarioMontacarga());
			callableStatement.setString(3, entregaInput.getEntrega());
			callableStatement.setString(4, "4");
			callableStatement.registerOutParameter(5, java.sql.Types.INTEGER);
			callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
			callableStatement.execute();
			int id = 0;
			id = callableStatement.getInt(5);
			result.setId(id);
			entregaInput.setMatnr(Utils.zeroClean(callableStatement.getString(6)));
			entregaInput.setuOrigen0(callableStatement.getString(7));
			entregaInput.setuOrigen1(callableStatement.getString(8));
			entregaInput.setuOrigen2(callableStatement.getString(9));
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
		entregaInput.setResultDT(result);
		LOCATION.info("reservaUbicaciones id: "+result.getId());
		return entregaInput;
	}
	public ResultDT validaPickeoPrevioHU(EntregaInput entregaInput, String hu) throws ClassNotFoundException{
		Connection con = new DBConnectionMob().createConnection();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet resultado = null;
		try {
			stmn = con.prepareStatement(VALIDA_PICKEO_PREVIO_HU);
			stmn.setString(1, hu);
			resultado = stmn.executeQuery();
			if (resultado.next()) {
				resultDT.setId(2);
				resultDT.setMsg("El HU ya fue consumido");
			} else {
				resultDT.setId(1);
				resultDT.setMsg("HU sin confirmar");
			}
		} catch (SQLException e) {
			resultDT.setId(0);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(0);
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}
		LOCATION.info("validaPickeoPrevioHU id: "+resultDT.getId());
		return resultDT;
	}
	public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();
		Connection con = new DBConnectionMob().createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;
		LOCATION.info("hu: "+hu+"\n"+
					  "werks: "+werks+"\n"+
					  "lgtyp: "+lgtyp+"\n"+
					  "lgpla: "+lgpla+"\n");
		try {
			stmn = con.prepareStatement(GET_HU);
			stmn.setString(1, hu);
			stmn.setString(2, werks);
			stmn.setString(3, lgtyp);
			stmn.setString(4, lgpla);
			rs = stmn.executeQuery();
			if (rs.next()) {
				LOCATION.info("Entro al primer query");
				stmn2 = con.prepareStatement(GET_DATA_HU);
				stmn2.setString(1, hu);
				stmn2.setString(2, werks);
				stmn2.setString(3, lgtyp);
				stmn2.setString(4, lgpla);
				rs2 = stmn2.executeQuery();
				if (rs2.next()) {
					LOCATION.info("Entro al segundo query");
					result.setId(1);
					result.setMsg("Material encontrado");
					entrega.setMaterial(rs2.getString("matnr"));
					entrega.setDescripcion(rs2.getString("maktx"));
					entrega.setCajas(rs2.getString("vemng"));
					entrega.setMe(rs2.getString("meins"));
					entrega.setBestq(rs2.getString("bestq"));
					LOCATION.info("bestq: " + rs2.getString("bestq"));
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
		entrega.setResultDT(result);
		LOCATION.info("getDataHU id: "+result.getId());
		return entrega;
	}
	public ResultDT confirmaHusEnCamionFurgon(EntregaInput entregaInput) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		result.setId(0);
		Connection con = new DBConnectionMob().createConnection();
		CallableStatement callableStatement = null;
		try {
			LOCATION.info("CONSUME HUS");
			// "@HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGORT, @LGNUM,
			// @LGTYP, @LGPLA, @RESULT
			callableStatement = con.prepareCall(CONSUME_HUS);
			LOCATION.info(entregaInput.getHu1() + "-" 
					+ entregaInput.getHu2()+ "-"  
					+ entregaInput.getUsuarioMontacarga() + "-"
					+ entregaInput.getEntrega() + "-" 
					+ entregaInput.getMatnr()	+ "-" 
					+ entregaInput.getWerks() + "-"
					+ entregaInput.getLgort() + "-"
					+ entregaInput.getuOrigen0() + "-"
					+ entregaInput.getuOrigen1() + "-"
					+ entregaInput.getuOrigen2() + "-"
					+ entregaInput.getLfart());
			callableStatement.setString(1, entregaInput.getHu1());
			callableStatement.setString(2, entregaInput.getHu2());
			callableStatement.setString(3, entregaInput.getUsuarioMontacarga());
			callableStatement.setString(4, Utils.zeroFill(entregaInput.getEntrega(), 10));
			callableStatement.setString(5, Utils.zeroFill(entregaInput.getMatnr(), 18));
			callableStatement.setString(6, entregaInput.getWerks());
			callableStatement.setString(7, "");
			callableStatement.setString(8, entregaInput.getuOrigen0());
			callableStatement.setString(9, entregaInput.getuOrigen1());
			callableStatement.setString(10, entregaInput.getuOrigen2());
			callableStatement.registerOutParameter(11, java.sql.Types.INTEGER);
			callableStatement.execute();
			int id = 0;
			LOCATION.error("AFTER EXECUTE: " + id);
			id = callableStatement.getInt(11);
			result.setId(id);
			LOCATION.error("AFTER EXECUTE2: " + id);
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
		LOCATION.info("confirmaHusEnCamionFurgon id: "+result.getId());
		return result;
	}
	public ResultDT limpiaPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		Connection con = new DBConnectionMob().createConnection();
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
		LOCATION.info("limpiaPendientesXUsuario id: "+result.getId());
		return result;
	}
	public String getWerks(String entrega) throws ClassNotFoundException{
		LOCATION.info("getWerksDAO");
		String werks=null;
		Connection con= new DBConnectionMob().createConnection();
		ResultSet rs;
		PreparedStatement stmn;
		try {
			stmn= con.prepareStatement(GET_WERKS);
			stmn.setString(1, Utils.zeroFill(entrega, 10));
			LOCATION.info("entrega: "+Utils.zeroFill(entrega, 10));
			rs=stmn.executeQuery();
			if(rs.next()) {
				werks=rs.getString("WERKS");
			}
		}catch(SQLException e) {
			LOCATION.error(e.toString());
		}catch(Exception e) {
			LOCATION.error(e.toString());
		}finally{
			try {
				DBConnectionMob.closeConnection(con);
			}catch(Exception e) {
				LOCATION.error(e.toString());
			}
		}
		return werks;
	}
}