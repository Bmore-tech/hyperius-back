package com.bmore.hyperius.web.repository.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.EmbarquePTRepository;
import com.bmore.hyperius.web.repository.HUsRepository;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.export.ExportacionDatasource;
import com.bmore.hyperius.web.utils.remission.Remision;
import com.bmore.hyperius.web.utils.remission.RemisionDatasource;

@Repository
public class EmbarquePTRepositoryImpl implements EmbarquePTRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ResultDTO cambiarCantidadOrdenProduccion(EmbarqueDetalleDTO embarqueDetalleDTO, String user, String werks) {
		String query1 = "exec sp_bcps_wm_alter_lfimg  ?,?,?,?,?,?";
		Object[] args1 = { embarqueDetalleDTO.getVbeln(), embarqueDetalleDTO.getMaterial(),
				embarqueDetalleDTO.getCajas(), werks, user, java.sql.Types.INTEGER };
		ResultDTO result = new ResultDTO();
		result.setId(0);

		result.setId(jdbcTemplate.update(query1, args1));

		if (result.getId() == 1) {
			String query2 = "exec sp_bcps_wm_alter_lfimg_registry  ?,?,?,?,?,?,?";
			Object[] args2 = { embarqueDetalleDTO.getVbeln(), embarqueDetalleDTO.getMaterial(),
					embarqueDetalleDTO.getCajas(), werks, user, embarqueDetalleDTO.getPosicion(),
					java.sql.Types.INTEGER };

			result.setId(jdbcTemplate.update(query2, args2));
		}

