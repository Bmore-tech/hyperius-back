package com.bmore.hyperius.web.repository.old;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EntregaDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class RecepcionEnvaseRepository {

  @Autowired
  private Utils utils;
  
  @Autowired
  private DBConnection dbConnection;

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	static String ENTRY_EXISTS_LFART = "select DISTINCT(LIKP.VBELN),LFART  from HCMDB.dbo.LIKP LIKP with(nolock)  where LIKP.VBELN = ? ";

	static String ENTRY_LGORTS = "select DISTINCT(LIPS.LGORT)  from HCMDB.dbo.LIPS LIPS with(nolock)  where LIPS.VBELN = ? ";

	static String ENTRY_LGORTS_IM = "SELECT LGORT FROM TB_BCPS_LGORT_VBELN_IM WHERE ID_PROCESO=1";

	static String ENTRY_EXISTS_VBELN_SAP = "select DISTINCT(LIKP.VBELN) from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "inner join HCMDB.dbo.LIPS LIPS with(nolock) on LIKP.VBELN = LIPS.VBELN "
			+ "where (LIKP.LFART = 'EL' or LIKP.LFART = 'YD15' or LIKP.LFART = 'YD06' ) and LIKP.VBELN=? and (LIPS.WERKS=? or LIKP.KUNNR=(select top(1) kunnr from zCentrosBCPS where werks= ?))";

	static String ENTRY_EXISTS_VBELN_BCPS = "select DISTINCT(LIKP.VBELN),LIPS.WERKS from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "inner join HCMDB.dbo.LIPS LIPS with(nolock) on LIKP.VBELN = LIPS.VBELN "
			+ "inner join zContingencia zContingencia with(nolock) on LIKP.VBELN = zContingencia.ENTREGA "
			+ "where LIKP.LFART = 'Y015' and LIKP.VBELN=? and LIKP.KUNNR = (select kunnr from zCentrosBCPS where werks= ?)";

	static String DATOS_PROVEEDOR_SAP = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.LIFNR,LFA1.NAME1,LFA1.NAME2, "
			+ "LFA1.ORT01, LFA1.ORT02,LFA1.PSTLZ,LFA1.STRAS   from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "left outer join  HCMDB.dbo.LFA1 LFA1 on (LIKP.LIFNR= LFA1.LIFNR or LIKP.VSTEL = LFA1.LIFNR ) where VBELN=?";

	static String DATOS_PROOVERDOR_SAP_YD06 = "SELECT VBELN, dbo.conFec(ERDAT) AS ERDAT, LP.KUNNR AS LIFNR, KN.NAME1, KN.NAME2, KN.ORT01, '' AS ORT02, "
			+ "KN.PSTLZ, KN.STRAS  FROM LIKP LP WITH(NOLOCK) LEFT OUTER JOIN KNA1 KN WITH(NOLOCK) ON LP.KUNNR = KN.KUNNR WHERE VBELN = ?";

	static String DATOS_PROVEEDOR_BCPS = " select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.LIFNR,LFA1.NAME1,LFA1.NAME2, "
			+ "LFA1.ORT01, LFA1.ORT02,LFA1.PSTLZ,LFA1.STRAS   from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "left outer join  HCMDB.dbo.LFA1 LFA1 on (select lifnr from zCentrosBCPS where "
			+ "vstel= LIKP.VSTEL)= LFA1.LIFNR  where VBELN= ?";

	static String TOTAL_POS = "select count(POSNR) from HCMDB.dbo.LIPS with(nolock) where VBELN=?";

	static String POS_ENTRY = "select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN=? and "
			+ " PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and convert(decimal, LFIMG) > 0 and LGORT!='TA01' and LGORT!='TA02'";

	static String GET_TOTAL_HUS_POSNR_SAP = "select count(VENUM) from HCMDB.dbo.VEPO WITH(NOLOCK) where  VBELN = ? and MATNR = ?";

	static String GET_TOTAL_HUS_POSNR_BCPS = "select count(venum) from vepo WITH(NOLOCK) where VENUM in "
			+ "(select venum from VEKP WITH(NOLOCK) where EXIDV in (select hu from zContingencia WITH(NOLOCK) where "
			+ "ENTREGA = ? and IDPROC=28 AND HU IS NOT NULL )) and VELIN ='1' and MATNR = ?";

	static String GET_TOTAL_HUS_POSNR_EMZ1_BCPS = "select count(LENUM) from LQUA WITH(NOLOCK) where LENUM in "
			+ "(select hu from zContingencia WITH(NOLOCK) where "
			+ "ENTREGA = ? and IDPROC=8 AND HU IS NOT NULL ) and MATNR = ? and SKZUA is not null";

	static String GET_CAJAS_POSNR = "select sum(convert(decimal(18, 3), LFIMG)),MEINS from HCMDB.dbo.LIPS WITH(NOLOCK) "
			+ "where VBELN = ? and MATNR = ? and PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and convert(decimal, LFIMG) > 0 group by MEINS";

	static String GET_CAJAS_POSNR_ALT = "select sum(convert(decimal(18, 3), LFIMG)),VRKME from HCMDB.dbo.LIPS WITH(NOLOCK) "
			+ "where VBELN = ? and MATNR = ? and PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and convert(decimal, LFIMG) > 0 group by VRKME";

	static String VALIDA_PICK = "select distinct(MATNR) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ?";

	static String VALIDA_HU = "SELECT count(VEKP.EXIDV) from HCMDB.dbo.VEKP VEKP WITH(NOLOCK) INNER JOIN "
			+ "HCMDB.dbo.VEPO VEPO WITH(NOLOCK) on VEKP.VENUM = VEPO.VENUM where VEKP.VPOBJKEY=? and VEKP.EXIDV=?";

	static String GETPOSITION = "select LGNUM, LGTYP, LGPLA from "
			+ "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN =? and EXIDV=?";

	static String CONTABILIZADO = "select * from HCMDB.dbo.zContingencia WITH(NOLOCK) where (IDPROC = '4' or IDPROC='14' or IDPROC='32' ) and ENTREGA = ?";

	static String GETFALTANTES = "select count(*) from "
			+ "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and Status is null";

	static String DETAIL_ENTRY = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante "
			+ "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,?,?,?)";

	static String ENCONTABILIZAR = "insert into LQUA() select * from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)";

	static String VALIDAZPICK = "SELECT count(EXIDV) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ " where EXIDV=? and VBELN = ?";

	static String CONSUMIRZHU2 = "UPDATE HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set Status= null, EXIVD = null where VBELN = ? and  Status = 'X' and EXIVD  = ?" + " and  LGPLA= ?";

	static String INSERTCONTABILIZA = "insert into HCMDB.dbo.LQUA(LGNUM, LGTYP, LGPLA, WERKS, LENUM)  "
			+ "select LGNUM, LGTYP, LGPLA, WERKS = ?, EXIDV  from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ?";

	static String UPDATECONTABILIZA = "update HCMDB.dbo.ZPickingEntregaEntrante set Contabilizado = 'X' where VBELN = ?";

	static String ENTRYCONTABILZA = "SELECT VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ?";

	static String GET_CARRILES_BLOQUEADOS = "select distinct(CARRIL) "
			+ "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";

	static String GET_CARRILES = "EXEC SP_BCPS_GET_CARRILES_POR_MATERIAL ?,?,?,?,?";

	static String GET_CARRILES_VACIOS = "EXEC SP_GET_CARRILES_VACIOS ?,?,?,?,?";

	static String GET_CARRILES_PROPUESTOS = "SELECT distinct LGNUM, LGTYP, LGPLA FROM ZPickingEntregaEntrante with(nolock) where VBELN = ? and werks = ? and idProceso = ? and matnr = ? and (Status  is null or Status ='1')";

	static String GET_CANTIDADES_MIXTOS_VACIOS_LLENOS_MATERIAL = "SELECT top (1) MEINS, VERME "
			+ "from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) where WERKS = ? and MATNR=? and SKZUA is null and " + "LGNUM = ? "
			+ "and LGTYP = ? and LGPLA = ? " + "group by MEINS, VERME";

	static String GET_VBELN_FROM_HU_SAP = "SELECT VEPO.VBELN, LIKP.LFART from HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
			+ "on VEPO.VENUM = VEKP.VENUM and VEKP.EXIDV=? and VEPO.VELIN='1' "
			+ "inner join LIKP LIKP on VEPO.VBELN= LIKP.VBELN";

	static String GET_VBELN_FROM_HU_BCPS = "select ENTREGA as VBELN from zContingencia zCon WITH(NOLOCK) "
			+ " inner join HCMDB.dbo.LIKP LIKP WITH(NOLOCK) "
			+ "on zCon.ENTREGA = LIKP.VBELN and LIKP.KUNNR = (select KUNNR from zCentrosBCPS WITH(NOLOCK)"
			+ "where werks = ? ) and zCon.HU= ? and (zCon.IDPROC=28 or zCon.IDPROC=8)";

	static String VALIDAR_ENTREGA_EN_PICKING = "SELECT VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBLEN=?";

	static String RESERVAR_CARRIL_HU = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set EXIDV = ?, usuarioMontacarga = ? where VBELN = ? and MATNR = ?  and  Status is null and EXIDV is null and idProceso=1";

	static String CONSULTA_RESERVA_CARRIL_HU = "SELECT LGNUM, LGTYP, LGPLA, STATUS from "
			+ " HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN=? and EXIDV=? and idProceso=1 ";

	static String CONFIRMA_PICKING_HU = "update HCMDB.dbo.ZPickingEntregaEntrante set Status = 'X' where VBELN = ? and EXIDV=?";

	static String ROLL_BACK_CONFIRMA_PICKING_HU = "update HCMDB.dbo.ZPickingEntregaEntrante set Status = null where VBELN = ? and EXIDV=?";

	static String COMPARAR_UBICACIONES_HUS = "select LGNUM,LGTYP,LGPLA from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where EXIDV in(?,?)";

	static String GET_DATA_HU = "select vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH from "
			+ "HCMDB.dbo.MAKT MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)"
			+ " on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
			+ "VEKP.venum = VEPO.venum where VEKP.EXIDV=? and VEPO.VELIN ='1'";

	static String GET_DATA_HU_LQUA = "SELECT LQUA.MATNR AS MATNR, MAKT.MAKTX AS MAKTX, LQUA.VERME AS VEMNG, MEINS AS VEMEH FROM LQUA LQUA "
			+ "INNER JOIN MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR " + "WHERE LENUM = ?";

	static String SUMA_ZPICKING_ENTREGA_ENTRANTE_PENDIENTES = " SELECT count(MATNR) as cantidad, LGNUM FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ " where vbeln =? and matnr=? and status is not null and EXIDV is not null and idProceso='1' group by LGNUM";

	static String SUMA_ZPICKING_ENTREGA_ENTRANTE_TOTAL = "SELECT count(matnr) as cantidad,LGNUM FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln = ? and matnr = ? and idProceso = '1' group by LGNUM";

	static String PENDIENTES_POR_PICKEAR_POR_CARRIL = "select count(*) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='1' and Status is null";

	static String INSERT_PROCESO_ZCONTINGENCIA_3_13 = "insert into HCMDB.dbo.zContingencia(IDPROC,FECHA,HORA,CENTRO,HU,ENTREGA,CONTROL_CALIDAD,ALMACEN,USUARIO,TIPO_ALMACEN,DESTINO) "
			+ "select IDPROC= ? , convert(date,getdate()), convert(time, getdate()), WERKS = ?, HU=?,ENTREGA=?,CONTROL_CALIDAD=(select DISTINCT(Clase_Inspeccion) from HCMDB.dbo.MaterialQUA as QA WITH(NOLOCK) where Centro= ? and Material=?),ALMACEN= ? , USUARIO= ?,TIPO_ALMACEN=?,DESTINO=?";

	// static String INSERT_PROCESO_ZCONTINGENCIA_4_14 =
	// " insert into HCMDB.dbo.zContingencia(ENTREGA,IDPROC, FECHA, HORA, CENTRO,
	// USUARIO) "
	// +
	// "select DISTINCT(VBELN ), IDPROC= ? , convert(date,getdate()), convert(time,
	// getdate()), WERKS = ?, "
	// +
	// "usuarioSupervisor from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
	// + "where Status = 'X' and VBELN= ? and idProceso='1'";

	static String INSERT_PROCESO_ZCONTINGENCIA_4_14_32 = "insert into HCMDB.dbo.zContingencia(ENTREGA,IDPROC, FECHA, HORA, CENTRO, USUARIO) "
			+ "values(?,?,convert(date,getdate()), convert(time, getdate()),?,? )";

	static String UPDATE_LQUA = "insert into HCMDB.dbo.LQUA (LGNUM,MATNR,WERKS,BESTQ,LGTYP,LGPLA,MEINS,GESME,VERME,LENUM,LGORT,SKZUE,SKZUA,LETYP) VALUES "
			+ "(?,?,?,(select DISTINCT(Clase_Inspeccion) from HCMDB.dbo.MaterialQUA as QA WITH(NOLOCK) where Centro= ? and Material=?),?,?,?,?,?,?,?,NULL,NULL,"
			+ "(select top (1) LETYP from ZPAITT_TTW WITH(NOLOCK) where WERKS= ? and MATNR=(select top(1) VHILM FROM VEKP WITH(NOLOCK) where EXIDV= ?)))";

	static String VALIDA_PICK_FINALIZADO = "select VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and WERKS = ? and Status is null";

	@SuppressWarnings("resource")
	public EntregaDTO getEntrega(EntregaDTO entregaInput) {

		EntregaDTO entrega = new EntregaDTO();
		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		PreparedStatement stmn4 = null;
		ResultSet rs = null;
		ResultSet rs4 = null;

		try {

			stmn = con.prepareStatement(ENTRY_EXISTS_LFART);
			stmn.setString(1, entregaInput.getEntrega());

			rs = stmn.executeQuery();
			if (rs.next()) {

				if (rs.getString("LFART").equals("EL") || rs.getString("LFART").equals("YD15")
						|| rs.getString("LFART").equals("YD06")) {

					stmn = con.prepareStatement(ENTRY_EXISTS_VBELN_SAP);

					entregaInput.setLfart(rs.getString("LFART"));
					entrega.setLfart(rs.getString("LFART"));
					stmn.setString(3, entregaInput.getWerks());

				} else if (rs.getString("LFART").equals("Y015")) {
					stmn = con.prepareStatement(ENTRY_EXISTS_VBELN_BCPS);
					entregaInput.setLfart("Y015");
					entrega.setLfart("Y015");
				}

				stmn.setString(1, entregaInput.getEntrega());
				stmn.setString(2, entregaInput.getWerks());

				LOCATION.error("ENTREGA TIPO:  " + entregaInput.toString());

				rs = stmn.executeQuery();
				if (rs.next()) {

					if (entregaInput.getLfart().equals("EL") || entregaInput.getLfart().equals("YD15")) {

						stmn4 = con.prepareStatement(DATOS_PROVEEDOR_SAP);

					} else if (entregaInput.getLfart().equals("Y015")) {

						stmn4 = con.prepareStatement(DATOS_PROVEEDOR_BCPS);
						entrega.setWerksBCPS(rs.getString("WERKS"));
					} else if (entregaInput.getLfart().equals("YD06")) {

						stmn4 = con.prepareStatement(DATOS_PROOVERDOR_SAP_YD06);
					}

					stmn4.setString(1, entregaInput.getEntrega());

					rs4 = stmn4.executeQuery();

					if (rs4.next()) {

						entrega.setEntrega(rs4.getString("VBELN"));
						entrega.setProveedor(rs4.getString("LIFNR"));
						entrega.setFechaDocumento(rs4.getString("ERDAT"));

						String name1 = rs4.getString("NAME1");
						String name2 = rs4.getString("NAME2");
						String ort01 = rs4.getString("ORT01");
						String ort02 = rs4.getString("ORT02");
						String pstlz = rs4.getString("PSTLZ");
						String stras = rs4.getString("STRAS");

						entrega.setProveedorDesc(
								name1 + " " + name2 + ", " + ort01 + ", " + ort02 + ", " + pstlz + ", " + stras);

						result.setId(1);
						result.setMsg("Recuperacion de Datos de Cabecera Correcta");

					} else {
						result.setId(5);
						result.setMsg("La entrega no tiene datos de cabecera para mostrar");
					}
				} else {

					result.setId(2);
					result.setMsg("ENTREGA NO EXISTE");

				}

			} else {

				result.setId(2);
				result.setMsg("ENTREGA NO EXISTE");

			}

		} catch (SQLException e) {

			result.setId(2);
			result.setMsg(e.getMessage());

		} catch (Exception en) {

			result.setId(2);
			result.setMsg(en.getMessage());

		} finally {
			try {
				stmn.close();
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		entrega.setResultDT(result);
		return entrega;
	}

	public HashMap<String, String> getLgortsEntrega(String entrega) {

		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("resultDT.id", "3");
		map.put("resultDT.msg", "No fue posible recuperar el  campo LGORT de la entrega (LIPS)");

		try {

			stmn = con.prepareStatement(ENTRY_LGORTS);

			stmn.setString(1, entrega);

			rs = stmn.executeQuery();

			while (rs.next()) {

				map.put(rs.getString("LGORT"), rs.getString("LGORT"));
				map.put("resultDT.id", "1");
				map.put("resultDT.msg", "LGORT de la entrega (LIPS) recuperado con exitosamente");

			}

		} catch (SQLException e) {

			map.put("resultDT.id", "2");
			map.put("resultDT.msg", "Error en getLgortsEntrega: SQLException -> " + e.toString());

			result.setId(2);
			result.setMsg(e.getMessage());

		} catch (Exception en) {

			map.put("resultDT.id", "2");
			map.put("resultDT.msg", "Error en getLgortsEntrega: Exception -> " + en.toString());

			result.setId(2);
			result.setMsg(en.getMessage());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());

				map.put("resultDT.id", "2");
				map.put("resultDT.msg", "Error en getLgortsEntrega: Cerrar conexión Exception -> " + e.toString());
			}
		}

		return map;
	}

	public HashMap<String, String> getLgortsTabla() {

		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("resultDT.id", "3");
		map.put("resultDT.msg", "No fue posible recuperar los valores LGORT de la tabla (TB_BCPS_LGORT_VBELN_IM)");

		try {

			stmn = con.prepareStatement(ENTRY_LGORTS_IM);

			rs = stmn.executeQuery();

			while (rs.next()) {

				map.put(rs.getString("LGORT"), rs.getString("LGORT"));
				map.put("resultDT.id", "1");
				map.put("resultDT.msg", "LGORTs de las tabla (TB_BCPS_LGORT_VBELN_IM)");

			}

		} catch (SQLException e) {

			map.put("resultDT.id", "2");
			map.put("resultDT.msg", "Error en getLgortsTabla: SQLException -> " + e.toString());

			result.setId(2);
			result.setMsg(e.getMessage());

		} catch (Exception en) {

			map.put("resultDT.id", "2");
			map.put("resultDT.msg", "Error en getLgortsTabla: Exception -> " + en.toString());

			result.setId(2);
			result.setMsg(en.getMessage());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());

				map.put("resultDT.id", "2");
				map.put("resultDT.msg", "Error en getLgortsTabla: Cerrar conexión Exception -> " + e.toString());
			}
		}

		return map;
	}

	public EntregaDTO getEntregaDetalle(EntregaDTO entrega) {

		ResultDTO result = new ResultDTO();
		EntregaDetalleDTOItem entregaDetalleDTOItem = new EntregaDetalleDTOItem();

		List<EntregaDetalleDTO> items = new ArrayList<EntregaDetalleDTO>();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		PreparedStatement stmn2 = null;
		PreparedStatement stmn3 = null;
		PreparedStatement stmn4 = null;
		PreparedStatement stmn5 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;

		try {
			stmn = con.prepareStatement(TOTAL_POS);
			stmn.setString(1, entrega.getEntrega());
			rs = stmn.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) >= 1) {

					stmn2 = con.prepareStatement(POS_ENTRY);
					stmn2.setString(1, entrega.getEntrega());
					rs2 = stmn2.executeQuery();
					HashMap<String, String> map = new HashMap<String, String>();

					while (rs2.next()) {

						if (map.get(rs2.getString("MATNR")) == null) {

							LOCATION.error("MATNR: " + rs2.getString("MATNR"));

							map.put(rs2.getString("MATNR"), rs2.getString("MATNR"));

							EntregaDetalleDTO item = new EntregaDetalleDTO();

							item.setMaterial(rs2.getString("MATNR"));
							item.setPosicion(rs2.getString("POSNR"));
							item.setDescripcion(rs2.getString("ARKTX"));

							if (entrega.getLfart().equals("EL") || entrega.getLfart().equals("YD15")
									|| entrega.getLfart().equals("YD06")) {
								stmn3 = con.prepareStatement(GET_TOTAL_HUS_POSNR_SAP);
								LOCATION.error("TODO 1.0");
							} else if (entrega.getLfart().equals("Y015")) {

								if (entrega.getWerksBCPS() != null && (entrega.getWerksBCPS().equals("EMZ1"))
										|| (entrega.getWerksBCPS().equals("TMZ1"))
										|| (entrega.getWerksBCPS().toUpperCase().startsWith("PC"))

								) {

									stmn3 = con.prepareStatement(GET_TOTAL_HUS_POSNR_EMZ1_BCPS);
									LOCATION.error("envases 1.5 HUS");

								} else {
									LOCATION.error("VIDRIO 1.5 HUS");
									stmn3 = con.prepareStatement(GET_TOTAL_HUS_POSNR_BCPS);
								}

							}
							stmn3.setString(1, entrega.getEntrega());
							stmn3.setString(2, item.getMaterial());

							LOCATION.error("hus MATNR: ");
							rs3 = stmn3.executeQuery();

							if (rs3.next()) {
								item.setHus(rs3.getString(1));
								LOCATION.error("cantidad: " + item.getHus());
								item.setEmbalar("false");
							} else {
								item.setHus("NO EXISTE");
								item.setEmbalar("true");
								LOCATION.error("no hay");
							}

							if (entrega.getLfart().equals("EL")) {
								stmn4 = con.prepareStatement(GET_CAJAS_POSNR_ALT);
							} else {

								stmn4 = con.prepareStatement(GET_CAJAS_POSNR);
							}
							stmn4.setString(1, entrega.getEntrega());
							stmn4.setString(2, item.getMaterial());
							LOCATION.error("cajas MATNR");
							rs4 = stmn4.executeQuery();

							if (rs4.next()) {
								item.setCajas(rs4.getString(1));
								item.setCajas(new BigDecimal(rs4.getString(1)).setScale(3, RoundingMode.HALF_UP) + "");
								item.setMe(rs4.getString(2));
							} else {
								item.setCajas("NO EXISTE");
							}

							LOCATION.error("Buscando HUs asignadas");

							item.setHusPendientes("0");

							// Agregar LGNUM N/A = aun no se ha elegido un LGNUM
							// para esta entrega, unicamente es uno por entrega
							// indepentientemente de las posiciones que tenga
							item.setLgnum("N/A");

							try {

								stmn5 = con.prepareStatement(SUMA_ZPICKING_ENTREGA_ENTRANTE_PENDIENTES);

								stmn5.setString(1, entrega.getEntrega());
								stmn5.setString(2, item.getMaterial());

								rs5 = stmn5.executeQuery();

								if (rs5.next()) {
									String cantidad = rs5.getString("cantidad");
									item.setHusPendientes(cantidad);
									item.setLgnum(rs5.getString("LGNUM"));
								}

							} catch (Exception e) {
								LOCATION.error("Error en SUMA_ZPICKING_ENTREGA_ENTRANTE_PENDIENTES: " + e);
							}

							LOCATION.error("Buscando HUs asignadas");
							item.setHusAsignadas("0");

							// tambien se toma en cuenta el lgnum de las hus en
							// proceso de picking, "sustituye" (siempre es el
							// mismo)
							// al que ya fue pickeado, en caso de que ninguna hu
							// este confirmada se arrastro de las que ya
							// fueron
							// confirmadas o bien N/A si no hay confirmadas

							try {

								stmn5 = con.prepareStatement(SUMA_ZPICKING_ENTREGA_ENTRANTE_TOTAL);

								stmn5.setString(1, entrega.getEntrega());
								stmn5.setString(2, item.getMaterial());

								rs5 = stmn5.executeQuery();

								if (rs5.next()) {
									String cantidad = rs5.getString("cantidad");
									item.setHusAsignadas(cantidad);
									item.setLgnum(rs5.getString("LGNUM"));

								}

							} catch (Exception e) {
								LOCATION.error("Error en SUMA_ZPICKING_ENTREGA_ENTRANTE_TOTAL: " + e);
							}

							items.add(item);
						}
					}

					result.setId(1);
					result.setMsg("Detalle de entrega encontrado.");

				} else {
					result.setId(2);
					result.setMsg("Detalle de entrega NO encontrado.");
				}
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		entregaDetalleDTOItem.setItem(items);
		entrega.setItems(entregaDetalleDTOItem);
		entrega.setResultDT(result);

		return entrega;
	}

	public EntregaDTO getEntregaDetalleSoloCabecera(String vbeln) {

		ResultDTO result = new ResultDTO();
		EntregaDTO entregaDTO = new EntregaDTO();
		EntregaDetalleDTOItem entregaDetalleDTOItem = new EntregaDetalleDTOItem();

		List<EntregaDetalleDTO> items = new ArrayList<EntregaDetalleDTO>();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;

		try {

			stmn = con.prepareStatement(POS_ENTRY);
			stmn.setString(1, vbeln);
			rs = stmn.executeQuery();
			HashMap<String, String> map = new HashMap<String, String>();

			result.setId(2);
			result.setMsg("Detalle de entrega NO encontrado.");

			while (rs.next()) {

				result.setId(1);
				result.setMsg("Detalle de entrega encontrado.");

				if (map.get(rs.getString("MATNR")) == null) {

					map.put(rs.getString("MATNR"), rs.getString("MATNR"));

					EntregaDetalleDTO item = new EntregaDetalleDTO();

					item.setMaterial(rs.getString("MATNR"));
					item.setPosicion(rs.getString("POSNR"));
					item.setDescripcion(rs.getString("ARKTX"));

					items.add(item);
				}
			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		entregaDetalleDTOItem.setItem(items);
		entregaDTO.setItems(entregaDetalleDTOItem);
		entregaDTO.setResultDT(result);

		return entregaDTO;
	}

	public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {

		HashMap<String, String> map = new HashMap<String, String>();
		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(GET_CARRILES_BLOQUEADOS);
			stmn.setString(1, idProceso);
			stmn.setString(2, werks);
			rs = stmn.executeQuery();

			while (rs.next()) {

				map.put(rs.getString("CARRIL").trim(), "");

			}

		} catch (SQLException e) {
			LOCATION.error("SQLException:" + e.toString());
			map = null;
		} catch (Exception en) {
			LOCATION.error("Exception:" + en.toString());
			map = null;
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception:" + e.toString());

			}
		}

		return map;
	}

	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z, String LGORT,
			String VBELN, HashMap<String, String> carrilesBloqueados) {

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();
		Connection con = dbConnection.createConnection();

		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		PreparedStatement stmnt3 = null;
		ResultSet rs3 = null;

		PreparedStatement stmnt4 = null;
		ResultSet rs4 = null;

		PreparedStatement stmnt5 = null;
		ResultSet rs5 = null;

		try {

			HashMap<String, String> hashMap = new HashMap<String, String>();

			stmnt2 = con.prepareStatement(GET_CARRILES);
			stmnt2.setString(1, MATNR);
			stmnt2.setString(2, WERKS);
			stmnt2.setString(3, ID_PR);
			stmnt2.setString(4, ID_PR_Z);
			stmnt2.setInt(5, 1);

			rs2 = stmnt2.executeQuery();

			if (rs2 != null) {

				while (rs2.next()) {

					if (carrilesBloqueados.get(
							rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {

						CarrilUbicacionDTO item = new CarrilUbicacionDTO();
						item.setLgnum(rs2.getString("LGNUM"));
						item.setLgtyp(rs2.getString("LGTYP"));
						item.setLgpla(rs2.getString("LGPLA"));

						item.setCantidadHus(rs2.getString("TOTAL"));
						item.setCajas(rs2.getString("VERME"));
						item.setMe(rs2.getString("MEINS"));
						item.setMaxle(rs2.getString("MAXLE"));

						//
						hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
						carrilList.add(item);
					}
				}
			}
			rs2.close();
			stmnt2.close();

			stmnt3 = con.prepareStatement(GET_CARRILES_VACIOS);
			stmnt3.setString(1, MATNR);
			stmnt3.setString(2, LGORT);
			stmnt3.setString(3, WERKS);
			stmnt3.setString(4, ID_PR);
			stmnt3.setString(5, ID_PR_Z);

			rs3 = stmnt3.executeQuery();

			while (rs3.next()) {

				if (carrilesBloqueados
						.get(rs3.getString("LGNUM") + rs3.getString("LGTYP") + rs3.getString("LGPLA").trim()) == null) {

					CarrilUbicacionDTO item = new CarrilUbicacionDTO();
					item.setLgnum(rs3.getString("LGNUM"));
					item.setLgtyp(rs3.getString("LGTYP"));
					item.setLgpla(rs3.getString("LGPLA"));
					item.setCantidadHus("0");

					hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
					carrilList.add(item);
				}

			}

			rs3.close();
			stmnt3.close();

			stmnt4 = con.prepareStatement(GET_CARRILES_PROPUESTOS);
			stmnt4.setString(1, VBELN);
			stmnt4.setString(2, WERKS);
			stmnt4.setString(3, ID_PR_Z);
			stmnt4.setString(4, MATNR);

			rs4 = stmnt4.executeQuery();

			while (rs4.next()) {
				if (carrilesBloqueados
						.get(rs4.getString("LGNUM") + rs4.getString("LGTYP") + rs4.getString("LGPLA").trim()) != null) {

					CarrilUbicacionDTO item = new CarrilUbicacionDTO();
					item.setLgnum(rs4.getString("LGNUM"));
					item.setLgtyp(rs4.getString("LGTYP"));
					item.setLgpla(rs4.getString("LGPLA"));
					item.setCantidadHus("-1");

					if (hashMap.get(item.getLgnum() + item.getLgtyp() + item.getLgpla()) == null)
						carrilList.add(0, item);
				}

			}

			rs4.close();
			stmnt4.close();

			for (int x = 0; x < carrilList.size(); x++) {
				CarrilUbicacionDTO item = carrilList.get(x);

				if (Integer.parseInt(item.getCantidadHus().trim()) != 0) {

					if (Integer.parseInt(item.getCantidadHus().trim()) == -1) {
						item.setCantidadHus("0");
					}

					try {

						stmnt5 = con.prepareStatement(PENDIENTES_POR_PICKEAR_POR_CARRIL);

						stmnt5.setString(1, item.getLgnum());
						stmnt5.setString(2, item.getLgtyp());
						stmnt5.setString(3, item.getLgpla());

						rs5 = stmnt5.executeQuery();

						if (rs5.next()) {
							int cantidad = rs5.getInt(1);
							item.setHusPendientes(cantidad + "");
						}
						rs5.close();

					} catch (Exception e) {
						LOCATION.error("Error: PENDIENTES_POR_PICKEAR_POR_CARRIL " + e.toString());
					}

				}
			}

			carrilesDTO.setItem(carrilList);

		} catch (SQLException e) {
			LOCATION.error("Error SQL:" + e.toString());
		} catch (Exception en) {
			LOCATION.error("Error SQL:" + en.toString());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error SQL:" + e.toString());
				e.getMessage();
			}
		}
		return carrilesDTO;

	}

	public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {

		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();

		PreparedStatement stmntX = null;
		try {

			for (int x = 0; x < carrilesDTO.getItem().size(); x++) {

				BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());

				int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

				for (int y = 0; y < ingresarZpicking; y++) {

					stmntX = con.prepareStatement(DETAIL_ENTRY);
					stmntX.setString(1, VBELN);
					stmntX.setString(2, carrilesDTO.getItem().get(x).getLgnum());
					stmntX.setString(3, carrilesDTO.getItem().get(x).getLgtyp());
					stmntX.setString(4, carrilesDTO.getItem().get(x).getLgpla());
					stmntX.setString(5, Utils.zeroFill(carrilesDTO.getItem().get(x).getMaterial(), 18));
					stmntX.setString(6, user);
					stmntX.setString(7, "1");
					stmntX.setString(8, werks);

					stmntX.setString(9, carrilesDTO.getItem().get(x).getLgnum()
							+ carrilesDTO.getItem().get(x).getLgtyp() + carrilesDTO.getItem().get(x).getLgpla());

					stmntX.executeUpdate();
				}
			}
			resultDT.setId(1);
			resultDT.setMsg("Se registro la entrega entrante correctamete, mandar a montacarga");

		} catch (SQLException e) {

			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return resultDT;
	}

	public EntregaInputDTO validarEntregaPickin(String entry) {

		EntregaInputDTO entrega = new EntregaInputDTO();
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
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
				result.setMsg("ENTREGA YA SE ENCUENTRA EN PICKING");

			}

			if (cont == 0) {
				result.setId(2);
				result.setMsg("ENTREGA NO DISPONIBLE PARA PICKING");
			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		entrega.setMateriales(map);
		entrega.setResultDT(result);
		return entrega;
	}

	public ResultDTO validarHU_(String entry, String HU) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(VALIDA_HU);
			stmn.setString(1, entry);
			stmn.setString(2, HU);
			rs = stmn.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					result.setId(1);
					result.setMsg("HU EXISTENTE");
				} else {
					result.setId(2);
					result.setMsg("HU NO EXISTENTE");
				}
			} else {
				result.setId(2);
				result.setMsg("HU NO EXISTENTE");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;
	}

	public ResultDTO VRPTVALUE(String entry, String HU) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(VALIDAZPICK);
			stmn.setString(1, HU);
			stmn.setString(2, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					result.setId(2);
					result.setMsg("HU YA FUE INGRESADA");
				} else {
					result.setId(1);
					result.setMsg("HU LIBRE");
				}
			} else {
				result.setId(3);
				result.setMsg("HU NO ENCONTRADA");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public ResultDTO borrarZHU(String entry, String HU) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		try {
			stmn = con.prepareStatement(CONSUMIRZHU2);
			stmn.setString(1, entry);
			stmn.setString(2, HU);
			if (stmn.executeUpdate() > 0) {
				result.setId(2);
				result.setMsg("RollBack Generado");
			} else {
				result.setId(5);
				result.setMsg("Error RollBack Generado");
			}
		} catch (SQLException e) {
			result.setId(7);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(8);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(9);
				result.setMsg(e.getMessage());
			}
		}
		return result;
	}

	public EntregaInputDTO getPositions(String entry, String HU) {
		ResultDTO result = new ResultDTO();
		EntregaInputDTO entrega = new EntregaInputDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GETPOSITION);
			stmn.setString(1, entry);
			stmn.setString(2, HU);
			rs = stmn.executeQuery();
			if (rs.next()) {
				result.setId(1);
				entrega.setuDestino1(rs.getString("LGTYP"));
				entrega.setuDestino2(rs.getString("LGPLA"));
			} else {
				result.setId(2);
				result.setMsg("Error De Comunicacion");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		entrega.setResultDT(result);
		return entrega;

	}

	public ResultDTO entryContabilizada(String entry) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(ENCONTABILIZAR);
			stmn.setString(1, entry);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("Los Datos se Actualizaron Correctamente");
			} else {
				result.setId(2);
				result.setMsg("No Se Acutalizaron Datos");
			}
		} catch (SQLException e) {
			if (e.getMessage().indexOf("LQUA") == -1) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;
	}

	public int getFaltantes(String entry) {
		Connection con = dbConnection.createConnection();
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
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				x = 999999;
			}
		}

		return x;
	}

	public ResultDTO contabilizadoOK(String entry) {
		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(CONTABILIZADO);
			stmn.setString(1, entry);
			rs = stmn.executeQuery();
			if (rs.next()) {
				resultDT.setId(1);
				resultDT.setMsg("Entrega contabilizada");
			} else {
				resultDT.setId(0);
				resultDT.setMsg("Entrega aun no contabilizada");
			}

		} catch (SQLException e) {
			resultDT.setId(0);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(0);
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		return resultDT;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Manejo Picking HH

	public ResultDTO getVBELNFromHuSAP(String hu, String werks) {

		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		resultDT.setTypeS("");

		try {
			stmn = con.prepareStatement(GET_VBELN_FROM_HU_SAP);

			stmn.setString(1, hu);
			// stmn.setString(2, werks);

			rs = stmn.executeQuery();
			if (rs.next()) {

				resultDT.setTypeS(rs.getString("VBELN"));
				resultDT.setMsg(rs.getString("LFART"));
				resultDT.setId(1);

			} else {
				resultDT.setId(0);
				resultDT.setMsg("Entrega no encontrada vía HU y con centro de montacarguista: " + werks);
			}

		} catch (SQLException e) {
			resultDT.setId(0);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(0);
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		return resultDT;
	}

	public ResultDTO getVBELNFromHuBCPS(String hu, String werks) {

		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
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
				resultDT.setId(0);
				resultDT.setMsg("Entrega no encontrada vía HU y con centro de montacarguista: " + werks);
			}

		} catch (SQLException e) {
			resultDT.setId(0);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(0);
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		return resultDT;
	}

	public ResultDTO validarEntregaEnPicking(String VBELN) {

		Connection con = dbConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(VALIDAR_ENTREGA_EN_PICKING);

			stmn.setString(1, VBELN);

			rs = stmn.executeQuery();
			if (rs.next()) {

				resultDT.setId(1);
				resultDT.setMsg("Entrega en Picking");

			} else {
				resultDT.setId(0);
				resultDT.setMsg("La entrega no está en picking");
			}

		} catch (SQLException e) {
			resultDT.setId(0);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(0);
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		return resultDT;
	}

	public ResultDTO reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(RESERVAR_CARRIL_HU);

			stmn.setString(1, hu);
			stmn.setString(2, usuarioMontacargas);
			stmn.setString(3, entrega);
			stmn.setString(4, matnr);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("LUGAR RESERVADO PARA HU");
			} else {
				result.setId(0);
				result.setMsg("NO FUE POSIBLE RESERVAR HU");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) {

		Connection con = dbConnection.createConnection();

		CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(CONSULTA_RESERVA_CARRIL_HU);

			stmn.setString(1, vbeln);
			stmn.setString(2, hu);

			rs = stmn.executeQuery();
			if (rs.next()) {

				carrilUbicacionDTO.setLgnum(rs.getString("LGNUM"));
				carrilUbicacionDTO.setLgtyp(rs.getString("LGTYP"));
				carrilUbicacionDTO.setLgpla(rs.getString("LGPLA"));

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
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
			}
		}

		carrilUbicacionDTO.setResultDT(resultDT);

		return carrilUbicacionDTO;
	}

	public EntregaDetalleDTO getDataHU(String hu) {

		ResultDTO result = new ResultDTO();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(GET_DATA_HU);
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
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		entrega.setResultDT(result);

		return entrega;

	}

	public EntregaDetalleDTO getDataHU_LQUA(String hu) {

		ResultDTO result = new ResultDTO();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();
		Connection con = dbConnection.createConnection();
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
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		entrega.setResultDT(result);

		return entrega;

	}

	public ResultDTO confirmaPickingHU(String VBELN, String hu) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(CONFIRMA_PICKING_HU);
			stmn.setString(1, VBELN);
			stmn.setString(2, hu);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("HU CONFIRMADA EN UBICACION");
			} else {
				result.setId(0);
				result.setMsg("NO FUE POSIBLE CONFIRMAR HU EN UBICACION");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public CarrilesUbicacionDTO compararUbicacionesHUs(String hu1, String hu2) {

		CarrilesUbicacionDTO carriles = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> listaCarriles = new ArrayList<CarrilUbicacionDTO>();
		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(COMPARAR_UBICACIONES_HUS);
			stmn.setString(1, hu1);
			stmn.setString(2, hu2);

			rs = stmn.executeQuery();

			while (rs.next()) {

				CarrilUbicacionDTO carril = new CarrilUbicacionDTO();

				carril.setLgnum(rs.getString("LGNUM"));
				carril.setLgpla(rs.getString("LGPLA"));
				carril.setLgtyp(rs.getString("LGTYP"));

				listaCarriles.add(carril);

			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		carriles.setItem(listaCarriles);

		return carriles;

	}

	public ResultDTO rollBackPickingHU(String VBELN, String hu) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(ROLL_BACK_CONFIRMA_PICKING_HU);
			stmn.setString(1, VBELN);
			stmn.setString(2, hu);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("RollBack a HU con exito");
			} else {
				result.setId(0);
				result.setMsg("NO FUE POSIBLE REALIZAR ROLL BACK A HU");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public ResultDTO insertProcesoContingencia_3(EntregaInputDTO entrega, String hu) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			stmn = con.prepareStatement(INSERT_PROCESO_ZCONTINGENCIA_3_13);

			stmn.setString(1, entrega.getLfart());
			stmn.setString(2, entrega.getWerks());
			stmn.setString(3, hu);
			stmn.setString(4, entrega.getEntrega());
			stmn.setString(5, entrega.getWerks());
			stmn.setString(6, entrega.getMatnr());
			stmn.setString(7, entrega.getLgort());
			stmn.setString(8, entrega.getUsuarioMontacarga());
			stmn.setString(9, entrega.getuDestino1());
			stmn.setString(10, entrega.getuDestino2());

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("3to paso ejecutado con exito");
			} else {
				result.setId(0);
				result.setMsg("3to paso ejecutado con error");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public ResultDTO aumentaInventario(EntregaInputDTO entrega, String hu) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			LOCATION.error("Se consume inventario: ");

			stmn = con.prepareStatement(UPDATE_LQUA);
			stmn.setString(1, entrega.getuDestino0());// LGNUM
			stmn.setString(2, entrega.getMatnr());// MATNR
			stmn.setString(3, entrega.getWerks());// WERKS
			stmn.setString(4, entrega.getWerks());// BESTQ
			stmn.setString(5, entrega.getMatnr());// BESTQ
			stmn.setString(6, entrega.getuDestino1());// LGTYP
			stmn.setString(7, entrega.getuDestino2()); // LGPLA
			stmn.setString(8, entrega.getCantT());// MEINS
			stmn.setString(9, entrega.getCant());// GESME
			stmn.setString(10, entrega.getCant());// VERME
			stmn.setString(11, hu);// LENUM
			stmn.setString(12, entrega.getLgort());// LGORT
			stmn.setString(13, entrega.getWerks());// LGORT //EN PT solo se
			// ingresa al LV01
			stmn.setString(14, hu);// LGORT //EN PT solo se ingresa al LV01
			// stmn.setString(15, entrega.getWerks());// LGORT //EN PT solo se
			// ingresa al LV01

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("Inventario aumentado");

				LOCATION.error("recepcion LGNUM: " + entrega.getuDestino0() + " LGTYP: " + entrega.getuDestino1()
						+ " LGPLA:" + entrega.getuDestino2());
				utils.actualizarInventarioCarriles(entrega.getuDestino0(), entrega.getuDestino1(),
						entrega.getuDestino2());

			} else {
				result.setId(0);
				result.setMsg("El inventario no fue aumentado");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public ResultDTO insertProcesoContingencia_4_14_32(String werks, String VBELN, String lfart, String user) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			LOCATION.error("antes de ejecutar insert 4");

			stmn = con.prepareStatement(INSERT_PROCESO_ZCONTINGENCIA_4_14_32);

			stmn.setString(1, VBELN);
			stmn.setString(2, lfart);
			stmn.setString(3, werks);
			stmn.setString(4, user);

			LOCATION.error("WERKs: " + werks);
			LOCATION.error("vbeln: " + VBELN);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("4to paso ejecutado con exito");
			} else {
				result.setId(0);
				result.setMsg("4to paso ejecutado con error");
			}
		} catch (SQLException e) {
			LOCATION.error("SQL Exception 4to paso: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("SQL Exception 4to paso: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("SQL Exception 4to paso: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput) {

		EntregaInputDTO entregaInputReturn = new EntregaInputDTO();
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> hashhMap = new HashMap<String, String>();

		try {

			LOCATION.error("Error validaEntregaPickingCompleto");
			stmn = con.prepareStatement(VALIDA_PICK_FINALIZADO);

			stmn.setString(1, Utils.zeroFill(entregaInput.getEntrega(), 10));
			stmn.setString(2, entregaInput.getWerks());

			LOCATION.error("Error: " + Utils.zeroFill(entregaInput.getEntrega(), 10));

			LOCATION.error("Error: " + entregaInput.getWerks());
			rs = stmn.executeQuery();

			LOCATION.error("Error despues");
			if (rs.next()) {

				result.setId(1);
				result.setMsg("ENTREGA ENTRANTE YA SE ENCUENTRA EN PICKING");

			} else {
				result.setId(0);
				result.setMsg("ENTREGA ENTRANTE NO DISPONIBLE PARA PICKING");
			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		entregaInputReturn.setResultDT(result);
		entregaInputReturn.setMateriales(hashhMap);
		return entregaInputReturn;
	}

}
