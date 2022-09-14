package com.bmore.hyperius.mobile.repository.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnectionMob;
import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.UtilsMob;

@Repository
public class EmbarquePTIMDAO {
  
  @Autowired
  private DBConnectionMob connectionMob;

	private static final Logger LOCATION = LoggerFactory.getLogger(EmbarquePTIMDAO.class);

	// HH
	static String VALIDA_PICK = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingVidrio where VBELN = ? and WERKS = ?";

	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingVidrio set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and usuarioMontacarga = ? ";

	static String STOCK = "select MATNR,VEMNG,VEMEH,count(*) as stock from HCMDB.dbo.VEPO "
			+ "where VELIN='1' and ((BESTQ != 'S') or (BESTQ is null)) and  VENUM in ( "
			+ "select venum from HCMDB.dbo.VEKP where WERKS = ? and HU_LGORT='X')	 "
			+ "and MATNR= ? group by  MATNR,VEMNG,VEMEH order by stock desc";

	static String RESEVA_HUS_EMBARQUE = "exec sp_bcps_im_embarquePT_reserva_hu ?, ?,?,?, ?, ?, ?,?,?,?";

	static String OBTIENE_DESCRIPCION_MATERIAL = "select top (1) ARKTX from  HCMDB.dbo.LIPS where VBELN=? and MATNR=?";

	static String CONSUME_HUS = "exec sp_bcps_im_embarquePT_consume_hu ?, ?, ?,?, ?, ?, ?, ?";

	public EntregaInput validarEntregaPickin(EntregaInput entregaInput) throws ClassNotFoundException{

		EntregaInput entregaInputReturn = new EntregaInput();
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> hashhMap = new HashMap<String, String>();
		try {
			stmn = con.prepareStatement(VALIDA_PICK);

			stmn.setString(1, UtilsMob.zeroFill(entregaInput.getEntrega(), 10));
			stmn.setString(2, entregaInput.getWerks());
			rs = stmn.executeQuery();
			int cont = 0;

			while (rs.next()) {
				cont++;
				// Se caza a que solo es un material en la orden de produccion
				// de otra manera esto no funcionara

				hashhMap.put(rs.getString("MATNR"), rs.getString("MATNR"));
				result.setId(1);
				result.setMsg("Entrega saliente ya se encuentra en picking");

			}

			if (cont == 0) {
				result.setId(0);
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
		return entregaInputReturn;
	}

	public ResultDT reservaHus(EntregaInput entregaInput) throws ClassNotFoundException{

		ResultDT result = new ResultDT();
		result.setId(0);

		Connection con = connectionMob.createConnection();

		CallableStatement callableStatement = null;

		try {

			// exec sp_reserva_zpicking_vidrio 'PV11', 'h0008340','0400584428' ,
			// '4','000000000002000311', 0, @var1
			// exec sp_reserva_zpicking_vidrio @WERKS, @USRMNT, @VBELN, @IDPR,
			// @MATNR, @RETURN, @CANTIDAD_EXIDV

			callableStatement = con.prepareCall(RESEVA_HUS_EMBARQUE);

			callableStatement.setString(1, entregaInput.getWerks());
			callableStatement.setString(2, entregaInput.getUsuarioMontacarga());
			callableStatement.setString(3, entregaInput.getEntrega());
			callableStatement.setString(4, "4");

			callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
			callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(10, java.sql.Types.VARCHAR);
			callableStatement.execute();

			int id = 0;

			String cantidad = "";
			String material = "";
			String cantidadTotal = "";
			String um = "";
			String cantidadPickeada = "";

			material = callableStatement.getString(5);
			id = callableStatement.getInt(6);
			cantidad = callableStatement.getString(7);
			cantidadTotal = callableStatement.getString(8);
			um = callableStatement.getString(9);
			cantidadPickeada = callableStatement.getString(10);

			LOCATION.error("id: " + id + " material:" + material + "  cantidadTotal: " + cantidadTotal + " cantidadPickeada: " + cantidadPickeada);
			result.setId(id);
			if (id == 1) {

				result.setTypeS(material);
				result.setTypeF(Float.parseFloat(cantidad));
				result.setMsg(um);
				BigDecimal cantidadTotalD = new BigDecimal("0.00");
				BigDecimal cantidadTotalZPickingD = new BigDecimal("0.00");

				try {

					cantidadTotalD = new BigDecimal(cantidadTotal).setScale(3,RoundingMode.HALF_UP);

					cantidadTotalZPickingD = new BigDecimal(cantidadPickeada).setScale(3, RoundingMode.HALF_UP);

					cantidadTotalD = cantidadTotalD.subtract(cantidadTotalZPickingD);

				} catch (Exception e) {

				}

				result.setTypeBD(cantidadTotalD);

			}

		} catch (SQLException e) {
			LOCATION.error("SQLException: " + e.toString());
			result.setId(200);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Exception: " + en.toString());
			result.setId(200);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception: " + e.toString());
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}

		return result;

	}

	public ResultDT obtieneDescripcionMaterial(String matnr, String vblen) throws ClassNotFoundException{

		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		try {
			LOCATION.error("MATNR: " + matnr);
			LOCATION.error("Vblen: " + vblen);
			stmn2 = con.prepareStatement(OBTIENE_DESCRIPCION_MATERIAL);

			stmn2.setString(1, vblen);
			stmn2.setString(2, matnr);

			rs2 = stmn2.executeQuery();
			if (rs2.next()) {

				result.setId(1);
				result.setMsg("Material encontrado");
				result.setTypeS(rs2.getString("ARKTX"));

			} else {
				result.setId(2);
				result.setMsg("Material no encontrado");
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

	public ResultDT consumeHUs(EntregaInput entregaInput) throws ClassNotFoundException{

		ResultDT result = new ResultDT();
		result.setId(0);

		Connection con = connectionMob.createConnection();

		CallableStatement callableStatement = null;

		try {

			// exec sp_vidrio_embarquePT_consume_hu @EXIDV, @WERKS, @MATNR,
			// @VEMNG, @SKIPBESTQ, @USER, @VBLEN, @RESULT

			callableStatement = con.prepareCall(CONSUME_HUS);

			callableStatement.setString(1, entregaInput.getHu1());
			callableStatement.setString(2, entregaInput.getWerks());
			callableStatement.setString(3, UtilsMob.zeroFill(entregaInput.getMatnr(), 18));
			callableStatement.setString(4, entregaInput.getCant());
			callableStatement.setString(5, entregaInput.getHu2());
			callableStatement.setString(6, entregaInput.getUsuarioMontacarga());
			callableStatement.setString(7, entregaInput.getEntrega());

			callableStatement.registerOutParameter(8, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;

			id = callableStatement.getInt(8);
			result.setId(id);
			LOCATION.error("ID DE CONSUMIR HUS: " + id);

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

	public ResultDT limpiaPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;

		try {

			LOCATION.error("Limpia pendientes DAO :" + vbeln);

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

}