package com.bmore.hyperius.web.repository.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.NormaEmbalajeDTO;
import com.bmore.hyperius.web.dto.NormaEmbalajeItemsDTO;
import com.bmore.hyperius.web.dto.NormasEmbalajeDTO;
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.PaletizadoraItemsDTO;
import com.bmore.hyperius.web.dto.PaletizadorasDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.ControlPaletizadoraRepository;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.print.Etiqueta;
import com.bmore.hyperius.web.utils.print.Etiquetas;

@Repository
public class ControlPaletizadoraRepositoryImpl implements ControlPaletizadoraRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ResultDTO cambiarNormaEmbalaje(PaletizadoraDTO paletizadoraDTO) {
		ResultDTO resultDT = new ResultDTO();

		String query1 = "UPDATE VEKP SET VHILM = ? WHERE VPOBJKEY = "
				+ " (SELECT VPOBJKEY FROM AUFK WITH(NOLOCK) WHERE AUFNR = ? ) AND ERLKZ IS NULL";
		Object[] args1 = { paletizadoraDTO.getTarima(), paletizadoraDTO.getAufnr() };

		String query2 = "UPDATE VEPO SET VEMNG = ? WHERE VENUM IN "
				+ " (SELECT venum FROM VEKP WITH(NOLOCK) WHERE VPOBJKEY = "
				+ " (SELECT VPOBJKEY FROM AUFK WITH(NOLOCK) WHERE AUFNR = ? ) AND ERLKZ IS NULL AND werks = ?)";
		Object[] args2 = { paletizadoraDTO.getCantidadXTarima(), paletizadoraDTO.getAufnr(),
				paletizadoraDTO.getWerks() };

		int count = jdbcTemplate.update(query1, args1);

		if (count > 1) {
			count = jdbcTemplate.update(query2, args2);

			if (count > 0) {
				resultDT.setId(1);
				resultDT.setMsg("Norma actualizada");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("Error al tratar de actualizar norma");
			}
		}

