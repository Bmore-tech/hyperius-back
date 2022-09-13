package com.bmore.hyperius.web.repository.old;

import java.sql.CallableStatement;
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
import com.bmore.hyperius.web.dto.NormaEmbalajeDTO;
import com.bmore.hyperius.web.dto.NormaEmbalajeItemsDTO;
import com.bmore.hyperius.web.dto.NormasEmbalajeDTO;
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.PaletizadoraItemsDTO;
import com.bmore.hyperius.web.dto.PaletizadorasDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.print.Etiqueta;
import com.bmore.hyperius.web.utils.print.Etiquetas;

@Repository
public class ControlPaletizadoraRepositoryOld {
  
  @Autowired
  private DBConnection dbConnection;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static String UPDATE_AUFNR_EN_PALETIZADORA = "update HCMDB.dbo.ZPAITT_PALLETOBR set AUFNR = ? where WERKS = ? and LGPLA = ? ";

// TODO Remove unused code found by UCDetector
// 	static String MARCAR_HUS_PARA_IMPRIMIR = "update VEKP SET VEKP.ERLKZ = ? where EXIDV in(select top(?) EXIDV from VEKP where VPOBJKEY = (select VPOBJKEY from AUFK WITH(NOLOCK) where AUFNR = ? ) and ERLKZ is null order by EXIDV)";

// TODO Remove unused code found by UCDetector
// 	static String ROLLBACK_MARCAR_HUS_PARA_IMPRIMIR = "update VEKP SET VEKP.ERLKZ = null where VPOBJKEY = (select VPOBJKEY from AUFK WITH(NOLOCK) where AUFNR = ? ) and ERLKZ = ?";

	private static String OBTIENE_HUS_PARA_IMPRIMIR = "select VEKP.EXIDV as HU, VEPO.MATNR as MATERIAL, MAKT.MAKTX as DESCRIPCION, KNA1.NAME1+' '+KNA1.NAME2 as DescPlanta, "
			+ " VEPO.WERKS as CENTRO, VEPO.VEMEH as VEMEH, VEPO.VEMNG as VEMNG, SUBSTRING(VEPO.CHARG,1,4) as CHARG, VEKP.VHILM as TARIMA, "
			+ " convert(varchar(10),convert(date,getdate()),105) as FECHA, replace(convert(varchar(10),getdate(),102),'.','') as FECHA2 "
			+ " from VEPO VEPO  WITH(NOLOCK) "
			+ " inner join VEKP VEKP  WITH(NOLOCK) on VEPO.VENUM = VEKP.VENUM "
			+ " inner join MAKT MAKT  WITH(NOLOCK) on VEPO.MATNR = MAKT.MATNR "
			+ " inner join zCentrosBCPS ZCBC WITH(NOLOCK)  on VEPO.WERKS = ZCBC.werks "
			+ " inner join KNA1 KNA1 WITH(NOLOCK) on ZCBC.kunnr = KNA1.KUNNR "
			+ " where VEPO.VENUM in (select venum from VEKP WITH(NOLOCK) where VPOBJKEY in (select VPOBJKEY from AUFK WITH(NOLOCK) where "
			+ " AUFNR = ?)) and VEPO.VELIN='1' and VEKP.ERLKZ = ? order by hu ";

// TODO Remove unused code found by UCDetector
// 	static String UPDATE_TARIMA_VEKP = "update  HCMDB.dbo.VEKP set VHILM = ? where ERLKZ is null";

	private static String OBTIENE_CANTIDAD_HUS = "  select count(*) as cantidadHUs from  HCMDB.dbo.vekp WITH(NOLOCK) inner join AUFK  on dbo.VEKP.VPOBJKEY = dbo.AUFK.VPOBJKEY "
			+ "and dbo.AUFK.AUFNR= ?";

