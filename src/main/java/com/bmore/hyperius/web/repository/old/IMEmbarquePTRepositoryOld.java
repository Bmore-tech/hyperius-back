package com.bmore.hyperius.web.repository.old;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class IMEmbarquePTRepositoryOld {
  
  @Autowired
  private DBConnection dbConnection;

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	static String SUMA_ZPICKING_ORDEN_PRODUCCION = "select sum(cast( cantidadExidv as decimal(9,3)))as cantidad from ZPickingVidrio where "
			+ "vbeln = ? and matnr= ? and EXIDV is not null";

	static String ENTRY_EXISTS = "select DISTINCT(LIKP.VBELN) from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "inner join HCMDB.dbo.LIPS LIPS with(nolock) on LIKP.VBELN = LIPS.VBELN "
			+ "where LIKP.LFART != 'EL' and LIKP.VBELN=? and LIPS.WERKS=?";

	static String ENTRY_RESULT = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.KUNNR,KNA1.NAME1,KNA1.NAME2, "
			+ "KNA1.ORT01, KNA1.PSTLZ,KNA1.STRAS,VKORG,LFART   from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "left outer join  HCMDB.dbo.KNA1 KNA1 on LIKP.KUNNR= KNA1.KUNNR  where VBELN=?";

	static String TOTAL_POS = "select count(POSNR) from HCMDB.dbo.LIPS with(nolock) where VBELN=?";

	static String POS_ENTRY = "select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN=? and "
			+ " PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4) and convert(decimal, LFIMG) > 0 and MATNR!='000000000002000164'";

	static String GET_CAJAS = "select sum(cast( LFIMG as decimal(9,3))) as LFIMG, MEINS from HCMDB.dbo.lips where VBELN = ? and MATNR =? group by LFIMG, MEINS";

	static String CONTABILIZADO = "select * from HCMDB.dbo.zContingencia where IDPROC = '29' and ENTREGA = ?";

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////777

	static String INGRESA_ZPICKING_VIDRIO = "INSERT INTO HCMDB.dbo.ZPickingVidrio"
			+ "(VBELN,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,cantidadExidv,um) values(?,?,getdate(),?,'4',?,?,?)";

	// HH
	static String VALIDA_PICK = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingVidrio where VBELN = ? and WERKS = ?";

	static String VALIDA_PICK_FINALIZADO = "select VBELN from HCMDB.dbo.ZPickingVidrio where VBELN = ? and WERKS = ? and (Status is null or Status != '2')";

	static String RESERVA_UBICACION_HU1 = "update top (1) HCMDB.dbo.ZPickingVidrio set usuarioMontacarga = ? , Status='1' where "
			+ "usuarioMontacarga is null and EXIDV is null and Status is null and VBELN = ?";

	static String RESERVA_UBICACION_HU2 = " update top (1) HCMDB.dbo.ZPickingVidrio set usuarioMontacarga = ?,  Status='1' where "
			+ "usuarioMontacarga is null and EXIDV is null and Status is null and VBELN = ? and LGNUM+LGTYP+LGPLA in "
			+ "(select LGNUM+LGTYP+LGPLA from HCMDB.dbo.ZPickingVidrio where Status='1' and  usuarioMontacarga = ?) ";

	static String OBTIENE_RESERVA_UBICACION_HU1 = "select * from HCMDB.dbo.ZPickingVidrio where usuarioMontacarga = ? and VBELN = ? and status = 1";

	static String VALIDA_PICKEO_PREVIO_HU = "select EXIDV from HCMDB.dbo.ZPickingVidrio where EXIDV = ? and VBELN = ?";

	static String GET_DATA_HU = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx "
			+ " from HCMDB.dbo.LQUA LQUA INNER JOIN HCMDB.dbo.MAKT MAKT on LQUA.MATNR = MAKT.MATNR "
			+ " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";

	static String GET_HU = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";

	static String CONFIRMA_HU_EN_ZPICKING = "update top (1) HCMDB.dbo.ZPickingVidrio set status = '2', EXIDV = ? where  usuarioMontacarga = ? "
			+ "and Status = '1' and EXIDV is null and VBELN = ?";

	static String INSERT_PROCESO_ZCONTINGENCIA_8 = "insert into HCMDB.dbo.zContingencia(IDPROC,FECHA,HORA,CENTRO,HU,ENTREGA,CONTROL_CALIDAD,USUARIO) "
			+ "select IDPROC=8, convert(date,getdate()), convert(time, getdate()), WERKS = ?, HU=?,ENTREGA = ?,CONTROL_CALIDAD=(select BESTQ from HCMDB.dbo.LQUA where LENUM = ?),USUARIO= ?";

	static String INSERT_PROCESO_ZCONTINGENCIA_9 = "insert into HCMDB.dbo.zContingencia(IDPROC, FECHA, HORA, CENTRO, ENTREGA,USUARIO) "
			+ "values(9,convert(date,getdate()), convert(time,getdate()), ?,?,?)";

	// Por cambios ABAP es necesario ingresar paso 12, unicamente cambia ID de 9
	// a 12
	static String INSERT_PROCESO_ZCONTINGENCIA_12 = "insert into HCMDB.dbo.zContingencia(IDPROC, FECHA, HORA, CENTRO, ENTREGA,USUARIO) "
			+ "values(12,convert(date,getdate()), convert(time,getdate()), ?,?,?)";

	static String VALIDA_INSERT_PROCESO_ZCONTINGENCIA_1 = "select IDPROC from HCMDB.dbo.zContingencia where IDPROC='1' and TRANSPORTE = ?";

	static String UPDATE_LQUA = "update HCMDB.dbo.LQUA set SKZUA ='X' where LENUM = ?";

	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingVidrio set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and usuarioMontacarga = ? ";

	static String LIMPIA_PENDIENTE = "update HCMDB.dbo.ZPickingVidrio set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and idProceso='4'";

	static String GENERAR_FACTURACION = "SELECT VKORG from HCMDB.dbo.ZPACTT_ALLOWBILL where VKORG = ? and LFART = ?";

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static String OBTIENE_ENTREGA_DE_TRANSPORTE = "select VBELN from HCMDB.dbo.VTTP where TKNUM = ?";

	static String STOCK = "select MATNR,VEMNG,VEMEH,count(*) as stock from HCMDB.dbo.VEPO "
			+ "where VELIN='1' and ((BESTQ != 'S') or (BESTQ is null)) and  VENUM in ( "
			+ "select venum from HCMDB.dbo.VEKP where WERKS = ? and HU_LGORT='X')	 "
			+ "and MATNR= ? group by  MATNR,VEMNG,VEMEH order by stock desc";

	static String PENDIENTES_POR_PICKEAR = "select count(MATNR) as cantidad from HCMDB.dbo.ZPickingVidrio where MATNR=? and werks=? and cantidadExidv=? and EXIDV is null";

	static String RESEVA_HUS_EMBARQUE = "exec sp_bcps_im_embarquePT_reserva_hu ?, ?,?,?, ?, ?, ?,?,?,?";

	static String OBTIENE_DESCRIPCION_MATERIAL = "select top (1) ARKTX from  HCMDB.dbo.LIPS where VBELN=? and MATNR=?";

	static String CONSUME_HUS = "exec sp_bcps_im_embarquePT_consume_hu ?, ?, ?,?, ?, ?, ?, ?";

	static String CONTABILIZAR_EMBARQUE_PT = "exec sp_bcps_wm_contabilizar_entrega_salida ?, ?, ?, ?, ?, ?, ?";

	public EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput) {

		EmbarqueDTO embarqueDTO = new EmbarqueDTO();
		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		PreparedStatement stmn4 = null;
		ResultSet rs = null;
		ResultSet rs4 = null;

		LOCATION.error("Entrega: " + embarqueDTOInput.getOrdenEmbarque());
		LOCATION.error("Entrega: " + embarqueDTOInput.getWerks());

		try {
			stmn = con.prepareStatement(ENTRY_EXISTS);
			stmn.setString(1, embarqueDTOInput.getOrdenEmbarque());
			stmn.setString(2, embarqueDTOInput.getWerks());
			rs = stmn.executeQuery();
			if (rs.next()) {
				LOCATION.error("existe entrega");
				stmn4 = con.prepareStatement(ENTRY_RESULT);
				stmn4.setString(1, embarqueDTOInput.getOrdenEmbarque());
				rs4 = stmn4.executeQuery();
				if (rs4.next()) {
					LOCATION.error("datos entrega");
					embarqueDTO.setOrdenEmbarque(rs4.getString("VBELN"));
					embarqueDTO.setFabrica(rs4.getString("KUNNR"));
					embarqueDTO.setFechaDocumento(rs4.getString("ERDAT"));
					embarqueDTO.setVkorg(rs4.getString("VKORG"));

					String name1 = rs4.getString("NAME1");
					String name2 = rs4.getString("NAME2");
					String ort01 = rs4.getString("ORT01");
					String pstlz = rs4.getString("PSTLZ");
					String stras = rs4.getString("STRAS");

					embarqueDTO.setFabricaDesc(name1 + " " + name2 + ", " + ort01 + ", " + pstlz + ", " + stras);

					embarqueDTO.setLfart(rs4.getString("LFART"));

					result.setId(1);
					result.setMsg("Recuperacion de Datos de Cabecera Correcta");
				} else {
					result.setId(5);
					result.setMsg("La entrega no tiene datos de cabecera para mostrar");
				}

			} else {
				LOCATION.error("Entrega no existe!!!!!!");
				result.setId(2);
				result.setMsg("Entrega no existe");
			}
		} catch (SQLException e) {
			LOCATION.error("SQLException: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("SQLException: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("SQLException: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		embarqueDTO.setResultDT(result);
		return embarqueDTO;
	}

	public EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarqueDTO) {

		ResultDTO result = new ResultDTO();
		EmbarqueDetalleDTOItem embarqueDetalleDTOItem = new EmbarqueDetalleDTOItem();

		List<EmbarqueDetalleDTO> items = new ArrayList<EmbarqueDetalleDTO>();

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;
		PreparedStatement stmn2 = null;
		PreparedStatement stmn3 = null;
		PreparedStatement stmn4 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;

		try {
			stmn = con.prepareStatement(TOTAL_POS);
			stmn.setString(1, embarqueDTO.getOrdenEmbarque());
			rs = stmn.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) >= 1) {
					LOCATION.error("tOTAL POSICONES:" + rs.getInt(1));
					stmn2 = con.prepareStatement(POS_ENTRY);
					stmn2.setString(1, embarqueDTO.getOrdenEmbarque());
					rs2 = stmn2.executeQuery();

					HashMap<String, String> map = new HashMap<String, String>();
					while (rs2.next()) {
						if (map.get(rs2.getString("MATNR")) == null) {

							map.put(rs2.getString("MATNR"), rs2.getString("MATNR"));

							EmbarqueDetalleDTO item = new EmbarqueDetalleDTO();
							item.setMaterial(rs2.getString("MATNR"));
							item.setPosicion(rs2.getString("POSNR"));
							item.setDescripcion(rs2.getString("ARKTX"));

							stmn4 = con.prepareStatement(GET_CAJAS);
							stmn4.setString(1, embarqueDTO.getOrdenEmbarque());
							stmn4.setString(2, item.getMaterial());

							rs4 = stmn4.executeQuery();

							if (rs4.next()) {

								try {
									item.setCajas(
											new BigDecimal(rs4.getString(1)).setScale(3, RoundingMode.HALF_UP) + "");
								} catch (Exception e) {
									item.setCajas(rs4.getString(1));
								}

								item.setMe(rs4.getString(2));
							} else {
								item.setCajas("0");
							}
							item.setCajasAsignadas("0");
							// /
							try {

								stmn3 = con.prepareStatement(SUMA_ZPICKING_ORDEN_PRODUCCION);

								stmn3.setString(1, embarqueDTO.getOrdenEmbarque());
								stmn3.setString(2, item.getMaterial());

								rs3 = stmn3.executeQuery();

								if (rs3.next()) {

									String cantidad = rs3.getString("cantidad");

									item.setCajasAsignadas((Float.parseFloat(cantidad)) + "");

								}

							} catch (Exception e) {
								LOCATION.error("Error en SUMA_ZPICKING_ORDEN_PRODUCCION: " + e);
							}
							//

							items.add(item);
							LOCATION.error("Item agregado");

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		embarqueDetalleDTOItem.setItem(items);
		embarqueDTO.setItems(embarqueDetalleDTOItem);
		embarqueDTO.setResultDT(result);

		return embarqueDTO;
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		return resultDT;
	}

	public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {

		LOCATION.error("ingresaDetalleEnvase DAO Embarque");
		Connection con = dbConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();

		PreparedStatement stmntX = null;

		try {

			LOCATION.error("ingresaDetalleEnvase DAO Embarque PT :" + VBELN);

			for (int x = 0; x < carrilesDTO.getItem().size(); x++) {

				BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());

				int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

				for (int y = 0; y < ingresarZpicking; y++) {

					stmntX = con.prepareStatement(INGRESA_ZPICKING_VIDRIO);

					stmntX.setString(1, VBELN);
					stmntX.setString(2, carrilesDTO.getItem().get(x).getMaterial());

					stmntX.setString(3, user);
					stmntX.setString(4, werks);
					stmntX.setString(5, carrilesDTO.getItem().get(x).getCajas());
					stmntX.setString(6, carrilesDTO.getItem().get(x).getMe());

					stmntX.executeUpdate();
				}

			}
			resultDT.setId(1);
			resultDT.setMsg("Se registro la entrega saliente correctamete, mandar a montacarga");

		} catch (SQLException e) {

			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return resultDT;

	}

	public EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput) {

		EntregaInputDTO entregaInputReturn = new EntregaInputDTO();
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> hashhMap = new HashMap<String, String>();
		try {
			stmn = con.prepareStatement(VALIDA_PICK);

			stmn.setString(1, Utils.zeroFill(entregaInput.getEntrega(), 10));
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		entregaInputReturn.setResultDT(result);
		entregaInputReturn.setMateriales(hashhMap);
		return entregaInputReturn;
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
				result.setMsg("Entrega saliente ya se encuentra en picking");

			} else {
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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		entregaInputReturn.setResultDT(result);
		entregaInputReturn.setMateriales(hashhMap);
		return entregaInputReturn;
	}

	public EntregaInputDTO obtieneReservaUbicacionHU1(EntregaInputDTO entregaInput) {

		Connection con = dbConnection.createConnection();

		EntregaInputDTO entregaInputReturn = new EntregaInputDTO();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet resultado = null;

		try {
			stmn = con.prepareStatement(OBTIENE_RESERVA_UBICACION_HU1);

			stmn.setString(1, entregaInput.getUsuarioMontacarga());
			stmn.setString(2, Utils.zeroFill(entregaInput.getEntrega(), 10));

			resultado = stmn.executeQuery();

			if (resultado.next()) {

				entregaInputReturn.setuOrigen0(resultado.getString("LGNUM"));
				entregaInputReturn.setuOrigen1(resultado.getString("LGTYP"));
				entregaInputReturn.setuOrigen2(resultado.getString("LGPLA"));

				resultDT.setId(1);
				resultDT.setMsg("Reserva de carril");

			} else {
				resultDT.setId(0);
				resultDT.setMsg("No hay más HU’s para pickear en esta orden");
			}

		} catch (SQLException e) {
			resultDT.setId(0);
			resultDT.setMsg("Error SQL: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(0);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		entregaInputReturn.setResultDT(resultDT);

		return entregaInputReturn;
	}

	public ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu) {

		Connection con = dbConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet resultado = null;

		try {

			stmn = con.prepareStatement(VALIDA_PICKEO_PREVIO_HU);

			stmn.setString(1, hu);
			stmn.setString(2, Utils.zeroFill(entregaInput.getEntrega(), 10));

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(0);
			}
		}

		return resultDT;
	}

	public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {

		ResultDTO result = new ResultDTO();
		EntregaDetalleDTO entrega = new EntregaDetalleDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;
		try {

			stmn = con.prepareStatement(GET_HU);

			stmn.setString(1, hu);
			stmn.setString(2, werks);
			stmn.setString(3, lgtyp);
			stmn.setString(4, lgpla);
			rs = stmn.executeQuery();
			if (rs.next()) {

				stmn2 = con.prepareStatement(GET_DATA_HU);

				stmn2.setString(1, hu);
				stmn2.setString(2, werks);
				stmn2.setString(3, lgtyp);
				stmn2.setString(4, lgpla);

				rs2 = stmn2.executeQuery();
				if (rs2.next()) {

					result.setId(1);
					result.setMsg("Material encontrado");

					entrega.setMaterial(rs2.getString("matnr"));
					entrega.setDescripcion(rs2.getString("maktx"));
					entrega.setCajas(rs2.getString("vemng"));
					entrega.setMe(rs2.getString("meins"));
				} else {
					result.setId(2);
					result.setMsg("Material no encontrado.");
				}

			} else {
				result.setId(2);
				result.setMsg("El HU no pertenece a la ubicacion.");
			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		entrega.setResultDT(result);

		return entrega;

	}

	public ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput, String hu) {

		return null;
	}

	public ResultDTO obtieneEntregaDeTransporte(String tknum) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		try {

			stmn2 = con.prepareStatement(OBTIENE_ENTREGA_DE_TRANSPORTE);

			stmn2.setString(1, tknum);

			rs2 = stmn2.executeQuery();
			if (rs2.next()) {

				result.setId(1);
				result.setMsg("Entrega encontrada");
				result.setTypeS(rs2.getString("VBELN"));

			} else {
				result.setId(2);
				result.setMsg("El transporte no tiene asociada una entrega");
			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return result;

	}

	public CarrilesUbicacionDTO getStock(String werks, String matnr) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;
		ResultSet rs4 = null;

		try {

			stmn2 = con.prepareStatement(STOCK);

			stmn2.setString(1, werks);
			stmn2.setString(2, matnr);

			rs2 = stmn2.executeQuery();

			while (rs2.next()) {
				LOCATION.error("Entre a buscar carriles rs2");
				CarrilUbicacionDTO item = new CarrilUbicacionDTO();

				item.setCajas(rs2.getString("VEMNG"));
				item.setMaterial(rs2.getString("MATNR"));
				item.setCantidadHus(rs2.getString("STOCK"));
				item.setMe(rs2.getString("VEMEH"));

				try {

					stmn2 = con.prepareStatement(PENDIENTES_POR_PICKEAR);

					stmn2.setString(1, matnr);
					stmn2.setString(2, werks);
					stmn2.setString(3, item.getCajas());

					rs4 = stmn2.executeQuery();

					if (rs4.next()) {
						int cantidad = rs4.getInt(1);

						item.setHusPendientes(cantidad + "");
					}

				} catch (Exception e) {
					LOCATION.error("Error: PENDIENTES_POR_PICKEAR " + e.toString());
				}
				LOCATION.error("Add item");
				carrilList.add(item);
				LOCATION.error("Add item Hecho");
			}
			carrilesDTO.setItem(carrilList);

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return carrilesDTO;

	}

	// ///////////////////////

	public ResultDTO reservaHus(EntregaInputDTO entregaInput) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

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

			LOCATION.error("id: " + id + " material:" + material + "  cantidadTotal: " + cantidadTotal
					+ " cantidadPickeada: " + cantidadPickeada);
			result.setId(id);
			if (id == 1) {

				result.setTypeS(material);
				result.setTypeF(Float.parseFloat(cantidad));
				result.setMsg(um);
				BigDecimal cantidadTotalD = new BigDecimal("0.00");
				BigDecimal cantidadTotalZPickingD = new BigDecimal("0.00");

				try {

					cantidadTotalD = new BigDecimal(cantidadTotal).setScale(3, RoundingMode.HALF_UP);

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception: " + e.toString());
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}

		return result;

	}

	public ResultDTO obtieneDescripcionMaterial(String matnr, String vblen) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return result;

	}

	public ResultDTO consumeHUs(EntregaInputDTO entregaInput) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			// exec sp_vidrio_embarquePT_consume_hu @EXIDV, @WERKS, @MATNR,
			// @VEMNG, @SKIPBESTQ, @USER, @VBLEN, @RESULT

			callableStatement = con.prepareCall(CONSUME_HUS);

			callableStatement.setString(1, entregaInput.getHu1());
			callableStatement.setString(2, entregaInput.getWerks());
			callableStatement.setString(3, Utils.zeroFill(entregaInput.getMatnr(), 18));
			callableStatement.setString(4, entregaInput.getCant());
			callableStatement.setString(5, entregaInput.getHu2());
			callableStatement.setString(6, entregaInput.getUsuarioMontacarga());
			callableStatement.setString(7, entregaInput.getEntrega());

			callableStatement.registerOutParameter(8, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;

			id = callableStatement.getInt(8);
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

	public ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			// exec sp_vidrio_embarquePT_consume_hu @EXIDV, @WERKS, @MATNR,
			// @VEMNG, @SKIPBESTQ, @USER, @VBLEN, @RESULT

			// exec SP_CONTABILIZAR_ENTREGA_ENTRANTE_EMBARQUE_PT @EMBARQUE,
			// @WERKS, @USER, @VKORG, @LFART, @RETURN

			callableStatement = con.prepareCall(CONTABILIZAR_EMBARQUE_PT);

			callableStatement.setString(1, Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10));
			callableStatement.setString(2, embarqueDTO.getWerks());
			callableStatement.setString(3, user);
			callableStatement.setString(4, embarqueDTO.getVkorg());
			callableStatement.setString(5, embarqueDTO.getLfart());
			callableStatement.setInt(6, 29);

			callableStatement.registerOutParameter(7, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;

			id = callableStatement.getInt(7);
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
}