		return result;
	}

	@Override
	public ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput) {
		String query = "exec sp_bcps_wm_consume_hus ?,?,?,?,?,?,?,?,?,?";

		Object[] args = { entregaInput.getHu1(), entregaInput.getHu2(), entregaInput.getUsuarioMontacarga(),
				Utils.zeroFill(entregaInput.getEntrega(), 10), Utils.zeroFill(entregaInput.getMatnr(), 18),
				entregaInput.getWerks(), entregaInput.getuOrigen0(), entregaInput.getuOrigen1(),
				entregaInput.getuOrigen2(), java.sql.Types.INTEGER };

		ResultDTO result = new ResultDTO();
		result.setId(0);
		result.setId(jdbcTemplate.queryForObject(query, args, Integer.class));

		return result;
	}

	@Override
	public ResultDTO contabilizadoOK(String entry) {
		String query = "select * from HCMDB.dbo.zContingencia WITH(NOLOCK) where IDPROC = '9' and ENTREGA = ?";
		Object[] args = { entry };
		ResultDTO resultDT = new ResultDTO();

		jdbcTemplate.query(query, args, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rowNum > 0) {
					resultDT.setId(1);
					resultDT.setMsg("Entrega contabilizada");
				} else {
					resultDT.setId(0);
					resultDT.setMsg("Entrega aun no contabilizada");
				}
				return "";
			}
		});

		return resultDT;
	}

	@Override
	public ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user) {
		String query = "exec sp_bcps_wm_contabilizar_entrega_salida ?, ?, ?, ?, ?, ?, ?";
		Object[] args = { Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10), embarqueDTO.getWerks(), user,
				embarqueDTO.getVkorg(), embarqueDTO.getLfart(), 9, java.sql.Types.INTEGER };
		ResultDTO result = new ResultDTO();
		result.setId(0);

		result.setId(jdbcTemplate.queryForObject(query, args, Integer.class));

		return result;
	}

	@Override
	public ResultDTO contabilizaEntregaExport(EmbarqueDTO embarqueDTO, String user) {
		String query = "exec sp_bcps_wm_contabilizar_entrega_salida_vkorg  ?, ?, ?, ?, ?, ?, ?";
		Object[] args = { Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10), embarqueDTO.getWerks(), user,
				embarqueDTO.getVkorg(), embarqueDTO.getLfart(), 9, java.sql.Types.INTEGER };
		ResultDTO result = new ResultDTO();
		result.setId(0);

		result.setId(jdbcTemplate.queryForObject(query, args, Integer.class));

		return result;
	}

	@Override
	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z,
			HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado) {
		String query1 = "exec sp_bcps_get_carriles_por_material ?, ?, ?, ?, ?";
		Object[] args1 = { MATNR, WERKS, ID_PR, ID_PR_Z, 2 };

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();

		carrilList = jdbcTemplate.query(query1, args1, new RowMapper<CarrilUbicacionDTO>() {

			@Override
			public CarrilUbicacionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				CarrilUbicacionDTO item = new CarrilUbicacionDTO();
				if (carrilesBloqueados
						.get(rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim()) == null) {

					if (carrilesMaterialBloqueado.get(
							rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim()) == null) {
						item.setStatusS("0");
					} else {
						item.setStatusS("1");
					}

					item.setLgnum(rs.getString("LGNUM"));
					item.setLgtyp(rs.getString("LGTYP"));
					item.setLgpla(rs.getString("LGPLA"));
					item.setCantidadHus(rs.getString("TOTAL"));
					item.setMe(rs.getString("MEINS"));

					item.setCajas(rs.getString("VERME"));

					String query2 = "select count(*) from HCMDB.dbo.ZPickingEntregaEntrante with(nolock) "
							+ "where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='4' and (Status is null or Status ='1')  ";
					Object[] args2 = { item.getLgnum(), item.getLgtyp(), item.getLgpla() };

					Integer cantidad = jdbcTemplate.queryForObject(query2, args2, Integer.class);

					if (cantidad != null) {
						item.setHusPendientes(cantidad + "");
					}
				}
				return item;
			}
		});

		carrilesDTO.setItem(carrilList);

		return carrilesDTO;
	}

	@Override
	public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {
		String query = "select distinct(CARRIL) "
				+ "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";
		Object[] args = { idProceso, werks };

		HashMap<String, String> map = new HashMap<String, String>();

		jdbcTemplate.query(query, args, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				map.put(rs.getString("CARRIL").trim(), "");

				return "";
			}
		});

		return map;
	}

	@Override
	public HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks) {
		String query = "select distinct LGNUM, LGTYP, LGPLA from LQUA with(nolock) where "
				+ "MATNR=? and (BESTQ='S' or BESTQ='Q') and WERKS=?";
		Object[] args = { matnr, werks };

		HashMap<String, String> map = new HashMap<String, String>();

		jdbcTemplate.query(query, args, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				map.put(rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim(), "");

				return "";
			}
		});

		return map;
	}

	@Override
	public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {
		String query1 = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
		Object[] args1 = { hu, werks, lgtyp, lgpla };

		ResultDTO result = new ResultDTO();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();

		result.setId(2);
		result.setMsg("Material no encontrado.");

		jdbcTemplate.queryForObject(query1, args1, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				String query2 = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx, BESTQ as BESTQ"
						+ " from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR "
						+ " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
				Object[] args2 = { hu, werks, lgtyp, lgpla };

				jdbcTemplate.queryForObject(query2, args2, new RowMapper<Integer>() {

					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						result.setId(1);
						result.setMsg("Material encontrado");

						entrega.setMaterial(rs.getString("matnr"));
						entrega.setDescripcion(rs.getString("maktx"));
						entrega.setCajas(rs.getString("vemng"));
						entrega.setMe(rs.getString("meins"));
						entrega.setBestq(rs.getString("bestq"));

						return 1;
					}
				});

				return 1;
			}

		});
		entrega.setResultDT(result);

		return entrega;
	}

	@Override
	public EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput) {
		String query1 = "select DISTINCT(LIKP.VBELN) from HCMDB.dbo.LIKP LIKP with(nolock) "
				+ "inner join HCMDB.dbo.LIPS LIPS with(nolock) on LIKP.VBELN = LIPS.VBELN "
				+ "where LIKP.LFART != 'EL' and LIKP.VBELN=? and LIPS.WERKS=?";
		Object[] args1 = { embarqueDTOInput.getOrdenEmbarque(), embarqueDTOInput.getWerks() };

		EmbarqueDTO embarque = new EmbarqueDTO();
		ResultDTO result = new ResultDTO();

		result.setId(5);
		result.setMsg("La entrega no tiene datos de cabecera para mostrar");

		jdbcTemplate.queryForObject(query1, args1, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				String query2 = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.KUNNR,KNA1.NAME1,KNA1.NAME2, "
						+ "KNA1.ORT01, KNA1.PSTLZ,KNA1.STRAS,VKORG,LFART   from HCMDB.dbo.LIKP LIKP with(nolock) "
						+ "left outer join  HCMDB.dbo.KNA1 KNA1 on LIKP.KUNNR= KNA1.KUNNR  where VBELN=?";
				Object[] args2 = { embarqueDTOInput.getOrdenEmbarque() };

				jdbcTemplate.queryForObject(query2, args2, new RowMapper<Integer>() {

					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						embarque.setOrdenEmbarque(rs.getString("VBELN"));
						embarque.setFabrica(rs.getString("KUNNR"));
						embarque.setFechaDocumento(rs.getString("ERDAT"));
						embarque.setVkorg(rs.getString("VKORG"));

						String name1 = rs.getString("NAME1");
						String name2 = rs.getString("NAME2");
						String ort01 = rs.getString("ORT01");
						String pstlz = rs.getString("PSTLZ");
						String stras = rs.getString("STRAS");

						embarque.setFabricaDesc(name1 + " " + name2 + ", " + ort01 + ", " + pstlz + ", " + stras);
						embarque.setLfart(rs.getString("LFART"));

						result.setId(1);
						result.setMsg("Recuperacion de Datos de Cabecera Correcta");

						return 1;
					}
				});

				return 1;
			}
		});

		embarque.setResultDT(result);
		return embarque;
	}

	@Override
	public EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarque) {
		String query1 = "select count(POSNR) from HCMDB.dbo.LIPS with(nolock) where VBELN=?";
		Object[] args1 = { embarque.getOrdenEmbarque() };

		List<EmbarqueDetalleDTO> items = new ArrayList<EmbarqueDetalleDTO>();
		ResultDTO result = new ResultDTO();
		EmbarqueDetalleDTOItem embarqueDetalleDTOItem = new EmbarqueDetalleDTOItem();

		if (jdbcTemplate.queryForObject(query1, args1, Integer.class) >= 1) {
			String query2 = "select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN=? and "
					+ " PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4) and convert(decimal(18, 3), LFIMG) > 0 and LGORT!='TA01' and LGORT!='TA02'";
			Object[] args2 = { embarque.getOrdenEmbarque() };
			HashMap<String, String> map = new HashMap<String, String>();

			items = jdbcTemplate.query(query2, args2, new RowMapper<EmbarqueDetalleDTO>() {

				@Override
				public EmbarqueDetalleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					EmbarqueDetalleDTO item = new EmbarqueDetalleDTO();

					if (map.get(rs.getString("MATNR")) == null) {
						map.put(rs.getString("MATNR"), rs.getString("MATNR"));

						item.setMaterial(rs.getString("MATNR"));
						item.setPosicion(rs.getString("POSNR"));
						item.setDescripcion(rs.getString("ARKTX"));
						item.setCajas("0");

						String query3 = "select sum(convert(decimal(18, 3), LFIMG)), MEINS from HCMDB.dbo.lips WITH(NOLOCK) "
								+ "where VBELN = ? and MATNR = ? and PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4)  "
								+ "and convert(decimal(18, 3), LFIMG) > 0 group by MEINS";
						Object[] args3 = { embarque.getOrdenEmbarque(), item.getMaterial() };

						jdbcTemplate.queryForObject(query3, args3, new RowMapper<Integer>() {

							@Override
							public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
								try {
									item.setCajas(
											new BigDecimal(rs.getString(1)).setScale(3, RoundingMode.HALF_UP) + "");
								} catch (Exception e) {
									item.setCajas(rs.getString(1));
								}

								item.setMe(rs.getString(2));

								return null;
							}
						});

						item.setCajasAsignadas("0");

						String query4 = "select sum(cast(VERME as float))as cantidad from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM "
								+ "in(SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln =? and matnr=? and status='2' and EXIDV is not null)";
						Object[] args4 = { embarque.getOrdenEmbarque(), item.getMaterial() };

						String cantidad = jdbcTemplate.queryForObject(query4, args4, String.class);

						if (cantidad != null) {
							item.setCajasAsignadas((Float.parseFloat(cantidad)) + "");
						}

						result.setId(1);
						result.setMsg("Detalle de entrega encontrado");
					} else {
						result.setId(2);
						result.setMsg("Detalle de entrega NO encontrado.");
					}

					return item;
				}
			});

		}

		embarqueDetalleDTOItem.setItem(items);
		embarque.setItems(embarqueDetalleDTOItem);
		embarque.setResultDT(result);

		return embarque;
	}

	@Override
	public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {
		String query = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
				+ "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,'4',?,?)";

		ResultDTO resultDT = new ResultDTO();

		for (int x = 0; x < carrilesDTO.getItem().size(); x++) {
			BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());
			int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

			jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, VBELN);
					ps.setString(2, carrilesDTO.getItem().get(i).getLgnum());
					ps.setString(3, carrilesDTO.getItem().get(i).getLgtyp());
					ps.setString(4, carrilesDTO.getItem().get(i).getLgpla());
					ps.setString(5, carrilesDTO.getItem().get(i).getMaterial());
					ps.setString(6, user);
					ps.setString(7, werks);
					ps.setString(8, carrilesDTO.getItem().get(i).getLgnum() + carrilesDTO.getItem().get(i).getLgtyp()
							+ carrilesDTO.getItem().get(i).getLgpla());
				}

				@Override
				public int getBatchSize() {
					return ingresarZpicking;
				}
			});
		}

		resultDT.setId(1);
		resultDT.setMsg("Se registro la entrega saliente correctamete, mandar a montacarga");

		return resultDT;
	}

	@Override
	public int isRstAllowed(HuDTO huDTO) {
		String query = "SELECT COUNT(*) TOT FROM TB_BCPS_Z_HU_RST WHERE zRstAll = ? AND zIsRst = 1";
		Object[] args = { Base64
				.encodeBase64String((HUsRepository.RST_VALUE + huDTO.getWerks() + "_" + huDTO.getLfart()).getBytes()) };

		return jdbcTemplate.queryForObject(query, args, int.class);
	}

	@Override
	public ResultDTO limpiaPendientes(String vbeln) {
		String query = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and idProceso='4'";
		Object[] args = { vbeln };
		ResultDTO result = new ResultDTO();

		jdbcTemplate.update(query, args);
		result.setId(1);
		result.setMsg("Limpieza ejecutada con exito");

		return result;
	}

	@Override
	public ResultDTO limpiaPendientesXUsuario(String vbeln, String user) {
		String query = "update ZPickingEntregaEntrante set status = null, usuarioMontacarga = null "
				+ "where VBELN = ? and Status = '1' and usuarioMontacarga = ? ";
		Object[] args = { vbeln, user };
		ResultDTO result = new ResultDTO();

		jdbcTemplate.update(query, args);

		result.setId(1);
		result.setMsg("Limpieza ejecutada con exito");

		return result;
	}

	@Override
	public String obtenerCliente(String noEntrega, String num) {
		String query = "select KUNNR, VKORG from LIKP WITH(NOLOCK) where VBELN = ? ";
		Object[] args = { noEntrega };
		String queryResult = "VACIO";

		int column = Integer.parseInt(num);

		jdbcTemplate.query(query, args, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String queryResult = "VACIO";

				queryResult = rs.getString(column);

				return queryResult;
			}
		});

		return queryResult;
	}

	@Override
	public String obtenerDatosCliente(String noEntrega) {
		String query = "select ADRC.NAME1, ADRC.NAME2, ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1 "
				+ "from HCMDB.dbo.LIKP LIKP WITH(NOLOCK) INNER JOIN HCMDB.dbo.KNA1 KNA1 WITH(NOLOCK) on LIKP.KUNNR = KNA1.KUNNR "
				+ "INNER JOIN HCMDB.dbo.ADRC ADRC WITH(NOLOCK) on KNA1.ADRNR = ADRC.ADDRNUMBER "
				+ "where LIKP.VBELN = ? "
				+ "group by ADRC.NAME1, ADRC.NAME2, ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1";
		Object[] args = { noEntrega };
		ResultDTO result = new ResultDTO();

		jdbcTemplate.query(query, args, new RowMapper<String>() {
			String queryResult = "VACIO";
			String queryResult1 = "VACIO";
			String queryResult2 = "VACIO";
			String queryResult3 = "VACIO";
			String queryResult4 = "VACIO";
			String queryResult5 = "VACIO";
			String queryResult6 = "VACIO";
			String queryResult7 = "VACIO";

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				queryResult1 = rs.getString(1);
				queryResult2 = rs.getString(2);
				queryResult3 = rs.getString(3);
				queryResult4 = rs.getString(4);
				queryResult5 = rs.getString(5);
				queryResult6 = rs.getString(6);
				queryResult7 = rs.getString(7);

				queryResult = queryResult1 + " " + queryResult2 + " " + queryResult3 + " No. " + queryResult4 + " Col. "
						+ queryResult5 + " " + queryResult6 + " R.F.C. " + queryResult7;
				result.setMsg(queryResult);

				return queryResult;
			}
		});

		return result.getMsg();
	}

	@Override
	public String obtenerDirPlanta(String noEntrega) {
		String DIRPLANTA = "select ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1 "
				+ "from HCMDB.dbo.LIPS LIPS WITH(NOLOCK) "
				+ "INNER JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on LIPS.WERKS = ZCBC.WERKS "
				+ "INNER JOIN HCMDB.dbo.KNA1 KNA1 WITH(NOLOCK) on KNA1.KUNNR = ZCBC.KUNNR "
				+ "INNER JOIN HCMDB.dbo.ADRC ADRC WITH(NOLOCK) on KNA1.ADRNR = ADRC.ADDRNUMBER "
				+ "where LIPS.VBELN = ? "
				+ "group by ADRC.NAME1, ADRC.NAME2, ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1";
		Object[] args = { noEntrega };
		ResultDTO result = new ResultDTO();

		jdbcTemplate.query(DIRPLANTA, args, new RowMapper<String>() {
			String queryResult = "VACIO";
			String queryResult1 = "VACIO";
			String queryResult2 = "VACIO";
			String queryResult3 = "VACIO";
			String queryResult4 = "VACIO";
			String queryResult5 = "VACIO";

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				queryResult1 = rs.getString(1);
				queryResult2 = rs.getString(2);
				queryResult3 = rs.getString(3);
				queryResult4 = rs.getString(4);
				queryResult5 = rs.getString(5);

				queryResult = queryResult1 + " No. " + queryResult2 + " C.P. " + queryResult3 + " " + queryResult4
						+ " R.F.C. " + queryResult5;
				result.setMsg(queryResult);

				return queryResult;
			}
		});

		return result.getMsg();
	}

	@Override
	public String obtenerFecha() {
		String FECHA = "select convert(date, GETDATE())";

		return jdbcTemplate.queryForObject(FECHA, String.class);
	}

	@Override
	public String obtenerPlanta(String noEntrega) {
		String query = "select ADRC.NAME1, ADRC.NAME2 " + "from HCMDB.dbo.LIPS LIPS WITH(NOLOCK)"
				+ "INNER JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on LIPS.WERKS = ZCBC.WERKS "
				+ "INNER JOIN HCMDB.dbo.KNA1 KNA1 WITH(NOLOCK) on KNA1.KUNNR = ZCBC.KUNNR "
				+ "INNER JOIN HCMDB.dbo.ADRC ADRC WITH(NOLOCK) on KNA1.ADRNR = ADRC.ADDRNUMBER "
				+ "where LIPS.VBELN = ? ";
		Object[] args = { noEntrega };
		ResultDTO result = new ResultDTO();

		jdbcTemplate.query(query, args, new RowMapper<String>() {
			String queryResult = "VACIO";
			String queryResult1 = "VACIO";
			String queryResult2 = "VACIO";

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				queryResult1 = rs.getString(1);
				queryResult2 = rs.getString(2);

				queryResult = queryResult1 + queryResult2;
				result.setMsg(queryResult);

				return queryResult;
			}
		});

		return result.getMsg();
	}

	@Override
	public RemisionDatasource obtenerTabla(String noEntrega) {
		String query = "select MATNR, ARKTX, LFIMG from HCMDB.dbo.LIPS WITH(NOLOCK) where VBELN = ? AND "
				+ "PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4) and convert(decimal(18, 3), LFIMG) > 0";
		Object[] args = { noEntrega };

		RemisionDatasource datasource = new RemisionDatasource();

		jdbcTemplate.query(query, args, new RowMapper<Remision>() {

			@Override
			public Remision mapRow(ResultSet rs, int rowNum) throws SQLException {
				Remision r = new Remision();

				try {
					r.setClave(Integer.parseInt(rs.getString(1)) + "");
				} catch (Exception e) {
					r.setClave(rs.getString(1));
				}

				r.setDescripcion(rs.getString(2));
				r.setCantidad(rs.getString(3));

				datasource.addRemision(r);

				return r;
			}
		});

		return datasource;
	}

	@Override
	public ExportacionDatasource obtenerTablaExp(String noEntrega) {
		String query = "SELECT TOP 1 MATNR, BISMT, LFIMG, KBETR FROM VS_BCPS_REM_QUAN WHERE VBELN = ?";
		Object[] args = { noEntrega };
		ExportacionDatasource datasource = new ExportacionDatasource();

		jdbcTemplate.query(query, args, new RowMapper<Remision>() {

			@Override
			public Remision mapRow(ResultSet rs, int rowNum) throws SQLException {
				Remision r = new Remision();

				try {
					r.setClave(Integer.parseInt(rs.getString("MATNR")) + "");
				} catch (Exception e) {
					r.setClave(rs.getString("MATNR"));
				}

				r.setDescripcion(rs.getString("BISMT"));
				r.setCantidad(rs.getString("LFIMG"));
				r.setPrecioUnitario("$ " + rs.getString("KBETR"));
				datasource.addRemision(r);

				return r;
			}
		});

		return datasource;
	}

	@Override
	public EntregaInputDTO reservaUbicaciones(EntregaInputDTO entregaInput) {
		String query = "exec sp_bcps_wm_reserva_espacio_hu ?,?,?,?,?,?,?,?,?";
		Object[] args = { entregaInput.getWerks(), entregaInput.getUsuarioMontacarga(), entregaInput.getEntrega(), "4",
				java.sql.Types.INTEGER, java.sql.Types.VARCHAR, java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
				java.sql.Types.VARCHAR };

		ResultDTO result = new ResultDTO();
		result.setId(0);

		jdbcTemplate.queryForObject(query, args, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				result.setId(rs.getInt(5));
				entregaInput.setMatnr(Utils.zeroClean(rs.getString(6)));
				entregaInput.setuOrigen0(rs.getString(7));
				entregaInput.setuOrigen1(rs.getString(8));
				entregaInput.setuOrigen2(rs.getString(9));

				return 1;
			}
		});

		entregaInput.setResultDT(result);

		return entregaInput;
	}

	@Override
	public ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu) {
		String query = "select EXIDV from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where EXIDV = ? and VBELN = ?";
		Object[] args = { hu, Utils.zeroFill(entregaInput.getEntrega(), 10) };
		ResultDTO resultDT = new ResultDTO();

		resultDT.setId(1);
		resultDT.setMsg("HU sin confirmar");

		jdbcTemplate.queryForObject(query, args, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				resultDT.setId(2);
				resultDT.setMsg("El HU ya fue consumido");

				return 1;
			}
		});

		return resultDT;
	}

	@Override
	public EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput) {
		String query = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and WERKS = ?";
		Object[] args = { Utils.zeroFill(entregaInput.getEntrega(), 10), entregaInput.getWerks() };

		ResultDTO result = new ResultDTO();
		EntregaInputDTO entregaInputReturn = new EntregaInputDTO();
		HashMap<String, String> hashhMap = new HashMap<String, String>();
		Integer cont = 1;

		cont = jdbcTemplate.query(query, args, new RowMapper<Integer>() {
			int counterRow = 0;

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				counterRow++;

				hashhMap.put(rs.getString("MATNR"), rs.getString("MATNR"));
				result.setId(1);
				result.setMsg("Entrega saliente ya se encuentra en picking");

				return counterRow;
			}
		}).size();

		if (cont == 0) {
			result.setId(0);
			result.setMsg("Entrega saliente no disponible para picking");
		}

		entregaInputReturn.setResultDT(result);
		entregaInputReturn.setMateriales(hashhMap);

		return entregaInputReturn;
	}

	@Override
	public EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput) {
		String query = "select VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
				+ "where VBELN = ? and WERKS = ? and (Status is null or Status != '2')";
		Object[] args = { Utils.zeroFill(entregaInput.getEntrega(), 10), entregaInput.getWerks() };

		HashMap<String, String> hashhMap = new HashMap<String, String>();
		EntregaInputDTO entregaInputReturn = new EntregaInputDTO();
		ResultDTO result = new ResultDTO();

		result.setId(0);
		result.setMsg("Entrega saliente no disponible para picking");

		jdbcTemplate.queryForObject(query, args, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				result.setId(1);
				result.setMsg("Entrega saliente ya se encuentra en picking");

				return 1;
			}
		});

		entregaInputReturn.setResultDT(result);
		entregaInputReturn.setMateriales(hashhMap);

		return entregaInputReturn;
	}

}