	private static String OBTIENE_CANTIDAD_HUS_IMPRESAS = " select count(*) as cantidadHUs from  HCMDB.dbo.vekp WITH(NOLOCK) inner join AUFK  on dbo.VEKP.VPOBJKEY = dbo.AUFK.VPOBJKEY "
			+ "and dbo.AUFK.AUFNR= ? where dbo.VEKP.ERLKZ is not null";

// TODO Remove unused code found by UCDetector
// 	static String OBTIENE_LETYS_X_LGNUM_X_CENTRO_X_MATERIAL = "select LGNUM,LHMG1,LHMG2,LHMG3,LETY1,LETY2,LETY3 from  "
// 			+ " HCMDB.dbo.MLGN WITH(NOLOCK) where LGNUM in (select LGNUM from  HCMDB.dbo.t320 WITH(NOLOCK) where WERKS = ? ) and MATNR = ?";

// TODO Remove unused code found by UCDetector
// 	static String OBTIENE_TARIMAS_X_LETYP = "select * from  HCMDB.dbo.ZPAITT_TTW WITH(NOLOCK) where letyp = ? and LGNUM = ?";

// TODO Remove unused code found by UCDetector
// 	static String ACTUALIZA_NORMA_EN_CABECERA = "update HCMDB.dbo.VEKP set  VHILM = ? where VPOBJKEY = "
// 			+ " (select VPOBJKEY from HCMDB.dbo.AUFK WITH(NOLOCK) where AUFNR = ? ) and ERLKZ is null";

// TODO Remove unused code found by UCDetector
// 	static String ACTUALIZA_NORMA_EN_POSICION = "update HCMDB.dbo.VEPO set  VEMNG = ? where VENUM in "
// 			+ " (select venum from HCMDB.dbo.VEKP WITH(NOLOCK) where VPOBJKEY = "
// 			+ " (select VPOBJKEY from HCMDB.dbo.AUFK WITH(NOLOCK) where AUFNR = ? ) and ERLKZ is null and werks = ?)";

	private static String OBTIENE_PALETIZADORAS_X_CENTRO = "SELECT  ZPPBR.WERKS, ZPPBR.LGPLA, ZPPBR.ID_PALETIZ, ZPPBR.AUFNR, TB_BCPS_DATA_NEW_HU.VHILM,MAKT.MAKTX as MAKTX1, TB_BCPS_DATA_NEW_HU.VEMNG, TB_BCPS_DATA_NEW_HU.MEINS, ZPAITT_TTW.MAKTX as MAKTX2 "
			+ "FROM ZPAITT_PALLETOBR ZPPBR WITH(NOLOCK) "
			+ "LEFT JOIN AUFK AUFK ON ZPPBR.AUFNR = AUFK.AUFNR "
			+ "LEFT JOIN TB_BCPS_DATA_NEW_HU ON ZPPBR.AUFNR = dbo.TB_BCPS_DATA_NEW_HU.AUFNR "
			+ "LEFT JOIN MAKT MAKT ON TB_BCPS_DATA_NEW_HU.VHILM = MAKT.MATNR "
			+ "LEFT JOIN ZPAITT_TTW ZPAITT_TTW ON TB_BCPS_DATA_NEW_HU.VHILM = ZPAITT_TTW.MATNR "
			+ "WHERE ZPPBR.WERKS = ?  group bY ZPPBR.WERKS, ZPPBR.LGPLA, ZPPBR.ID_PALETIZ, ZPPBR.AUFNR, TB_BCPS_DATA_NEW_HU.VHILM, MAKT.MAKTX, TB_BCPS_DATA_NEW_HU.VEMNG, TB_BCPS_DATA_NEW_HU.MEINS, ZPAITT_TTW.MAKTX ";

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////77
	// //////BCPS genera HUS
	// ///////////

	private static String OBTIENE_NORMA_EMBALAJE = "select distinct convert(int,POBJID) as POBJID from PACKKP WITH(NOLOCK) WHERE PACKNR = ?";

	private static String OBTIENE_TARIMAS_X_MATNR = "SELECT PAT.PACKNR,  PAT.MATNR AS TARIMA,MAKT.MAKTX as MAKTX1 , dbo.ZPAITT_TTW.MAKTX as MAKTX2 "
			+ "FROM PACKPO PAT  "
			+ "left JOIN MAKT MAKT ON PAT.MATNR = MAKT.MATNR "
			+ "left join ZPAITT_TTW on PAT.MATNR = dbo.ZPAITT_TTW.MATNR "
			+ "WHERE PAT.PAITEMTYPE = 'P' and PAT.PACKITEM='000010' AND PAT.PACKNR IN   "
			+ "(SELECT DISTINCT(PACKNR) FROM PACKPO WHERE MATNR = ?) ";

	private static String OBTIENE_CANTIDADES_TARIMAS = "SELECT TRGQTY,BASEUNIT FROM PACKPO WHERE PAITEMTYPE = 'I' AND PACKNR = ? and MATNR=?";

	private static String CAMBIA_NORMA_EMBALAJE_BCPS = "exec SP_BCPS_WM_CAMBIA_NORMA ?,?,?,?,?,?,?,?";

	private static String GENERA_HU_BCPS = "exec SP_BCPS_WM_GENERATE_NEW_HU ?,?,?";

	private static String EMBALAR_HUS = "exec SP_BCPS_WM_GENERATE_NEW_HU_VBELN ?,?,?,?,?,?,?,?,?,?,?,?,?,?";

	private static String OBTIENE_EQUIVALENCIA_UM = "select UMREN,UMREZ from MARM where MATNR = ? and MEINH = ?";

