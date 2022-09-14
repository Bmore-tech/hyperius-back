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
import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.UtilsMob;

@Repository
public class UbicacionPTDAO {
    
  @Autowired
  private DBConnectionMob connectionMob;

	private static final Logger LOCATION = LoggerFactory.getLogger(UbicacionPTDAO.class);
	static String GET_DATA_HU = "select vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH,VHILM from "
			+ "HCMDB.dbo.MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
			+ "VEKP.venum = VEPO.venum where VEKP.EXIDV=? and VEPO.VELIN ='1'";
	static String CONSULTA_RESERVA_CARRIL_HU = "SELECT LGNUM, LGTYP, LGPLA, STATUS from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
			+ "where VBELN=? and EXIDV=? and idProceso='3'";
	static String RESERVAR_CARRIL_HU1 = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set EXIDV = ?,usuarioMontacarga = ? where VBELN = ? and MATNR = ? and  Status is null and EXIDV is null and idProceso='3'";
	static String RESERVAR_CARRIL_HU2 = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set EXIDV = ?,usuarioMontacarga = ? where VBELN = ? and MATNR = ? and LGTYP = ? and LGPLA = ? and  Status is null and EXIDV is null and idProceso='3'";
	static String GETFALTANTES = "select count(*) from "
			+ "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and Status is null and idProceso='3'";
	static String GET_AUFNR_FROM_HU = "SELECT VPOBJKEY from HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
			+ "on VEPO.VENUM = VEKP.VENUM and VEKP.EXIDV= ? and VEPO.WERKS = ? and VEPO.VELIN='1'";
	static String VALIDA_PICK = "select distinct(MATNR) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and idProceso='3'";
	static String CONSUME_HUS = "exec sp_bcps_wm_consume_hus_ubicacion ?,?,?,?,?,?,?,?,?,?,?";
	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null,EXIDV = null, usuarioMontacarga = null  "
			+ "where VBELN = ? and Status is null and usuarioMontacarga = ? and idProceso='3'";
	static String GET_WERKS="SELECT WERKS FROM VEKP WHERE EXIDV= ?";
	public OrdenProduccionDetalleDTO getDataHU(String hu) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GET_DATA_HU);
			stmn.setString(1, hu);
			LOCATION.info("HU: " + hu);
			rs = stmn.executeQuery();
			if (rs.next()) {
				result.setId(1);
				result.setMsg("Material encontrado");
				orden.setMaterial(rs.getString("matnr"));
				orden.setDescripcion(rs.getString("maktx"));
				orden.setCajas(rs.getString("vemng"));
				orden.setMe(rs.getString("VEMEH"));
				orden.setTarima(rs.getString("VHILM"));
			} else {
				result.setId(2);
				result.setMsg("Material no encontrado en la orden de producción");
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
		LOCATION.info("GetDataHU id: "+ result.getId());
		orden.setResultDT(result);
		return orden;
	}
	public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) throws ClassNotFoundException{
		Connection con = connectionMob.createConnection();
		CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(CONSULTA_RESERVA_CARRIL_HU);
			LOCATION.info("Consulta reserva carril");
			LOCATION.info("hu: "+hu);
			LOCATION.info("Vblen: "+vbeln);
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
				LOCATION.info("status: "+resultDT.getTypeS()+"\n"+
						"LGNUM: "+carrilUbicacionDTO.getLGNUM()+"\n"+
						"LGTYP: "+carrilUbicacionDTO.getLGTYP()+"\n"+
						"LGPLA: "+carrilUbicacionDTO.getLGPLA()+"\n");
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
		LOCATION.info("consultReservaCarrilHu id: "+resultDT.getId());
		carrilUbicacionDTO.setResultDT(resultDT);
		return carrilUbicacionDTO;
	}
	public ResultDT reservarCarrilHU(String entrega, String hu, String matnr,String usuarioMontacarga) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			stmn = con.prepareStatement(RESERVAR_CARRIL_HU1);
			stmn.setString(1, hu);
			stmn.setString(2, usuarioMontacarga);
			stmn.setString(3, entrega);
			stmn.setString(4, matnr);
			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("LUGAR RESERVADO PARA HU");
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
	public ResultDT reservarCarrilHU(String entrega, String hu, String matnr,
			String lgtyp, String lgpla,String usuarioMontacarga) throws ClassNotFoundException{
		LOCATION.info("reservarCarrilHU2");
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			stmn = con.prepareStatement(RESERVAR_CARRIL_HU2);
			stmn.setString(1, hu);
			stmn.setString(2, usuarioMontacarga);
			stmn.setString(3, entrega);
			stmn.setString(4, matnr);
			stmn.setString(5, lgtyp);
			stmn.setString(6, lgpla);
			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("LUGAR RESERVADO PARA HU");
			} else {
				result.setId(0);
				result.setMsg("Continue con solo una HU");
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
	public int getFaltantes(String entry) throws ClassNotFoundException{
		Connection con = connectionMob.createConnection();
		int x = 999999;
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GETFALTANTES);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) >= 0) {
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
	public ResultDT getAUFNRFromHu(String hu, String werks) throws ClassNotFoundException{
		Connection con = connectionMob.createConnection();
		ResultDT resultDT = new ResultDT();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		resultDT.setTypeS("");
		try {
			stmn = con.prepareStatement(GET_AUFNR_FROM_HU);
			stmn.setString(1, hu);
			stmn.setString(2, werks);
			LOCATION.info("Werks daoUbicacionPT: " + werks+" hu: "+hu);
			rs = stmn.executeQuery();
			if (rs.next()) {
				resultDT.setTypeS(rs.getString("VPOBJKEY"));
				resultDT.setId(1);
				resultDT.setMsg("Orden de producción encontrada");
			} else {
				resultDT.setId(0);
				resultDT.setMsg("Orden de producción no encontrada ví­a HU y con centro de montacarguista: "+ werks);
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
		LOCATION.info("getAUFNRFromHu id: "+resultDT.getId());
		return resultDT;
	}
	public OrdenProduccionInput validarOrdenEnPickin(String entry) throws ClassNotFoundException{
		LOCATION.info("entry: "+entry);
		OrdenProduccionInput orden = new OrdenProduccionInput();
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			stmn = con.prepareStatement(VALIDA_PICK);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			int cont = 0;
			while (rs.next()) {
				cont++;
				map.put(rs.getString("MATNR"), "MATNR");
				result.setId(1);
				result.setMsg("Orden de producción ya se encuentra en picking");
			}
			if (cont == 0) {
				result.setId(2);
				result.setMsg("Orden de producción no disponible para picking");
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
		orden.setMateriales(map);
		orden.setResultDT(result);
		LOCATION.info("validarOrdenEnPickin id: "+result.getId());
		return orden;
	}
	public ResultDT confirmaHusEnCarrill(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		result.setId(0);
		Connection con = connectionMob.createConnection();
		CallableStatement callableStatement = null;
		try {
			// @IDPROC, @HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGORT,
			// @LGNUM, @LGTYP, @LGPLA, @FECHA, @HORA, @RESULT
			LOCATION.error("CONSUME HUS");
			callableStatement = con.prepareCall(CONSUME_HUS);
			callableStatement.setString(1, ordenProduccionInput.getHu1());
			callableStatement.setString(2, ordenProduccionInput.getHu2());
			callableStatement.setString(3, ordenProduccionInput.getUsuarioMontacarga());
			callableStatement.setString(4, UtilsMob.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12));
			callableStatement.setString(5, UtilsMob.zeroFill(ordenProduccionInput.getMatnr(), 18));
			callableStatement.setString(6, ordenProduccionInput.getWerks());
			callableStatement.setString(7, "LV01");
			callableStatement.setString(8, ordenProduccionInput.getuDestino0());
			callableStatement.setString(9, ordenProduccionInput.getuDestino1());
			callableStatement.setString(10, ordenProduccionInput.getuDestino2());
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
		LOCATION.info("confirmaHusEnCarrill id: "+result.getId());
		return result;
	}
	public ResultDT limpiaPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		Connection con = connectionMob.createConnection();
		PreparedStatement stmn = null;
		try {
			LOCATION.error("Limpia pendientes DAO :" + vbeln +" usuario:"+user);
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
	public String getWerks(String Hu) throws ClassNotFoundException{
		Connection con= connectionMob.createConnection();
		PreparedStatement stmn=null;
		ResultSet rs=null;
		String werks="";
		LOCATION.info("Antes del try\nHu: "+Hu);
		try {
			stmn=con.prepareStatement(GET_WERKS);
			stmn.setString(1,Hu);
			rs=stmn.executeQuery();
			if(rs.next()) {
				LOCATION.info("Dentro if");
				werks=rs.getString("WERKS");
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
		LOCATION.info("Centro encontrado: "+werks);
		return werks;
	}
}