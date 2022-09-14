package com.bmore.hyperius.mobile.repository.impl;

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
import com.bmore.hyperius.mobile.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.mobile.dto.EntregaDetalleDTO;
import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.UtilsMob;

@Repository
public class RecepcionEnvaseDAO {
  
  @Autowired
  private DBConnectionMob connectionMob;

	private static final Logger LOCATION = LoggerFactory.getLogger(RecepcionEnvaseDAO.class);
	static String RETRIVE_WERKS="SELECT werks FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV= ?";
	static String VALIDA_PICK = "SELECT DISTINCT(MATNR) FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ?";
	static String GETFALTANTES = "SELECT count(*) FROM "
			+ "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ? AND Status is null AND EXIDV is null";
	static String GET_VBELN_FROM_HU_SAP = "SELECT VEPO.VBELN, LIKP.LFART FROM HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
			+ "on VEPO.VENUM = VEKP.VENUM AND VEKP.EXIDV=? AND VEPO.VELIN='1' "
			+ "inner join LIKP LIKP on VEPO.VBELN= LIKP.VBELN";
	static String GET_VBELN_FROM_HU_BCPS = "SELECT ENTREGA as VBELN FROM zContingencia zCon WITH(NOLOCK) "
			+ " inner join HCMDB.dbo.LIKP LIKP WITH(NOLOCK) "
			+ "on zCon.ENTREGA = LIKP.VBELN AND LIKP.KUNNR = (SELECT KUNNR FROM zCentrosBCPS WITH(NOLOCK)"
			+ "WHERE werks = ? ) AND zCon.HU= ? AND (zCon.IDPROC=28 or zCon.IDPROC=8)";
	static String RESERVAR_CARRIL_HU1 = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set EXIDV = ?, usuarioMontacarga = ? WHERE VBELN = ? AND MATNR = ?  AND  Status is null AND EXIDV is null AND idProceso=1";
	static String RESERVAR_CARRIL_HU2 = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set EXIDV = ?, usuarioMontacarga = ? WHERE VBELN = ? AND MATNR = ? AND LGTYP = ? AND LGPLA = ?  AND  Status is null AND EXIDV is null AND idProceso=1";
	static String CONSULTA_RESERVA_CARRIL_HU = "SELECT LGNUM, LGTYP, LGPLA, STATUS FROM "
			+ " HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN=? AND EXIDV=? AND idProceso=1";
	static String GET_DATA_HU = "SELECT vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH FROM "
			+ "HCMDB.dbo.MAKT MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)"
			+ " on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
			+ "VEKP.venum = VEPO.venum WHERE VEKP.EXIDV=? AND VEPO.VELIN ='1'";
	static String GET_DATA_HU_LQUA = "SELECT LQUA.MATNR AS MATNR, MAKT.MAKTX AS MAKTX, LQUA.VERME AS VEMNG, MEINS AS VEMEH FROM LQUA LQUA "
			+ "INNER JOIN MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR "
			+ "WHERE LENUM = ?";
	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
			+ "WHERE VBELN = ? AND Status is null AND usuarioMontacarga = ? AND idProceso='1'";
	static String VALIDA_PICKEO_PREVIO_HU = "SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV = ? AND idProceso='1' AND Status='X'";
	static String CONSUME_HUS = "exec sp_bcps_wm_consume_hus_recepcion ?,?,?,?,?,?,?,?,?,?,?,?";
	public String getWerks(String Hu) throws ClassNotFoundException{
		Connection con= connectionMob.createConnection();
		PreparedStatement stmn=null;
		ResultSet rs=null;
		String Werks="";
		try {
			stmn=con.prepareStatement(RETRIVE_WERKS);
			stmn.setString(1,Hu);
			rs=stmn.executeQuery();
			while(rs.next()) {
				Werks=rs.getString("werks");
			}
		}catch(Exception e) {
			LOCATION.error(e.toString());
		}finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.toString());
			}
		}
		return Werks;
	}
	public EntregaInput validarEntregaPickin(String entrega) throws ClassNotFoundException {
		EntregaInput entregaInput = new EntregaInput();
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			stmn = con.prepareStatement(VALIDA_PICK);
			LOCATION.info("Entrega: "+entrega);
			stmn.setString(1, entrega);
			rs = stmn.executeQuery();
			int cont = 0;
			while (rs.next()) {
				cont++;
				LOCATION.info(rs.getString("MATNR"));
				//map.put("MATNR",rs.getString("MATNR"));
				map.put(rs.getString("MATNR"),"MATNR");
				result.setId(1);
				result.setMsg("Entrega en picking");
			}
			if (cont == 0) {
				result.setId(2);
				result.setMsg("No disponible para pickign, acuda con supervisor.");
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

		LOCATION.info("validarEntregaPickin id: "+result.getId());
		entregaInput.setMateriales(map);
		entregaInput.setResultDT(result);
		return entregaInput;
	}
	public int getFaltantes(String entrega) throws ClassNotFoundException{
		Connection con = connectionMob.createConnection();
		int x = 999999;
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GETFALTANTES);
			stmn.setString(1, entrega);
			rs = stmn.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					x = rs.getInt(1);
				} else {
					x = 0;
				}
			} else {
				x = 999999;
			}

		} catch (SQLException e) {
			x = 999999;
		} catch (Exception en) {
			x = 999999;
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				x = 999999;
			}
		}

		return x;
	}
	public ResultDT getVBELNFromHuSAP(String hu, String werks) throws ClassNotFoundException{
		Connection con = connectionMob.createConnection();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		resultDT.setTypeS("");
		try {
			stmn = con.prepareStatement(GET_VBELN_FROM_HU_SAP);
			stmn.setString(1, hu);
			LOCATION.info("Hu: "+hu);
			rs = stmn.executeQuery();
			if (rs.next()) {
				resultDT.setTypeS(rs.getString("VBELN"));
				resultDT.setId(1);
				resultDT.setMsg(rs.getString("LFART"));
			} else {
				resultDT.setId(0);
				resultDT.setMsg("Entrega no encontrada vía HU y con centro de montacarguista: "	+ werks);
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

		LOCATION.info("getVBELNFromHuSAP id: "+resultDT.getId());
		return resultDT;
	}
	public ResultDT getVBELNFromHuBCPS(String hu, String werks) throws ClassNotFoundException {

		Connection con = connectionMob.createConnection();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		resultDT.setTypeS("");

		try {
			stmn = con.prepareStatement(GET_VBELN_FROM_HU_BCPS);

			stmn.setString(1, werks);
			stmn.setString(2, hu);

			rs = stmn.executeQuery();
			if (rs.next()) {

				resultDT.setTypeS(rs.getString("VBELN"));

				resultDT.setId(1);
				resultDT.setMsg("Entrega encontrada");

			} else {
				resultDT.setId(2);
				resultDT.setMsg("Entrega no encontrada vÃ­a HU y con centro de montacarguista: " + werks);
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

		LOCATION.info("getVBELNFromHuBCPS id: "+resultDT.getId());
		return resultDT;
	}
	public ResultDT reservarCarrilHU(String entrega, String hu, String matnr,String usuarioMontacargas) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(RESERVAR_CARRIL_HU1);

			stmn.setString(1, hu);
			stmn.setString(2, usuarioMontacargas);
			stmn.setString(3, entrega);
			stmn.setString(4, matnr);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("HU reservado");
			} else {
				result.setId(0);
				result.setMsg("No fue posible reservar HU");
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
		LOCATION.info("reservarCarrilHU id: "+result.getId());
		return result;

	}
	public ResultDT reservarCarrilHU(String entrega, String hu, String matnr,String usuarioMontacargas, String lgtyp, String lgpla) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(RESERVAR_CARRIL_HU2);

			stmn.setString(1, hu);
			stmn.setString(2, usuarioMontacargas);
			stmn.setString(3, entrega);
			stmn.setString(4, matnr);
			stmn.setString(5, lgtyp);
			stmn.setString(6, lgpla);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("HU reservado");
			} else {
				result.setId(3);
				result.setMsg("Continue solo con una HU");
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
		LOCATION.info("reservarCarrilHU id: "+result.getId());
		return result;

	}
	public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) throws ClassNotFoundException {

		Connection con = connectionMob.createConnection();

		CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(CONSULTA_RESERVA_CARRIL_HU);

			stmn.setString(1, vbeln);
			stmn.setString(2, hu);

			rs = stmn.executeQuery();
			if (rs.next()) {

				carrilUbicacionDTO.setLGNUM(rs.getString("LGNUM"));
				carrilUbicacionDTO.setLGTYP(rs.getString("LGTYP"));
				carrilUbicacionDTO.setLGPLA(rs.getString("LGPLA"));

				resultDT.setId(1);
				resultDT.setMsg("HU1 encontrada");
				resultDT.setTypeS(rs.getString("STATUS"));

			} else {
				resultDT.setId(0);
				resultDT.setMsg("El HU no esta diponible para pickear");
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

		carrilUbicacionDTO.setResultDT(resultDT);

		LOCATION.info("consultReservaCarrilHu id: "+resultDT.getId());
		return carrilUbicacionDTO;
	}
	public EntregaDetalleDTO getDataHU(String hu) throws ClassNotFoundException {

		ResultDT result = new ResultDT();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GET_DATA_HU);
			stmn.setString(1, hu);

			rs = stmn.executeQuery();
			if (rs.next()) {

				result.setId(1);
				result.setMsg("Material encontrado");

				entrega.setMaterial(rs.getString("matnr"));
				entrega.setDescripcion(rs.getString("maktx"));
				entrega.setCajas(rs.getString("vemng"));
				entrega.setMe(rs.getString("VEMEH"));
			} else {
				result.setId(2);
				result.setMsg("Material no encontrado en la entrega entrante.");
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
	public EntregaDetalleDTO getDataHU_LQUA(String hu) throws ClassNotFoundException {

		ResultDT result = new ResultDT();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GET_DATA_HU_LQUA);
			stmn.setString(1, hu);
			LOCATION.error("HU: " + hu);

			rs = stmn.executeQuery();
			if (rs.next()) {

				result.setId(1);
				result.setMsg("Material encontrado");

				entrega.setMaterial(rs.getString("matnr"));
				entrega.setDescripcion(rs.getString("maktx"));
				entrega.setCajas(rs.getString("vemng"));
				entrega.setMe(rs.getString("VEMEH"));
			} else {
				result.setId(2);
				result.setMsg("Material no encontrado en la entrega entrante.");
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

		LOCATION.info("getDataHU_LQUA id: "+result.getId());
		return entrega;

	}
	public ResultDT confirmaHusEnCarrill(EntregaInput entregaEntranteInput) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		result.setId(0);
		Connection con = connectionMob.createConnection();
		CallableStatement callableStatement = null;
		try {
			// "@HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGORT, @LGNUM,
			// @LGTYP, @LGPLA, @RESULT
			LOCATION.info("CONSUME HUS");
			callableStatement = con.prepareCall(CONSUME_HUS);
			LOCATION.info(entregaEntranteInput.getHu1() + "-"
					+ entregaEntranteInput.getHu2() + "-"
					+ entregaEntranteInput.getUsuarioMontacarga() + "-"
					+ entregaEntranteInput.getEntrega() + "-"
					+ entregaEntranteInput.getMatnr() + "-"
					+ entregaEntranteInput.getWerks() + "-"
					+ entregaEntranteInput.getLgort() + "-"
					+ entregaEntranteInput.getuDestino0() + "-"
					+ entregaEntranteInput.getuDestino1() + "-"
					+ entregaEntranteInput.getuDestino2() + "-"
					+ entregaEntranteInput.getLfart());
			callableStatement.setString(1, entregaEntranteInput.getHu1());
			callableStatement.setString(2, entregaEntranteInput.getHu2());
			callableStatement.setString(3, entregaEntranteInput.getUsuarioMontacarga());
			callableStatement.setString(4, UtilsMob.zeroFill(entregaEntranteInput.getEntrega(), 10));
			callableStatement.setString(5, entregaEntranteInput.getMatnr());
			callableStatement.setString(6, entregaEntranteInput.getWerks());
			callableStatement.setString(7, entregaEntranteInput.getLgort());
			callableStatement.setString(8, entregaEntranteInput.getuDestino0());
			callableStatement.setString(9, entregaEntranteInput.getuDestino1());
			callableStatement.setString(10, entregaEntranteInput.getuDestino2());
			callableStatement.setString(11, entregaEntranteInput.getLfart());
			callableStatement.registerOutParameter(12, java.sql.Types.INTEGER);
			callableStatement.execute();
			int id = 0;
			LOCATION.info("AFTER EXECUTE: " + id);
			id = callableStatement.getInt(12);
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
		LOCATION.info("confirmaHusEnCarrill id: "+result.getId());
		return result;

	}
	public ResultDT limpiaPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException {
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
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
	public ResultDT validaPickeoPrevioHU(EntregaInput entregaInput, String hu) throws ClassNotFoundException {
		Connection con = connectionMob.createConnection();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet resultado = null;
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
		LOCATION.info("validaPickeoPrevioHU id: "+resultDT.getId());
		return resultDT;
	}
}