	public PaletizadorasDTO obtienePaletizadoras(String werks) {

		PaletizadorasDTO paletizadoras = new PaletizadorasDTO();
		PaletizadoraItemsDTO items = new PaletizadoraItemsDTO();
		List<PaletizadoraDTO> listPaletizadoras = new ArrayList<PaletizadoraDTO>();
		ResultDTO resultDT = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		PreparedStatement stmn3 = null;
		ResultSet rs3 = null;

		HashMap<String, PaletizadoraDTO> hashMap = new HashMap<String, PaletizadoraDTO>();

		try {

			stmn = con.prepareStatement(OBTIENE_PALETIZADORAS_X_CENTRO);
			stmn.setString(1, werks);

			rs = stmn.executeQuery();

			while (rs.next()) {

				PaletizadoraDTO paletizadoraDTO = new PaletizadoraDTO();

				paletizadoraDTO.setWerks(rs.getString("WERKS"));
				paletizadoraDTO.setLgpla(rs.getString("LGPLA"));
				paletizadoraDTO.setIdPaletizadora(rs.getString("ID_PALETIZ"));
				paletizadoraDTO.setAufnr(rs.getString("AUFNR"));
				paletizadoraDTO.setTarima(rs.getString("VHILM"));
				paletizadoraDTO.setDescripcionTarima(rs.getString("MAKTX2"));

				if (paletizadoraDTO.getDescripcionTarima() == null
						|| (paletizadoraDTO.getDescripcionTarima() != null && paletizadoraDTO
								.getDescripcionTarima().trim().equals(""))) {
					paletizadoraDTO
							.setDescripcionTarima(rs.getString("MAKTX1"));
				}

				paletizadoraDTO.setCantidadXTarima(rs.getString("VEMNG"));
				paletizadoraDTO.setUnidadMedida(rs.getString("MEINS"));

				paletizadoraDTO.setRowId(paletizadoraDTO.getWerks()
						+ paletizadoraDTO.getLgpla()
						+ paletizadoraDTO.getIdPaletizadora()
						+ paletizadoraDTO.getAufnr());

				if (hashMap.get(paletizadoraDTO.getRowId()) == null) {
					hashMap.put(paletizadoraDTO.getRowId(), paletizadoraDTO);

					try {

						stmn2 = con.prepareStatement(OBTIENE_CANTIDAD_HUS);

						stmn2.setString(1, paletizadoraDTO.getAufnr());

						rs2 = stmn2.executeQuery();

						if (rs2.next()) {

							paletizadoraDTO.setCantidadEtiquetas(rs2
									.getString("cantidadHUs"));
						}

					} catch (Exception e) {

						log.error("Error OBTIENE_CANTIDAD_HUS: "
								+ e.toString());

					}

					try {

						stmn3 = con
								.prepareStatement(OBTIENE_CANTIDAD_HUS_IMPRESAS);

						stmn3.setString(1, paletizadoraDTO.getAufnr());

						rs3 = stmn3.executeQuery();

						if (rs3.next()) {

							paletizadoraDTO.setCantidadEtiquetasImpresas(rs3
									.getString("cantidadHUs"));
						}

					} catch (Exception e) {

						log.error("Error OBTIENE_CANTIDAD_HUS: "
								+ e.toString());

					}

					listPaletizadoras.add(paletizadoraDTO);

				}

			}

			if (listPaletizadoras.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("Paletizadoras recuperadas correctamente");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("No fue posible recuperar paletizadoras");
			}

			items.setItem(listPaletizadoras);
			paletizadoras.setPaletizadoras(items);

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			log.error("SQLException in obtienePaletizadoras:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			log.error("Exception in obtienePaletizadoras:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				log.error("Exception in obtienePaletizadoras:"
						+ e.toString());
			}
		}
		paletizadoras.setResultDT(resultDT);

		return paletizadoras;

	}