		return resultDT;
	}

	@Override
	public ResultDTO cambiarNormaEmbalajeBCPS(PaletizadoraDTO paletizadoraDTO) {
		String query = "EXEC SP_BCPS_WM_CAMBIA_NORMA ?, ?, ?, ?, ?, ?, ?, ?";
		Object[] args = { paletizadoraDTO.getAufnr(), 
				paletizadoraDTO.getMaterialPTTarima(),
				paletizadoraDTO.getTarima(), 
				paletizadoraDTO.getCantidadXTarima(), 
				paletizadoraDTO.getUnidadMedida(),
				paletizadoraDTO.getLetyp(), 
				paletizadoraDTO.getWerks(), 
				java.sql.Types.INTEGER };
		ResultDTO result = new ResultDTO();

		int id = jdbcTemplate.update(query, args);
		result.setId(id);

		return result;
	}

	@Override
	public ResultDTO embalarHus(PaletizadoraDTO paletizadoraDTO, String keyTimeStamp, String userId) {
		String query = "EXEC SP_BCPS_WM_GENERATE_NEW_HU_VBELN ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
		Object[] args = { paletizadoraDTO.getAufnr(), keyTimeStamp, paletizadoraDTO.getTarima(),
				paletizadoraDTO.getMaterialPTTarima(), paletizadoraDTO.getUnidadMedida(),
				paletizadoraDTO.getCantidadXTarima(), paletizadoraDTO.getLetyp(), paletizadoraDTO.getWerks(),
				paletizadoraDTO.getCharg(), paletizadoraDTO.getCharg2(), paletizadoraDTO.getCantidadXTarima2(),
				paletizadoraDTO.getUnidadMedida2(), userId, java.sql.Types.INTEGER };
		ResultDTO result = new ResultDTO();

		int id = jdbcTemplate.update(query, args);
		result.setId(id);

		return result;
	}

	@Override
	public ResultDTO generaHusBCPS(PaletizadoraDTO paletizadoraDTO, String keyTimeStamp) {
		String query = "EXEC SP_BCPS_WM_GENERATE_NEW_HU ?, ?, ?";
		Object[] args = { paletizadoraDTO.getAufnr(), keyTimeStamp, java.sql.Types.INTEGER };
		ResultDTO result = new ResultDTO();

		int id = jdbcTemplate.update(query, args);

		result.setId(id);

		return result;
	}

	@Override
	public ResultDTO guardaPaletizadora(PaletizadoraDTO paletizadora) {
		String query = "UPDATE ZPAITT_PALLETOBR SET AUFNR = ? WHERE WERKS = ? AND LGPLA = ?";
		Object[] args = { paletizadora.getAufnr(), paletizadora.getWerks(), paletizadora.getLgpla() };
		ResultDTO resultDT = new ResultDTO();
		int result;

		result = jdbcTemplate.update(query, args);

		if (result > 0) {
			resultDT.setId(1);
			resultDT.setMsg("Se actualizo la nueva orden de producción");
		} else {
			resultDT.setId(2);
			resultDT.setMsg("No fue posible actualizar la orden de producción");
		}

		return resultDT;
	}

	@Override
	public ResultDTO marcarHusParaImprimir(PaletizadoraDTO paletizadora) {
		ResultDTO resultDT = new ResultDTO();
		resultDT.setTypeS(Utils.getKeyTimeStamp());

		BigDecimal bigDecimal = new BigDecimal(paletizadora.getCantidadEtiqueasAImprimir());
		int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

		String query1 = "UPDATE VEKP SET VEKP.ERLKZ = ? WHERE EXIDV IN(SELECT TOP(?) EXIDV FROM VEKP WHERE VPOBJKEY = "
				+ "(SELECT VPOBJKEY FROM AUFK WITH(NOLOCK) where AUFNR = ? ) AND ERLKZ IS NULL ORDER BY EXIDV)";
		Object[] args1 = { resultDT.getTypeS(), ingresarZpicking };

		int result = jdbcTemplate.update(query1, args1);

		if (result == ingresarZpicking) {
			resultDT.setId(1);
			resultDT.setMsg(paletizadora.getAufnr());

		} else {
			String query2 = "UPDATE VEKP SET VEKP.ERLKZ = NULL WHERE VPOBJKEY = "
					+ "(SELECT VPOBJKEY FROM AUFK WITH(NOLOCK) WHERE AUFNR = ? ) AND ERLKZ = ?";
			Object[] args2 = { paletizadora.getAufnr(), resultDT.getTypeS() };

			result = jdbcTemplate.update(query2, args2);

			resultDT.setId(2);
			resultDT.setMsg("No fue posible marcar las etiquetas para imprimir, intente con: " + result + " etiquetas");
		}

		return null;
	}

	@Override
	public ResultDTO obtieneCantidadHUS(String aufnr) {
		String query = "SELECT COUNT(*) AS cantidadHUs FROM vekp WITH(NOLOCK) INNER JOIN AUFK ON VEKP.VPOBJKEY = AUFK.VPOBJKEY "
				+ "AND AUFK.AUFNR = ?";
		Object[] args = { aufnr, aufnr };

		return jdbcTemplate.queryForObject(query, args, new RowMapper<ResultDTO>() {

			@Override
			public ResultDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultDTO resultDTO = new ResultDTO();
				resultDTO.setId(1);
				resultDTO.setTypeS(rs.getString("cantidadHUs"));
				resultDTO.setMsg(rs.getString("husImpresas"));

				return resultDTO;
			}
		});
	}

	@Override
	public NormasEmbalajeDTO obtieneEquivalenciasUM(String matnr, String um) {
		String query = "SELECT UMREN, UMREZ FROM MARM WHERE MATNR = ? AND MEINH = ?";
		Object[] args = { matnr, um };

		ResultDTO resultDT = new ResultDTO();
		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();

		List<NormaEmbalajeDTO> listNormaEmbalajeDTO = jdbcTemplate.query(query, args,
				new RowMapper<NormaEmbalajeDTO>() {

					@Override
					public NormaEmbalajeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						NormaEmbalajeDTO normaEmbalaje = new NormaEmbalajeDTO();

						normaEmbalaje.setUmren(rs.getString("UMREN"));
						normaEmbalaje.setUmrez(rs.getString("UMREZ"));

						return normaEmbalaje;
					}
				});

		if (listNormaEmbalajeDTO.size() > 0) {
			resultDT.setId(1);
			resultDT.setMsg("Conversión recuperada");
		} else {
			resultDT.setId(2);
			resultDT.setMsg("No fue posible recuperar la conversión tarimas");
		}

		normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);
		normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);
		normasEmbalajeDTO.setResultDT(resultDT);

		return normasEmbalajeDTO;
	}

	@Override
	public Etiquetas obtieneHusParaImprimir(String aufnr, String key) {
		String query = "select VEKP.EXIDV as HU, VEPO.MATNR as MATERIAL, MAKT.MAKTX as DESCRIPCION, KNA1.NAME1+' '+KNA1.NAME2 as DescPlanta, "
				+ " VEPO.WERKS as CENTRO, VEPO.VEMEH as VEMEH, VEPO.VEMNG as VEMNG, SUBSTRING(VEPO.CHARG,1,4) as CHARG, VEKP.VHILM as TARIMA, "
				+ " convert(varchar(10),convert(date,getdate()),105) as FECHA, replace(convert(varchar(10),getdate(),102),'.','') as FECHA2 "
				+ " from VEPO VEPO  WITH(NOLOCK) " + " inner join VEKP VEKP  WITH(NOLOCK) on VEPO.VENUM = VEKP.VENUM "
				+ " inner join MAKT MAKT  WITH(NOLOCK) on VEPO.MATNR = MAKT.MATNR "
				+ " inner join zCentrosBCPS ZCBC WITH(NOLOCK)  on VEPO.WERKS = ZCBC.werks "
				+ " inner join KNA1 KNA1 WITH(NOLOCK) on ZCBC.kunnr = KNA1.KUNNR "
				+ " where VEPO.VENUM in (select venum from VEKP WITH(NOLOCK) where VPOBJKEY in (select VPOBJKEY from AUFK WITH(NOLOCK) where "
				+ " AUFNR = ?)) and VEPO.VELIN='1' and VEKP.ERLKZ = ? order by hu ";
		Object[] args = { aufnr, key };

		ResultDTO resultDT = new ResultDTO();
		Etiquetas etiquetas = new Etiquetas();
		List<Etiqueta> listaEtiquetas = jdbcTemplate.query(query, args, new RowMapper<Etiqueta>() {

			@Override
			public Etiqueta mapRow(ResultSet rs, int rowNum) throws SQLException {
				String HU = rs.getString("HU");
				Etiqueta etiqueta = new Etiqueta();

				etiqueta.setEXIDV_HU(HU);
				etiqueta.setFechaImpr(rs.getString("FECHA"));
				etiqueta.setBarCode(HU + "," + Utils.zeroClean(rs.getString("TARIMA")) + ","
						+ Utils.zeroClean(rs.getString("MATERIAL")) + "," + rs.getString("VEMNG") + ","
						+ rs.getString("VEMEH") + "," + rs.getString("CHARG") + "," + rs.getString("FECHA2"));
				etiqueta.setCHARG4(rs.getString("CHARG"));
				etiqueta.setMATNR(Utils.zeroClean(rs.getString("MATERIAL")));
				etiqueta.setMAKTX_desc(rs.getString("DESCRIPCION"));
				etiqueta.setVEMEH(rs.getString("VEMEH"));
				etiqueta.setVEMNG(rs.getString("VEMNG"));
				etiqueta.setWERKS(rs.getString("CENTRO"));
				etiqueta.setNAME1(rs.getString("DescPlanta"));

				return etiqueta;
			}
		});

		if (listaEtiquetas.size() > 0) {
			resultDT.setId(1);
			resultDT.setMsg("HU's recuperadas");
		} else {
			resultDT.setId(2);
			resultDT.setMsg("No fue posible recuperar HU's");
		}

		etiquetas.setItems(listaEtiquetas);
		etiquetas.setResultDT(resultDT);

		return etiquetas;
	}

	@Override
	public NormasEmbalajeDTO obtieneLetyps(String werks, String matnr) {
		String query = "SELECT LGNUM, LHMG1, LHMG2, LHMG3, LETY1, LETY2, LETY3 FROM "
				+ "MLGN WITH(NOLOCK) WHERE LGNUM IN (SELECT LGNUM FROM t320 WITH(NOLOCK) WHERE WERKS = ? ) AND MATNR = ?";
		Object[] args = { werks, matnr };

		List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();
		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();
		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
		ResultDTO resultDT = new ResultDTO();

		jdbcTemplate.queryForObject(query, args, new RowMapper<List<NormaEmbalajeDTO>>() {

			@Override
			public List<NormaEmbalajeDTO> mapRow(ResultSet rs, int rowNum) throws SQLException {
				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();
				NormaEmbalajeDTO normaEmbalajeDTO2 = new NormaEmbalajeDTO();
				NormaEmbalajeDTO normaEmbalajeDTO3 = new NormaEmbalajeDTO();

				normaEmbalajeDTO1.setCantidad(rs.getString("LHMG1"));
				normaEmbalajeDTO1.setLetyp(rs.getString("LETY1"));
				normaEmbalajeDTO1.setLegnum(rs.getString("LGNUM"));

				normaEmbalajeDTO2.setCantidad(rs.getString("LHMG2"));
				normaEmbalajeDTO2.setLetyp(rs.getString("LETY2"));
				normaEmbalajeDTO2.setLegnum(rs.getString("LGNUM"));

				normaEmbalajeDTO3.setCantidad(rs.getString("LHMG3"));
				normaEmbalajeDTO3.setLetyp(rs.getString("LETY3"));
				normaEmbalajeDTO3.setLegnum(rs.getString("LGNUM"));

				listNormaEmbalajeDTO.add(normaEmbalajeDTO1);
				listNormaEmbalajeDTO.add(normaEmbalajeDTO2);
				listNormaEmbalajeDTO.add(normaEmbalajeDTO3);

				return listNormaEmbalajeDTO;
			}
		});

		normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);
		normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);

		if (listNormaEmbalajeDTO.size() > 0) {
			resultDT.setId(1);
			resultDT.setMsg("Letyps recuperadas");
		} else {
			resultDT.setId(2);
			resultDT.setMsg("No fue posible recuperar Letyps");
		}

		return normasEmbalajeDTO;
	}

	@Override
	public PaletizadorasDTO obtienePaletizadoras(String werks) {
		String query1 = "SELECT  ZPPBR.WERKS, ZPPBR.LGPLA, ZPPBR.ID_PALETIZ, ZPPBR.AUFNR, TB_BCPS_DATA_NEW_HU.VHILM,MAKT.MAKTX as MAKTX1, TB_BCPS_DATA_NEW_HU.VEMNG, TB_BCPS_DATA_NEW_HU.MEINS, ZPAITT_TTW.MAKTX as MAKTX2 "
				+ "FROM ZPAITT_PALLETOBR ZPPBR WITH(NOLOCK) " + "LEFT JOIN AUFK AUFK ON ZPPBR.AUFNR = AUFK.AUFNR "
				+ "LEFT JOIN TB_BCPS_DATA_NEW_HU ON ZPPBR.AUFNR = dbo.TB_BCPS_DATA_NEW_HU.AUFNR "
				+ "LEFT JOIN MAKT MAKT ON TB_BCPS_DATA_NEW_HU.VHILM = MAKT.MATNR "
				+ "LEFT JOIN ZPAITT_TTW ZPAITT_TTW ON TB_BCPS_DATA_NEW_HU.VHILM = ZPAITT_TTW.MATNR "
				+ "WHERE ZPPBR.WERKS = ?  group bY ZPPBR.WERKS, ZPPBR.LGPLA, ZPPBR.ID_PALETIZ, ZPPBR.AUFNR, TB_BCPS_DATA_NEW_HU.VHILM, MAKT.MAKTX, TB_BCPS_DATA_NEW_HU.VEMNG, TB_BCPS_DATA_NEW_HU.MEINS, ZPAITT_TTW.MAKTX ";
		Object[] args1 = { werks };

		HashMap<String, PaletizadoraDTO> hashMap = new HashMap<String, PaletizadoraDTO>();
		List<PaletizadoraDTO> listPaletizadoras = new ArrayList<PaletizadoraDTO>();
		PaletizadoraItemsDTO items = new PaletizadoraItemsDTO();
		ResultDTO resultDT = new ResultDTO();
		PaletizadorasDTO paletizadoras = new PaletizadorasDTO();

		listPaletizadoras = jdbcTemplate.query(query1, args1, new RowMapper<PaletizadoraDTO>() {

			@Override
			public PaletizadoraDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				PaletizadoraDTO paletizadoraDTO = new PaletizadoraDTO();

				paletizadoraDTO.setWerks(rs.getString("WERKS"));
				paletizadoraDTO.setLgpla(rs.getString("LGPLA"));
				paletizadoraDTO.setIdPaletizadora(rs.getString("ID_PALETIZ"));
				paletizadoraDTO.setAufnr(rs.getString("AUFNR"));
				paletizadoraDTO.setTarima(rs.getString("VHILM"));
				paletizadoraDTO.setDescripcionTarima(rs.getString("MAKTX2"));

				if (paletizadoraDTO.getDescripcionTarima() == null || (paletizadoraDTO.getDescripcionTarima() != null
						&& paletizadoraDTO.getDescripcionTarima().trim().equals(""))) {
					paletizadoraDTO.setDescripcionTarima(rs.getString("MAKTX1"));
				}

				paletizadoraDTO.setCantidadXTarima(rs.getString("VEMNG"));
				paletizadoraDTO.setUnidadMedida(rs.getString("MEINS"));

				paletizadoraDTO.setRowId(paletizadoraDTO.getWerks() + paletizadoraDTO.getLgpla()
						+ paletizadoraDTO.getIdPaletizadora() + paletizadoraDTO.getAufnr());

				if (hashMap.get(paletizadoraDTO.getRowId()) == null) {
					// Obtiene cantidad de HUS
					hashMap.put(paletizadoraDTO.getRowId(), paletizadoraDTO);
					String query2 = "SELECT COUNT(*) AS cantidadHUs FROM vekp WITH(NOLOCK) INNER JOIN AUFK ON VEKP.VPOBJKEY = AUFK.VPOBJKEY "
							+ "AND AUFK.AUFNR = ?";
					Object[] args2 = { paletizadoraDTO.getAufnr() };
					paletizadoraDTO.setCantidadEtiquetas(jdbcTemplate.queryForObject(query2, args2, String.class));

					// Obtiene cantidad de HUS impresas
					String query3 = "SELECT COUNT(*) AS cantidadHUs FROM vekp WITH(NOLOCK) INNER JOIN AUFK ON VEKP.VPOBJKEY = dbo.AUFK.VPOBJKEY "
							+ "AND AUFK.AUFNR = ? WHERE VEKP.ERLKZ IS NOT NULL";
					Object[] args3 = { paletizadoraDTO.getAufnr() };

					paletizadoraDTO
							.setCantidadEtiquetasImpresas(jdbcTemplate.queryForObject(query3, args3, String.class));
				}
				return paletizadoraDTO;
			}
		});

		if (listPaletizadoras.size() > 0) {
			resultDT.setId(1);
			resultDT.setMsg("Paletizadoras recuperadas correctamente");
		} else {
			resultDT.setId(2);
			resultDT.setMsg("No fue posible recuperar paletizadoras");
		}

		items.setItem(listPaletizadoras);
		paletizadoras.setPaletizadoras(items);
		paletizadoras.setResultDT(resultDT);

		return paletizadoras;
	}

	@Override
	public NormasEmbalajeDTO obtieneTarimas(String matnr) {
		String query1 = "SELECT PAT.PACKNR,  PAT.MATNR AS TARIMA,MAKT.MAKTX as MAKTX1 , dbo.ZPAITT_TTW.MAKTX as MAKTX2 "
				+ "FROM PACKPO PAT  " + "left JOIN MAKT MAKT ON PAT.MATNR = MAKT.MATNR "
				+ "left join ZPAITT_TTW on PAT.MATNR = dbo.ZPAITT_TTW.MATNR "
				+ "WHERE PAT.PAITEMTYPE = 'P' and PAT.PACKITEM='000010' AND PAT.PACKNR IN   "
				+ "(SELECT DISTINCT(PACKNR) FROM PACKPO WHERE MATNR = ?) ";
		Object[] args1 = { matnr };

		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
		HashMap<String, String> hashMap = new HashMap<String, String>();
		List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();
		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();
		ResultDTO resultDT = new ResultDTO();

		jdbcTemplate.query(query1, args1, new RowMapper<NormaEmbalajeDTO>() {

			@Override
			public NormaEmbalajeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();

				normaEmbalajeDTO1.setTarima(rs.getString("TARIMA"));
				normaEmbalajeDTO1.setDescripcionTarima(rs.getString("MAKTX2"));

				if (normaEmbalajeDTO1.getDescripcionTarima() == null
						|| (normaEmbalajeDTO1.getDescripcionTarima() != null
								&& normaEmbalajeDTO1.getDescripcionTarima().trim().equals(""))) {
					normaEmbalajeDTO1.setDescripcionTarima(rs.getString("MAKTX1"));
				}

				normaEmbalajeDTO1.setLetyp(rs.getString("PACKNR"));

				// Obtiene la cantidad de tarimas
				String query2 = "SELECT TRGQTY,BASEUNIT FROM PACKPO WHERE PAITEMTYPE = 'I' AND PACKNR = ? and MATNR=?";
				Object[] args2 = { normaEmbalajeDTO1.getLetyp(), matnr };

				jdbcTemplate.query(query2, args2, new RowMapper<NormaEmbalajeDTO>() {

					@Override
					public NormaEmbalajeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						String query3 = "SELECT distinct convert(int, POBJID) as POBJID from PACKKP WITH(NOLOCK) WHERE PACKNR = ?";
						Object[] args3 = { normaEmbalajeDTO1.getLetyp() };
						int cont = 0;

						jdbcTemplate.query(query3, args3, new RowMapper<String>() {

							@Override
							public String mapRow(ResultSet rs, int rowNum) throws SQLException {
								normaEmbalajeDTO1.setPobjid(rs.getString("POBJID"));

								return normaEmbalajeDTO1.getPobjid();
							}
						});

						if (cont != 0) {
							NormaEmbalajeDTO normaEmbalajeDTO2 = new NormaEmbalajeDTO();

							normaEmbalajeDTO2.setTarima(normaEmbalajeDTO1.getTarima());
							normaEmbalajeDTO2.setDescripcionTarima(normaEmbalajeDTO1.getDescripcionTarima());
							normaEmbalajeDTO2.setLetyp(normaEmbalajeDTO1.getLetyp());
							normaEmbalajeDTO2.setPobjid(normaEmbalajeDTO1.getPobjid());
							normaEmbalajeDTO2.setCantidad(rs.getString("TRGQTY"));
							normaEmbalajeDTO2.setUnidadMedida(rs.getString("BASEUNIT"));

							if (hashMap.get(normaEmbalajeDTO2.getTarima() + normaEmbalajeDTO2.getCantidad()
									+ normaEmbalajeDTO2.getUnidadMedida() + normaEmbalajeDTO2.getPobjid()) == null) {

								hashMap.put(
										normaEmbalajeDTO2.getTarima() + normaEmbalajeDTO2.getCantidad()
												+ normaEmbalajeDTO2.getUnidadMedida() + normaEmbalajeDTO2.getPobjid(),
										"1");

								listNormaEmbalajeDTO.add(normaEmbalajeDTO2);
							}
						} else {

							normaEmbalajeDTO1.setCantidad(rs.getString("TRGQTY"));
							normaEmbalajeDTO1.setUnidadMedida(rs.getString("BASEUNIT"));

							if (hashMap.get(normaEmbalajeDTO1.getTarima() + normaEmbalajeDTO1.getCantidad()
									+ normaEmbalajeDTO1.getUnidadMedida() + normaEmbalajeDTO1.getPobjid()) == null) {

								hashMap.put(
										normaEmbalajeDTO1.getTarima() + normaEmbalajeDTO1.getCantidad()
												+ normaEmbalajeDTO1.getUnidadMedida() + normaEmbalajeDTO1.getPobjid(),
										"1");

								listNormaEmbalajeDTO.add(normaEmbalajeDTO1);
							}

							cont++;
						}

						return null;
					}
				});

				return null;
			}
		});

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

		return normasEmbalajeDTO;
	}

	@Override
	public NormasEmbalajeDTO obtieneTarimas(String letyp, String legnum) {
		String query = "select * from  HCMDB.dbo.ZPAITT_TTW WITH(NOLOCK) where letyp = ? and LGNUM = ?";
		Object[] args = { letyp, legnum };

		List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();
		NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();
		NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
		ResultDTO resultDT = new ResultDTO();

		listNormaEmbalajeDTO = jdbcTemplate.query(query, args, new RowMapper<NormaEmbalajeDTO>() {

			@Override
			public NormaEmbalajeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				NormaEmbalajeDTO normaEmbalajeDTO1 = new NormaEmbalajeDTO();

				normaEmbalajeDTO1.setLetyp(rs.getString("LETYP"));
				normaEmbalajeDTO1.setTarima(rs.getString("MATNR"));
				normaEmbalajeDTO1.setDescripcionTarima(rs.getString("MAKTX"));

				return normaEmbalajeDTO1;
			}
		});

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

		return normasEmbalajeDTO;
	}
}
