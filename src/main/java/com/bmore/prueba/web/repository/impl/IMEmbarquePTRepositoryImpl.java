package com.bmore.prueba.web.repository.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.prueba.web.dto.CarrilUbicacionDTO;
import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.EmbarqueDTO;
import com.bmore.prueba.web.dto.EmbarqueDetalleDTO;
import com.bmore.prueba.web.dto.EmbarqueDetalleDTOItem;
import com.bmore.prueba.web.dto.EntregaDetalleDTO;
import com.bmore.prueba.web.dto.EntregaInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.IMEmbarquePTRepository;
import com.bmore.prueba.web.utils.Utils;

@Repository
public class IMEmbarquePTRepositoryImpl implements IMEmbarquePTRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput, String hu) {
		return null;
	}

	@Override
	public ResultDTO consumeHUs(EntregaInputDTO entregaInput) {
		String query = "exec sp_bcps_im_embarquePT_consume_hu ?, ?, ?,?, ?, ?, ?, ?";
		Object[] args = { entregaInput.getHu1(), entregaInput.getWerks(), Utils.zeroFill(entregaInput.getMatnr(), 18),
				entregaInput.getCant(), entregaInput.getHu2(), entregaInput.getUsuarioMontacarga(),
				entregaInput.getEntrega(), java.sql.Types.INTEGER };

		ResultDTO result = new ResultDTO();

		result.setId(0);
		result.setId(jdbcTemplate.update(query, args));

		return result;
	}

	@Override
	public ResultDTO contabilizadoOK(String entry) {
		String query = "select * from HCMDB.dbo.zContingencia where IDPROC = '29' and ENTREGA = ?";
		Object[] args = { entry };

		ResultDTO resultDT = new ResultDTO();
		resultDT.setId(0);
		resultDT.setMsg("Entrega aun no contabilizada");

		jdbcTemplate.queryForObject(query, args, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				resultDT.setId(1);
				resultDT.setMsg("Entrega contabilizada");

				return 1;
			}
		});

		return resultDT;
	}

	@Override
	public ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user) {
		String query = "exec sp_bcps_wm_contabilizar_entrega_salida ?, ?, ?, ?, ?, ?, ?";
		Object[] args = { Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10), embarqueDTO.getWerks(), user,
				embarqueDTO.getVkorg(), embarqueDTO.getLfart(), 29, java.sql.Types.INTEGER };

		ResultDTO result = new ResultDTO();
		result.setId(0);

		int id = jdbcTemplate.update(query, args);
		result.setId(id);

		return result;
	}

	@Override
	public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {
		String query1 = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
		Object[] args1 = { hu, werks, lgtyp, lgpla };

		ResultDTO result = new ResultDTO();
		result.setId(2);
		result.setMsg("Material no encontrado.");

		EntregaDetalleDTO entrega = new EntregaDetalleDTO();

		int respuesta = jdbcTemplate.queryForObject(query1, args1, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return 1;
			}
		});

		if (respuesta == 1) {
			String query2 = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx "
					+ " from HCMDB.dbo.LQUA LQUA INNER JOIN HCMDB.dbo.MAKT MAKT on LQUA.MATNR = MAKT.MATNR "
					+ " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
			Object[] args2 = { hu, werks, lgtyp, lgpla };

			entrega = jdbcTemplate.queryForObject(query2, args2, new RowMapper<EntregaDetalleDTO>() {

				@Override
				public EntregaDetalleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					EntregaDetalleDTO entrega = new EntregaDetalleDTO();

					entrega.setMaterial(rs.getString("matnr"));
					entrega.setDescripcion(rs.getString("maktx"));
					entrega.setCajas(rs.getString("vemng"));
					entrega.setMe(rs.getString("meins"));

					result.setId(1);
					result.setMsg("Material encontrado");

					return entrega;
				}
			});
		} else {
			result.setId(2);
			result.setMsg("El HU no pertenece a la ubicacion.");
		}

		entrega.setResultDT(result);

		return entrega;
	}

	@Override
	public EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput) {
		String query1 = "select DISTINCT(LIKP.VBELN) from HCMDB.dbo.LIKP LIKP with(nolock) "
				+ "inner join HCMDB.dbo.LIPS LIPS with(nolock) on LIKP.VBELN = LIPS.VBELN "
				+ "where LIKP.LFART != 'EL' and LIKP.VBELN=? and LIPS.WERKS=?";
		Object[] args1 = { embarqueDTOInput.getOrdenEmbarque(), embarqueDTOInput.getWerks() };

		EmbarqueDTO embarqueDTO = new EmbarqueDTO();
		ResultDTO result = new ResultDTO();
		result.setId(5);
		result.setMsg("La entrega no tiene datos de cabecera para mostrar");

		embarqueDTO = jdbcTemplate.queryForObject(query1, args1, new RowMapper<EmbarqueDTO>() {

			@Override
			public EmbarqueDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				String query2 = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.KUNNR,KNA1.NAME1,KNA1.NAME2, "
						+ "KNA1.ORT01, KNA1.PSTLZ,KNA1.STRAS,VKORG,LFART   from HCMDB.dbo.LIKP LIKP with(nolock) "
						+ "left outer join  HCMDB.dbo.KNA1 KNA1 on LIKP.KUNNR= KNA1.KUNNR  where VBELN=?";
				Object[] args2 = { embarqueDTOInput.getOrdenEmbarque() };

				return jdbcTemplate.queryForObject(query2, args2, new RowMapper<EmbarqueDTO>() {

					@Override
					public EmbarqueDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						EmbarqueDTO embarqueDTO = new EmbarqueDTO();

						embarqueDTO.setOrdenEmbarque(rs.getString("VBELN"));
						embarqueDTO.setFabrica(rs.getString("KUNNR"));
						embarqueDTO.setFechaDocumento(rs.getString("ERDAT"));
						embarqueDTO.setVkorg(rs.getString("VKORG"));

						String name1 = rs.getString("NAME1");
						String name2 = rs.getString("NAME2");
						String ort01 = rs.getString("ORT01");
						String pstlz = rs.getString("PSTLZ");
						String stras = rs.getString("STRAS");

						embarqueDTO.setFabricaDesc(name1 + " " + name2 + ", " + ort01 + ", " + pstlz + ", " + stras);
						embarqueDTO.setLfart(rs.getString("LFART"));

						result.setId(1);
						result.setMsg("Recuperacion de Datos de Cabecera Correcta");

						return embarqueDTO;
					}
				});
			}
		});

		embarqueDTO.setResultDT(result);

		return embarqueDTO;
	}

	@Override
	public EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarqueDTO) {
		String query1 = "select count(POSNR) from HCMDB.dbo.LIPS with(nolock) where VBELN=?";
		Object[] args1 = { embarqueDTO.getOrdenEmbarque() };

		ResultDTO result = new ResultDTO();
		EmbarqueDetalleDTOItem embarqueDetalleDTOItem = new EmbarqueDetalleDTOItem();
		List<EmbarqueDetalleDTO> items = new ArrayList<EmbarqueDetalleDTO>();

		result.setId(2);
		result.setMsg("Detalle de entrega NO encontrado.");

		jdbcTemplate.queryForObject(query1, args1, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				String query2 = "select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN=? and "
						+ " PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4) and convert(decimal, LFIMG) > 0 and MATNR!='000000000002000164'";
				Object[] args2 = { embarqueDTO.getOrdenEmbarque() };

				HashMap<String, String> map = new HashMap<String, String>();

				jdbcTemplate.query(query2, args2, new RowMapper<Integer>() {

					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						if (map.get(rs.getString("MATNR")) == null) {
							map.put(rs.getString("MATNR"), rs.getString("MATNR"));

							EmbarqueDetalleDTO item = new EmbarqueDetalleDTO();
							item.setMaterial(rs.getString("MATNR"));
							item.setPosicion(rs.getString("POSNR"));
							item.setDescripcion(rs.getString("ARKTX"));

							String query3 = "select sum(cast( LFIMG as decimal(9,3))) as LFIMG, MEINS from HCMDB.dbo.lips where VBELN = ? and MATNR =? group by LFIMG, MEINS";
							Object[] args3 = { embarqueDTO.getOrdenEmbarque(), item.getMaterial() };

							item.setCajas("0");

							jdbcTemplate.queryForObject(query3, args3, new RowMapper<Integer>() {

								@Override
								public Integer mapRow(ResultSet rs, int rowNum) {
									String cajas = "";

									try {
										cajas = rs.getString(1);
										item.setCajas(new BigDecimal(cajas).setScale(3, RoundingMode.HALF_UP) + "");
										item.setMe(rs.getString(2));
									} catch (SQLException e) {
										item.setCajas(cajas);
										e.printStackTrace();
									}

									return 1;
								}
							});

							item.setCajasAsignadas("0");

							String query4 = "select sum(cast( cantidadExidv as decimal(9,3)))as cantidad from ZPickingVidrio where "
									+ "vbeln = ? and matnr= ? and EXIDV is not null";
							Object[] args4 = { embarqueDTO.getOrdenEmbarque(), item.getMaterial() };

							jdbcTemplate.queryForObject(query4, args4, new RowMapper<Integer>() {

								@Override
								public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
									String cantidad = rs.getString("cantidad");
									item.setCajasAsignadas((Float.parseFloat(cantidad)) + "");

									return 1;
								}

							});

							result.setId(1);
							result.setMsg("Detalle de entrega encontrado.");
						} else {
							result.setId(2);
							result.setMsg("Detalle de entrega NO encontrado.");
						}

						return 1;
					}

				});

				return 1;
			}
		});

		embarqueDetalleDTOItem.setItem(items);
		embarqueDTO.setItems(embarqueDetalleDTOItem);
		embarqueDTO.setResultDT(result);

		return embarqueDTO;
	}

	@Override
	public CarrilesUbicacionDTO getStock(String werks, String matnr) {
		String query1 = "select MATNR,VEMNG,VEMEH,count(*) as stock from HCMDB.dbo.VEPO "
				+ "where VELIN='1' and ((BESTQ != 'S') or (BESTQ is null)) and  VENUM in ( "
				+ "select venum from HCMDB.dbo.VEKP where WERKS = ? and HU_LGORT='X')	 "
				+ "and MATNR= ? group by  MATNR,VEMNG,VEMEH order by stock desc";
		Object[] args1 = { werks, matnr };

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();

		carrilList = jdbcTemplate.query(query1, args1, new RowMapper<CarrilUbicacionDTO>() {

			@Override
			public CarrilUbicacionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				CarrilUbicacionDTO item = new CarrilUbicacionDTO();

				item.setCajas(rs.getString("VEMNG"));
				item.setMaterial(rs.getString("MATNR"));
				item.setCantidadHus(rs.getString("STOCK"));
				item.setMe(rs.getString("VEMEH"));

				String query2 = "select count(MATNR) as cantidad from HCMDB.dbo.ZPickingVidrio where MATNR=? and werks=? and cantidadExidv=? and EXIDV is null";
				Object[] args2 = { matnr, werks, item.getCajas() };

				jdbcTemplate.queryForObject(query2, args2, new RowMapper<Integer>() {

					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						int cantidad = rs.getInt(1);
						item.setHusPendientes(cantidad + "");

						return 1;
					}
				});

				return item;
			}

		});

		carrilesDTO.setItem(carrilList);

		return carrilesDTO;
	}

	@Override
	public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {
		String query = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante (VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,?,?,?)";
		ResultDTO resultDT = new ResultDTO();

		for (int x = 0; x < carrilesDTO.getItem().size(); x++) {
			BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());
			int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

			Object[] args = { VBELN, carrilesDTO.getItem().get(x).getMaterial(), user, werks,
					carrilesDTO.getItem().get(x).getCajas(), carrilesDTO.getItem().get(x).getMe() };

			for (int y = 0; y < ingresarZpicking; y++) {
				resultDT.setId(jdbcTemplate.update(query, args));
			}
		}

		resultDT.setMsg("Se registro la entrega saliente correctamete, mandar a montacarga");

		return resultDT;
	}

	@Override
	public ResultDTO obtieneDescripcionMaterial(String matnr, String vblen) {
		String query = "select top (1) ARKTX from  HCMDB.dbo.LIPS where VBELN=? and MATNR=?";
		

		return null;
	}

	@Override
	public ResultDTO obtieneEntregaDeTransporte(String tknum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntregaInputDTO obtieneReservaUbicacionHU1(EntregaInputDTO entregaInput) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultDTO reservaHus(EntregaInputDTO entregaInput) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput) {
		// TODO Auto-generated method stub
		return null;
	}

}