	public ResultDTO guardaPaletizadora(PaletizadoraDTO paletizadora) {

		ResultDTO resultDT = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;

		int resultado = 0;

		try {

			log.error("AUFNR: " + paletizadora.getAufnr() + "-"
					+ paletizadora.getWerks() + "-" + paletizadora.getLgpla()
					+ "-" + paletizadora.getIdPaletizadora());

			stmn = con.prepareStatement(UPDATE_AUFNR_EN_PALETIZADORA);
			stmn.setString(1, paletizadora.getAufnr());
			stmn.setString(2, paletizadora.getWerks());
			stmn.setString(3, paletizadora.getLgpla());
			// stmn.setString(4, paletizadora.getIdPaletizadora());

			resultado = stmn.executeUpdate();
			if (resultado > 0) {
				resultDT.setId(1);
				resultDT.setMsg("Se actualizo la nueva orden de producción");

			} else {
				resultDT.setId(2);
				resultDT
						.setMsg("No fue posible actualizar la orden de producción");

			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			log.error("SQLException in obtienePaletizadoras:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			log.error("Exception in obtienePaletizadoras:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				log.error("Exception in obtienePaletizadoras:"
						+ e.toString());
			}
		}

		return resultDT;

	}

// TODO Remove unused code found by UCDetector
// 	public ResultDTO marcarHusParaImprimir(PaletizadoraDTO paletizadora) {
// 
// 		ResultDTO resultDT = new ResultDTO();
// 
// 		Connection con = dbConnection.createConnection();
// 
// 		PreparedStatement stmn = null;
// 
// 		int resultado = 0;
// 
// 		LOCATION.error("Canditdad de etiquetas: "
// 				+ paletizadora.getCantidadEtiqueasAImprimir());
// 
// 		resultDT.setTypeS(Utils.getKeyTimeStamp());
// 
// 		try {
// 
// 			BigDecimal bigDecimal = new BigDecimal(paletizadora
// 					.getCantidadEtiqueasAImprimir());
// 
// 			int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger()
// 					+ "");
// 
// 			stmn = con.prepareStatement(MARCAR_HUS_PARA_IMPRIMIR);
// 
// 			stmn.setString(1, resultDT.getTypeS());
// 			stmn.setInt(2, ingresarZpicking);
// 			stmn.setString(3, paletizadora.getAufnr());
// 
// 			resultado = stmn.executeUpdate();
// 			if (resultado == ingresarZpicking) {
// 				resultDT.setId(1);
// 				resultDT.setMsg(paletizadora.getAufnr());
// 
// 			} else {
// 				// ROLL BACK A LAS POSIBLES N ETIQUETAS MARCADAS
// 
// 				stmn = con.prepareStatement(ROLLBACK_MARCAR_HUS_PARA_IMPRIMIR);
// 
// 				stmn.setString(1, paletizadora.getAufnr());
// 				stmn.setString(2, resultDT.getTypeS());
// 				resultado = stmn.executeUpdate();
// 
// 				resultDT.setId(2);
// 				resultDT
// 						.setMsg("No fue posible marcar las etiquetas para imprimir, intente con: "
// 								+ resultado + " etiquetas.");
// 			}
// 
// 		} catch (SQLException e) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(e.getMessage());
// 			LOCATION.error("SQLException in marcarHusParaImprimir:"
// 					+ e.toString());
// 		} catch (Exception en) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(en.getMessage());
// 			LOCATION.error("Exception in marcarHusParaImprimir:"
// 					+ en.toString());
// 		} finally {
// 			try {
// 				DBConnection.closeConnection(con);
// 			} catch (Exception e) {
// 				resultDT.setId(2);
// 				resultDT.setMsg(e.getMessage());
// 				LOCATION.error("Exception in marcarHusParaImprimir:"
// 						+ e.toString());
// 			}
// 		}
// 
// 		return resultDT;
// 
// 	}

	public ResultDTO generaHusBCPS(PaletizadoraDTO paletizadoraDTO,
			String keyTimeStamp) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(GENERA_HU_BCPS);
			// AUFNR,MATNR,VHILM,VEMNG,MEINS,PACKNR

			callableStatement.setString(1, paletizadoraDTO.getAufnr());
			callableStatement.setString(2, keyTimeStamp);
			callableStatement.registerOutParameter(3, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(3);
			log.error("ID:" + id);
			result.setId(id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());

			log.error("SQLException1 in generaHusParaImprimirBCPS:"
					+ e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			log.error("Exception in generaHusParaImprimirBCPS:"
					+ en.toString());

		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				log.error("Exception in generaHusParaImprimirBCPS:"
						+ e.toString());

			}
		}

		return result;

	}

	public ResultDTO obtieneCantidadHUS(String aufnr) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {

			stmn = con.prepareStatement(OBTIENE_CANTIDAD_HUS);

			stmn.setString(1, aufnr);
			stmn.setString(2, aufnr);

			rs = stmn.executeQuery();

			while (rs.next()) {

				resultDT.setId(1);

				resultDT.setTypeS(rs.getString("cantidadHUs"));
				resultDT.setMsg(rs.getString("husImpresas"));

			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			log.error("SQLException in obtieneHusParaImprimir:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			log.error("Exception in obtieneHusParaImprimir:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				log.error("Exception in obtieneHusParaImprimir:"
						+ e.toString());
			}
		}

		return resultDT;

	}

	public Etiquetas obtieneHusParaImprimir(String aufnr, String key) {

		ResultDTO resultDT = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;

		Etiquetas etiquetas = new Etiquetas();
		List<Etiqueta> listaEtiquetas = new ArrayList<Etiqueta>();

		try {

			log.error("AUFNR: " + aufnr + "  key_:" + key);

			stmn = con.prepareStatement(OBTIENE_HUS_PARA_IMPRIMIR);
			stmn.setString(1, aufnr);
			stmn.setString(2, key);
			String HU = "VACIO";
			rs = stmn.executeQuery();

			while (rs.next()) {
				HU = rs.getString("HU");
				Etiqueta e = new Etiqueta();

				e.setEXIDV_HU(HU);
				e.setFechaImpr(rs.getString("FECHA"));
				log.error("HU en DAO: " + HU);
				e.setBarCode(HU + "," + Utils.zeroClean(rs.getString("TARIMA"))
						+ "," + Utils.zeroClean(rs.getString("MATERIAL")) + ","
						+ rs.getString("VEMNG") + "," + rs.getString("VEMEH")
						+ "," + rs.getString("CHARG") + ","
						+ rs.getString("FECHA2"));
				e.setCHARG4(rs.getString("CHARG"));
				e.setMATNR(Utils.zeroClean(rs.getString("MATERIAL")));
				e.setMAKTX_desc(rs.getString("DESCRIPCION"));
				e.setVEMEH(rs.getString("VEMEH"));
				e.setVEMNG(rs.getString("VEMNG"));
				e.setWERKS(rs.getString("CENTRO"));
				e.setNAME1(rs.getString("DescPlanta"));
				listaEtiquetas.add(e);

			}

			if (listaEtiquetas.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("HU's recuperadas");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("No fue posible recuperar HU's");
			}

			etiquetas.setItems(listaEtiquetas);
			etiquetas.setResultDT(resultDT);

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			log.error("SQLException in obtieneHusParaImprimir:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			log.error("Exception in obtieneHusParaImprimir:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				log.error("Exception in obtieneHusParaImprimir:"
						+ e.toString());
			}
		}

		return etiquetas;

	}

// TODO Remove unused code found by UCDetector
// 	public NormasEmbalajeDTO obtieneLetyps(String werks, String matnr) {
// 
// 		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
// 		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();
// 		List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();
// 		ResultDTO resultDT = new ResultDTO();
// 
// 		Connection con = dbConnection.createConnection();
// 
// 		PreparedStatement stmn = null;
// 		ResultSet rs = null;
// 
// 		try {
// 
// 			// Obtiene el material de la orden de produccion
// 
// 			stmn = con
// 					.prepareStatement(OBTIENE_LETYS_X_LGNUM_X_CENTRO_X_MATERIAL);
// 
// 			stmn.setString(1, werks);
// 			stmn.setString(2, matnr);
// 
// 			rs = stmn.executeQuery();
// 
// 			while (rs.next()) {
// 
// 				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();
// 				NormaEmbalajeDTO normaEmbalajeDTO2 = new NormaEmbalajeDTO();
// 				NormaEmbalajeDTO normaEmbalajeDTO3 = new NormaEmbalajeDTO();
// 
// 				normaEmbalajeDTO1.setCantidad(rs.getString("LHMG1"));
// 				normaEmbalajeDTO1.setLetyp(rs.getString("LETY1"));
// 				normaEmbalajeDTO1.setLegnum(rs.getString("LGNUM"));
// 
// 				normaEmbalajeDTO2.setCantidad(rs.getString("LHMG2"));
// 				normaEmbalajeDTO2.setLetyp(rs.getString("LETY2"));
// 				normaEmbalajeDTO2.setLegnum(rs.getString("LGNUM"));
// 
// 				normaEmbalajeDTO3.setCantidad(rs.getString("LHMG3"));
// 				normaEmbalajeDTO3.setLetyp(rs.getString("LETY3"));
// 				normaEmbalajeDTO3.setLegnum(rs.getString("LGNUM"));
// 
// 				listNormaEmbalajeDTO.add(normaEmbalajeDTO1);
// 				listNormaEmbalajeDTO.add(normaEmbalajeDTO2);
// 				listNormaEmbalajeDTO.add(normaEmbalajeDTO3);
// 
// 			}
// 
// 			normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);
// 
// 			if (listNormaEmbalajeDTO.size() > 0) {
// 				resultDT.setId(1);
// 				resultDT.setMsg("Letyps recuperadas");
// 			} else {
// 				resultDT.setId(2);
// 				resultDT.setMsg("No fue posible recuperar Letyps");
// 			}
// 			normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);
// 			normasEmbalajeDTO.setResultDT(resultDT);
// 
// 		} catch (SQLException e) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(e.getMessage());
// 			LOCATION.error("SQLException in obtieneHusParaImprimir:"
// 					+ e.toString());
// 		} catch (Exception en) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(en.getMessage());
// 			LOCATION.error("Exception in obtieneHusParaImprimir:"
// 					+ en.toString());
// 		} finally {
// 			try {
// 				DBConnection.closeConnection(con);
// 			} catch (Exception e) {
// 				resultDT.setId(2);
// 				resultDT.setMsg(e.getMessage());
// 				LOCATION.error("Exception in obtieneHusParaImprimir:"
// 						+ e.toString());
// 			}
// 		}
// 
// 		return normasEmbalajeDTO;
// 	}

	public NormasEmbalajeDTO obtieneTarimas(String matnr) {

		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();

		ResultDTO resultDT = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		PreparedStatement smtn3 = null;
		ResultSet rs3 = null;

		try {

			// Obtiene el material de la orden de produccion

			stmn = con.prepareStatement(OBTIENE_TARIMAS_X_MATNR);

			stmn.setString(1, matnr);

			rs = stmn.executeQuery();
			List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();

			HashMap<String, String> hashMap = new HashMap<String, String>();

			while (rs.next()) {

				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();

				normaEmbalajeDTO1.setTarima(rs.getString("TARIMA"));
				normaEmbalajeDTO1.setDescripcionTarima(rs.getString("MAKTX2"));

				if (normaEmbalajeDTO1.getDescripcionTarima() == null
						|| (normaEmbalajeDTO1.getDescripcionTarima() != null && normaEmbalajeDTO1
								.getDescripcionTarima().trim().equals(""))) {
					normaEmbalajeDTO1.setDescripcionTarima(rs
							.getString("MAKTX1"));
				}

				normaEmbalajeDTO1.setLetyp(rs.getString("PACKNR"));

				stmn2 = con.prepareStatement(OBTIENE_CANTIDADES_TARIMAS);
				stmn2.setString(1, normaEmbalajeDTO1.getLetyp());
				stmn2.setString(2, matnr);

				rs2 = stmn2.executeQuery();

				int cont = 0;
				while (rs2.next()) {

					smtn3 = con.prepareStatement(OBTIENE_NORMA_EMBALAJE);
					smtn3.setString(1, normaEmbalajeDTO1.getLetyp());
					rs3 = smtn3.executeQuery();
					while (rs3.next()) {
						normaEmbalajeDTO1.setPobjid(rs3.getString("POBJID"));
					}

					if (cont != 0) {

						NormaEmbalajeDTO normaEmbalajeDTO2 = new NormaEmbalajeDTO();

						normaEmbalajeDTO2.setTarima(normaEmbalajeDTO1
								.getTarima());

						normaEmbalajeDTO2
								.setDescripcionTarima(normaEmbalajeDTO1
										.getDescripcionTarima());

						normaEmbalajeDTO2
								.setLetyp(normaEmbalajeDTO1.getLetyp());
						normaEmbalajeDTO2.setPobjid(normaEmbalajeDTO1
								.getPobjid());

						normaEmbalajeDTO2.setCantidad(rs2.getString("TRGQTY"));
						normaEmbalajeDTO2.setUnidadMedida(rs2
								.getString("BASEUNIT"));

						if (hashMap.get(normaEmbalajeDTO2.getTarima()
								+ normaEmbalajeDTO2.getCantidad()
								+ normaEmbalajeDTO2.getUnidadMedida()
								+ normaEmbalajeDTO2.getPobjid()) == null) {

							hashMap.put(normaEmbalajeDTO2.getTarima()
									+ normaEmbalajeDTO2.getCantidad()
									+ normaEmbalajeDTO2.getUnidadMedida()
									+ normaEmbalajeDTO2.getPobjid(), "1");

							listNormaEmbalajeDTO.add(normaEmbalajeDTO2);
						}

					} else {

						normaEmbalajeDTO1.setCantidad(rs2.getString("TRGQTY"));
						normaEmbalajeDTO1.setUnidadMedida(rs2
								.getString("BASEUNIT"));
						if (hashMap.get(normaEmbalajeDTO1.getTarima()
								+ normaEmbalajeDTO1.getCantidad()
								+ normaEmbalajeDTO1.getUnidadMedida()
								+ normaEmbalajeDTO1.getPobjid()) == null) {

							hashMap.put(normaEmbalajeDTO1.getTarima()
									+ normaEmbalajeDTO1.getCantidad()
									+ normaEmbalajeDTO1.getUnidadMedida()
									+ normaEmbalajeDTO1.getPobjid(), "1");

							listNormaEmbalajeDTO.add(normaEmbalajeDTO1);
						}

						cont++;

					}

				}

			}

			normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);

			if (listNormaEmbalajeDTO.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("Tarimas recuperadas");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("No fue posible recuperar tarimas");
			}
			normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);
			normasEmbalajeDTO.setResultDT(resultDT);

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			log.error("SQLException in obtieneHusParaImprimir:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			log.error("Exception in obtieneHusParaImprimir:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				log.error("Exception in obtieneHusParaImprimir:"
						+ e.toString());
			}
		}

		return normasEmbalajeDTO;

	}

// TODO Remove unused code found by UCDetector
// 	public NormasEmbalajeDTO obtieneTarimas(String letyp, String legnum) {
// 
// 		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
// 		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();
// 
// 		ResultDTO resultDT = new ResultDTO();
// 
// 		Connection con = dbConnection.createConnection();
// 
// 		PreparedStatement stmn = null;
// 		ResultSet rs = null;
// 
// 		try {
// 
// 			// Obtiene el material de la orden de produccion
// 
// 			stmn = con.prepareStatement(OBTIENE_TARIMAS_X_LETYP);
// 
// 			stmn.setString(1, letyp);
// 			stmn.setString(2, legnum);
// 
// 			rs = stmn.executeQuery();
// 			List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();
// 
// 			while (rs.next()) {
// 
// 				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();
// 
// 				normaEmbalajeDTO1.setLetyp(rs.getString("LETYP"));
// 				normaEmbalajeDTO1.setTarima(rs.getString("MATNR"));
// 				normaEmbalajeDTO1.setDescripcionTarima(rs.getString("MAKTX"));
// 
// 				listNormaEmbalajeDTO.add(normaEmbalajeDTO1);
// 
// 			}
// 
// 			normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);
// 
// 			if (listNormaEmbalajeDTO.size() > 0) {
// 				resultDT.setId(1);
// 				resultDT.setMsg("Tarimas recuperadas");
// 			} else {
// 				resultDT.setId(2);
// 				resultDT.setMsg("No fue posible recuperar tarimas");
// 			}
// 			normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);
// 			normasEmbalajeDTO.setResultDT(resultDT);
// 
// 		} catch (SQLException e) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(e.getMessage());
// 			LOCATION.error("SQLException in obtieneTarimas:" + e.toString());
// 		} catch (Exception en) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(en.getMessage());
// 			LOCATION.error("Exception in obtieneTarimas:" + en.toString());
// 		} finally {
// 			try {
// 				DBConnection.closeConnection(con);
// 			} catch (Exception e) {
// 				resultDT.setId(2);
// 				resultDT.setMsg(e.getMessage());
// 				LOCATION.error("Exception in obtieneTarimas:" + e.toString());
// 			}
// 		}
// 
// 		return normasEmbalajeDTO;
// 
// 	}

// TODO Remove unused code found by UCDetector
// 	public ResultDTO cambiarNormaEmbalaje(PaletizadoraDTO paletizadoraDTO) {
// 
// 		ResultDTO resultDT = new ResultDTO();
// 
// 		Connection con = dbConnection.createConnection();
// 
// 		PreparedStatement stmn = null;
// 		int count = 0;
// 
// 		try {
// 			// Transaction
// 			con.setAutoCommit(false);
// 			// Obtiene el material de la orden de produccion
// 
// 			stmn = con.prepareStatement(ACTUALIZA_NORMA_EN_CABECERA);
// 
// 			stmn.setString(1, paletizadoraDTO.getTarima());
// 			stmn.setString(2, paletizadoraDTO.getAufnr());
// 
// 			count = stmn.executeUpdate();
// 
// 			if (count > 0) {
// 
// 				stmn = con.prepareStatement(ACTUALIZA_NORMA_EN_POSICION);
// 
// 				stmn.setString(1, paletizadoraDTO.getCantidadXTarima());
// 				stmn.setString(2, paletizadoraDTO.getAufnr());
// 				stmn.setString(3, paletizadoraDTO.getWerks());
// 
// 				count = stmn.executeUpdate();
// 
// 				if (count > 0) {
// 					resultDT.setId(1);
// 					resultDT.setMsg("Norma actualizada");
// 					con.commit();
// 				} else {
// 
// 					con.rollback();
// 					resultDT.setId(2);
// 					resultDT.setMsg("Error al tratar de actualizar norma");
// 				}
// 
// 			} else {
// 				resultDT.setId(2);
// 				resultDT.setMsg("Error al tratar de actualizar norma");
// 				con.rollback();
// 			}
// 
// 		} catch (SQLException e) {
// 
// 			resultDT.setId(2);
// 			resultDT.setMsg(e.getMessage());
// 			LOCATION.error("SQLException in obtieneHusParaImprimir:"
// 					+ e.toString());
// 
// 		} catch (Exception en) {
// 			resultDT.setId(2);
// 			resultDT.setMsg(en.getMessage());
// 			LOCATION.error("Exception in obtieneHusParaImprimir:"
// 					+ en.toString());
// 		} finally {
// 			try {
// 				DBConnection.closeConnection(con);
// 			} catch (Exception e) {
// 				resultDT.setId(2);
// 				resultDT.setMsg(e.getMessage());
// 				LOCATION.error("Exception in obtieneHusParaImprimir:"
// 						+ e.toString());
// 			}
// 		}
// 
// 		return resultDT;
// 
// 	}

	public ResultDTO cambiarNormaEmbalajeBCPS(PaletizadoraDTO paletizadoraDTO) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(CAMBIA_NORMA_EMBALAJE_BCPS);
			// AUFNR,MATNR,VHILM,VEMNG,MEINS,PACKNR

			callableStatement.setString(1, paletizadoraDTO.getAufnr());
			callableStatement.setString(2, paletizadoraDTO
					.getMaterialPTTarima());
			callableStatement.setString(3, paletizadoraDTO.getTarima());
			callableStatement
					.setString(4, paletizadoraDTO.getCantidadXTarima());
			callableStatement.setString(5, paletizadoraDTO.getUnidadMedida());
			callableStatement.setString(6, paletizadoraDTO.getLetyp());
			callableStatement.setString(7, paletizadoraDTO.getWerks());
			callableStatement.registerOutParameter(8, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(8);
			log.error("ID:" + id);
			result.setId(id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}

		return result;

	}

	public ResultDTO embalarHus(PaletizadoraDTO paletizadoraDTO,
			String keyTimeStamp, String userId) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(EMBALAR_HUS);
			// AUFNR,ERLKZ,VHILM,MATNR,MEINS,VEMNG,PACKNR,WERKS,CHARG,CHARG2,RETURN

			callableStatement.setString(1, paletizadoraDTO.getAufnr());
			callableStatement.setString(2, keyTimeStamp);
			callableStatement.setString(3, paletizadoraDTO.getTarima());
			callableStatement.setString(4, paletizadoraDTO
					.getMaterialPTTarima());
			callableStatement.setString(5, paletizadoraDTO.getUnidadMedida());
			callableStatement
					.setString(6, paletizadoraDTO.getCantidadXTarima());
			callableStatement.setString(7, paletizadoraDTO.getLetyp());
			callableStatement.setString(8, paletizadoraDTO.getWerks());
			callableStatement.setString(9, paletizadoraDTO.getCharg());
			callableStatement.setString(10, paletizadoraDTO.getCharg2());

			callableStatement.setString(11, paletizadoraDTO
					.getCantidadXTarima2());
			callableStatement.setString(12, paletizadoraDTO.getUnidadMedida2());
			callableStatement.setString(13, userId);

			log.error("Charg: " + paletizadoraDTO.getCharg());
			log.error("Charg2: " + paletizadoraDTO.getCharg2());
			callableStatement.registerOutParameter(14, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(14);

			log.error("ID:" + id);

			result.setId(id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());

			log.error("SQLException1 in generaHusParaImprimirBCPS:"
					+ e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			log.error("Exception in generaHusParaImprimirBCPS:"
					+ en.toString());

		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				log.error("Exception in generaHusParaImprimirBCPS:"
						+ e.toString());

			}
		}

		return result;

	}

	public NormasEmbalajeDTO obtieneEquivalenciasUM(String matnr, String um) {

		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();

		ResultDTO resultDT = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {

			// Obtiene el material de la orden de produccion

			stmn = con.prepareStatement(OBTIENE_EQUIVALENCIA_UM);

			stmn.setString(1, matnr);
			stmn.setString(2, um);

			rs = stmn.executeQuery();
			List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();

			while (rs.next()) {

				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();

				normaEmbalajeDTO1.setUmren(rs.getString("UMREN"));
				normaEmbalajeDTO1.setUmrez(rs.getString("UMREZ"));

				listNormaEmbalajeDTO.add(normaEmbalajeDTO1);

			}

			normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);

			if (listNormaEmbalajeDTO.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("Conversión recuperada");
			} else {
				resultDT.setId(2);
				resultDT
						.setMsg("No fue posible recuperar la conversión tarimas");
			}
			normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);
			normasEmbalajeDTO.setResultDT(resultDT);

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			log.error("SQLException in obtieneConversionUM:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			log
					.error("Exception in obtieneConversionUM:" + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				log.error("Exception in obtieneConversionUM:"
						+ e.toString());
			}
		}

		return normasEmbalajeDTO;

	}
}
