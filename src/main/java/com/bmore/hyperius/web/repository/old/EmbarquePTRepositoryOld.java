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

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.HUsRepository;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.export.ExportacionDatasource;
import com.bmore.hyperius.web.utils.remission.Remision;
import com.bmore.hyperius.web.utils.remission.RemisionDatasource;

public class EmbarquePTRepositoryOld {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	static String SUMA_ZPICKING_ORDEN_PRODUCCION = "select sum(cast(VERME as float))as cantidad from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM "
			+ "in(SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln =? and matnr=? and status='2' and EXIDV is not null)";

	static String ENTRY_EXISTS = "select DISTINCT(LIKP.VBELN) from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "inner join HCMDB.dbo.LIPS LIPS with(nolock) on LIKP.VBELN = LIPS.VBELN "
			+ "where LIKP.LFART != 'EL' and LIKP.VBELN=? and LIPS.WERKS=?";

	static String REST_ALLOWED = "SELECT COUNT(*) TOT FROM TB_BCPS_Z_HU_RST WHERE zRstAll = ? AND zIsRst = 1";

	static String ENTRY_RESULT = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.KUNNR,KNA1.NAME1,KNA1.NAME2, "
			+ "KNA1.ORT01, KNA1.PSTLZ,KNA1.STRAS,VKORG,LFART   from HCMDB.dbo.LIKP LIKP with(nolock) "
			+ "left outer join  HCMDB.dbo.KNA1 KNA1 on LIKP.KUNNR= KNA1.KUNNR  where VBELN=?";

	static String TOTAL_POS = "select count(POSNR) from HCMDB.dbo.LIPS with(nolock) where VBELN=?";

	static String POS_ENTRY = "select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN=? and "
			+ " PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4) and convert(decimal(18, 3), LFIMG) > 0 and LGORT!='TA01' and LGORT!='TA02'";

	static String PENDIENTES_POR_PICKEAR_POR_CARRIL = "select count(*) from HCMDB.dbo.ZPickingEntregaEntrante with(nolock) where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='4' and (Status is null or Status ='1')  ";

	static String GET_CAJAS = "select sum(convert(decimal(18, 3), LFIMG)), MEINS from HCMDB.dbo.lips WITH(NOLOCK) "
			+ "where VBELN = ? and MATNR = ? and PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4)  "
			+ "and convert(decimal(18, 3), LFIMG) > 0 group by MEINS";

	static final String TABLA = "select MATNR, ARKTX, LFIMG from HCMDB.dbo.LIPS WITH(NOLOCK) where VBELN = ? AND "
			+ "PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 4) and convert(decimal(18, 3), LFIMG) > 0";

	static final String getDataLIPS = "SELECT TOP 1 MATNR, BISMT, LFIMG, KBETR FROM VS_BCPS_REM_QUAN WHERE VBELN = ?";

	static final String PLANTA = "select ADRC.NAME1, ADRC.NAME2 " + "from HCMDB.dbo.LIPS LIPS WITH(NOLOCK)"
			+ "INNER JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on LIPS.WERKS = ZCBC.WERKS "
			+ "INNER JOIN HCMDB.dbo.KNA1 KNA1 WITH(NOLOCK) on KNA1.KUNNR = ZCBC.KUNNR "
			+ "INNER JOIN HCMDB.dbo.ADRC ADRC WITH(NOLOCK) on KNA1.ADRNR = ADRC.ADDRNUMBER " + "where LIPS.VBELN = ? ";

	static final String DIRPLANTA = "select ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1 "
			+ "from HCMDB.dbo.LIPS LIPS WITH(NOLOCK) "
			+ "INNER JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on LIPS.WERKS = ZCBC.WERKS "
			+ "INNER JOIN HCMDB.dbo.KNA1 KNA1 WITH(NOLOCK) on KNA1.KUNNR = ZCBC.KUNNR "
			+ "INNER JOIN HCMDB.dbo.ADRC ADRC WITH(NOLOCK) on KNA1.ADRNR = ADRC.ADDRNUMBER " + "where LIPS.VBELN = ? "
			+ "group by ADRC.NAME1, ADRC.NAME2, ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1";

	static final String CLIENTE = "select KUNNR, VKORG from HCMDB.dbo.LIKP WITH(NOLOCK) where VBELN = ? ";

	static final String DATOSCLIENTE = "select ADRC.NAME1, ADRC.NAME2, ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1 "
			+ "from HCMDB.dbo.LIKP LIKP WITH(NOLOCK) INNER JOIN HCMDB.dbo.KNA1 KNA1 WITH(NOLOCK) on LIKP.KUNNR = KNA1.KUNNR "
			+ "INNER JOIN HCMDB.dbo.ADRC ADRC WITH(NOLOCK) on KNA1.ADRNR = ADRC.ADDRNUMBER " + "where LIKP.VBELN = ? "
			+ "group by ADRC.NAME1, ADRC.NAME2, ADRC.STREET, ADRC.HOUSE_NUM1, ADRC.POST_CODE1, ADRC.CITY1, KNA1.STCD1";

	static final String FECHA = "select convert(date, GETDATE())";

	static String GETCARRILES_EMBARQUE_PT = "exec sp_bcps_get_carriles_por_material ?,?,?,?,?";

	static String GET_CARRILES_EMBARQUE_PT_CON_MATERIAL_BLOQUEADO = "select distinct LGNUM, LGTYP, LGPLA from LQUA with(nolock) where "
			+ "MATNR=? and (BESTQ='S' or BESTQ='Q') and WERKS=?";

	static String GET_CARRILES_BLOQUEADOS = "select distinct(CARRIL) "
			+ "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";

	// static String GETCARRILES_EMBARQUE_PT_BLOQUEADOS =
	// "exec sp_bcps_get_carriles_por_material_bloqueados ?,?,?,?";

	static String CONTABILIZADO = "select * from HCMDB.dbo.zContingencia WITH(NOLOCK) where IDPROC = '9' and ENTREGA = ?";

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////777

	static String INGRESA_ZPICKING = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
			+ "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,'4',?,?)";

	static String INGRESA_ZPICKING_RESTO = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
			+ "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril,status,exidv) values(?,?,?,?,?,getdate(),?,'4',?,?,3,?)";

	static String INGRESA_HU_RESTO_PIV_OBJ = "INSERT INTO HCMDB.dbo.LQUA_PIV_ZPICK (VBELN, EXIDV, VERME) values (?,?,?) ";

	// HH
	static String VALIDA_PICK = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and WERKS = ?";

	static String VALIDA_PICK_FINALIZADO = "select VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where VBELN = ? and WERKS = ? and (Status is null or Status != '2')";

	static String VALIDA_PICKEO_PREVIO_HU = "select EXIDV from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where EXIDV = ? and VBELN = ?";

	static String GET_DATA_HU = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx, BESTQ as BESTQ"
			+ " from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR "
			+ " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";

	static String GET_HU = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";

	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and usuarioMontacarga = ? ";

	static String LIMPIA_PENDIENTE = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and idProceso='4'";

	static String CONTABILIZAR_EMBARQUE_PT = "exec sp_bcps_wm_contabilizar_entrega_salida ?, ?, ?, ?, ?, ?, ?";

	static String CONTABILIZAR_EMBARQUE_PT_EXPORT = "exec sp_bcps_wm_contabilizar_entrega_salida_vkorg  ?, ?, ?, ?, ?, ?, ?";

	static String RESERVA_ESPACIO_HU = "exec sp_bcps_wm_reserva_espacio_hu ?,?,?,?,?,?,?,?,?";

	static String CONSUME_HUS = "exec sp_bcps_wm_consume_hus ?,?,?,?,?,?,?,?,?,?";

	static String CAMBIA_CANTIDAD_ORDEN = "exec sp_bcps_wm_alter_lfimg  ?,?,?,?,?,?";

	static String CAMBIA_CANTIDAD_ORDEN_ZCONT = "exec sp_bcps_wm_alter_lfimg_registry  ?,?,?,?,?,?,?";

	public EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput) {

		EmbarqueDTO embarque = new EmbarqueDTO();
		ResultDTO result = new ResultDTO();

		Connection con = new DBConnection().createConnection();

		PreparedStatement stmn = null;
		PreparedStatement stmn4 = null;
		ResultSet rs = null;
		ResultSet rs4 = null;

		try {
			stmn = con.prepareStatement(ENTRY_EXISTS);
			stmn.setString(1, embarqueDTOInput.getOrdenEmbarque());
			stmn.setString(2, embarqueDTOInput.getWerks());
			rs = stmn.executeQuery();
			if (rs.next()) {

				stmn4 = con.prepareStatement(ENTRY_RESULT);
				stmn4.setString(1, embarqueDTOInput.getOrdenEmbarque());
				rs4 = stmn4.executeQuery();
				if (rs4.next()) {

					embarque.setOrdenEmbarque(rs4.getString("VBELN"));
					embarque.setFabrica(rs4.getString("KUNNR"));
					embarque.setFechaDocumento(rs4.getString("ERDAT"));
					embarque.setVkorg(rs4.getString("VKORG"));

					String name1 = rs4.getString("NAME1");
					String name2 = rs4.getString("NAME2");
					String ort01 = rs4.getString("ORT01");
					String pstlz = rs4.getString("PSTLZ");
					String stras = rs4.getString("STRAS");

					embarque.setFabricaDesc(name1 + " " + name2 + ", " + ort01 + ", " + pstlz + ", " + stras);

					embarque.setLfart(rs4.getString("LFART"));

					result.setId(1);
					result.setMsg("Recuperacion de Datos de Cabecera Correcta");
				} else {
					result.setId(5);
					result.setMsg("La entrega no tiene datos de cabecera para mostrar");
				}

			} else {
				result.setId(2);
				result.setMsg("Entrega no existe");
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

		embarque.setResultDT(result);
		return embarque;
	}

	public EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarque) {

		ResultDTO result = new ResultDTO();
		EmbarqueDetalleDTOItem embarqueDetalleDTOItem = new EmbarqueDetalleDTOItem();

		List<EmbarqueDetalleDTO> items = new ArrayList<EmbarqueDetalleDTO>();

		Connection con = new DBConnection().createConnection();

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
			stmn.setString(1, embarque.getOrdenEmbarque());
			rs = stmn.executeQuery();

			if (rs.next()) {
				if (rs.getInt(1) >= 1) {
					logger.error("tOTAL POSICONES:" + rs.getInt(1));
					stmn2 = con.prepareStatement(POS_ENTRY);
					stmn2.setString(1, embarque.getOrdenEmbarque());
					rs2 = stmn2.executeQuery();

					HashMap<String, String> map = new HashMap<String, String>();

					while (rs2.next()) {

						if (map.get(rs2.getString("MATNR")) == null) {
							map.put(rs2.getString("MATNR"), rs2.getString("MATNR"));

							EmbarqueDetalleDTO item = new EmbarqueDetalleDTO();
							item.setMaterial(rs2.getString("MATNR"));
							item.setPosicion(rs2.getString("POSNR"));
							item.setDescripcion(rs2.getString("ARKTX"));
							item.setCajas("0");

							logger.error("GET CAJAS");
							stmn4 = con.prepareStatement(GET_CAJAS);
							stmn4.setString(1, embarque.getOrdenEmbarque());
							stmn4.setString(2, item.getMaterial());

							logger.error("MATERIAL ->>>>>>>>>>>>>>: " + item.getMaterial());

							rs4 = stmn4.executeQuery();

							logger.error("stmn4.executeQuery() ->>>>>>>>>>>>>>: " + item.getMaterial());

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

							logger.error("Cajas asignadas");

							item.setCajasAsignadas("0");
							// /
							try {

								stmn3 = con.prepareStatement(SUMA_ZPICKING_ORDEN_PRODUCCION);

								logger.error("SUMA_ZPICKING_ORDEN_PRODUCCION ");

								stmn3.setString(1, embarque.getOrdenEmbarque());
								stmn3.setString(2, item.getMaterial());

								rs3 = stmn3.executeQuery();

								if (rs3.next()) {

									String cantidad = rs3.getString("cantidad");

									item.setCajasAsignadas((Float.parseFloat(cantidad)) + "");

								}

								logger.error("SUMA_ZPICKING_ORDEN_PRODUCCION ");

							} catch (Exception e) {
								logger.error("Error en SUMA_ZPICKING_ORDEN_PRODUCCION: " + e);
							}

							//
							logger.error("->>>>>>>>>>>>>>>>>>>>>>>>>>>Antes Item agregado");
							items.add(item);
							logger.error("Item agregado");

						}
					}

					result.setId(1);
					result.setMsg("Detalle de entrega encontrado");

				} else {
					result.setId(2);
					result.setMsg("Detalle de entrega NO encontrado.");
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			logger.error("SQLException: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				logger.error("SQLException: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		logger.error("sIZE ITMES_:" + items.size());
		embarqueDetalleDTOItem.setItem(items);
		embarque.setItems(embarqueDetalleDTOItem);
		embarque.setResultDT(result);

		return embarque;
	}

	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z,
			HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado) {

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();
		Connection con = new DBConnection().createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		PreparedStatement stmnt4 = null;
		ResultSet rs4 = null;
		try {

			logger.error("Entre a buscar carriles: " + MATNR + " " + WERKS + " " + ID_PR);
			stmnt2 = con.prepareStatement(GETCARRILES_EMBARQUE_PT);
			stmnt2.setString(1, MATNR);
			stmnt2.setString(2, WERKS);
			stmnt2.setString(3, ID_PR);
			stmnt2.setString(4, ID_PR_Z);
			stmnt2.setInt(5, 2);

			rs2 = stmnt2.executeQuery();
			logger.error("Entre a buscar carriles: END Execute...>");
			while (rs2.next()) {
				if (carrilesBloqueados
						.get(rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {
					CarrilUbicacionDTO item = new CarrilUbicacionDTO();

					if (carrilesMaterialBloqueado.get(
							rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {

						item.setStatusS("0");
					} else {
						item.setStatusS("1");

					}

					item.setLgnum(rs2.getString("LGNUM"));
					item.setLgtyp(rs2.getString("LGTYP"));
					item.setLgpla(rs2.getString("LGPLA"));
					item.setCantidadHus(rs2.getString("TOTAL"));
					item.setMe(rs2.getString("MEINS"));

					item.setCajas(rs2.getString("VERME"));

					try {

						stmnt4 = con.prepareStatement(PENDIENTES_POR_PICKEAR_POR_CARRIL);

						stmnt4.setString(1, item.getLgnum());
						stmnt4.setString(2, item.getLgtyp());
						stmnt4.setString(3, item.getLgpla());

						rs4 = stmnt4.executeQuery();

						if (rs4.next()) {
							int cantidad = rs4.getInt(1);

							item.setHusPendientes(cantidad + "");
						}

					} catch (Exception e) {
						logger.error("Error: PENDIENTES_POR_PICKEAR_POR_CARRIL " + e.toString());
					}

					carrilList.add(item);
				}

			}

			// //
			// //////////////////////////////////////////////////////////////////////////////////////////////////////
			// // se obtienen los carriles que cuentan con material, pero alguno
			// de
			// // ellos esta bloqueado, asi se avisara al supervisor
			//
			// stmnt2 =
			// con.prepareStatement(GETCARRILES_EMBARQUE_PT_BLOQUEADOS);
			// stmnt2.setString(1, MATNR);
			// stmnt2.setString(2, WERKS);
			// stmnt2.setString(3, ID_PR);
			// stmnt2.setString(4, ID_PR_Z);
			//
			// rs2 = stmnt2.executeQuery();
			// LOCATION.error("Entre a buscar carriles: END Execute...>");
			// while (rs2.next()) {
			//
			// CarrilUbicacionDTO item = new CarrilUbicacionDTO();
			//
			// item.setStatusS("1");
			// item.setLGNUM(rs2.getString("LGNUM"));
			// item.setLGTYP(rs2.getString("LGTYP"));
			// item.setLGPLA(rs2.getString("LGPLA"));
			// item.setCantidadHus(rs2.getString("TOTAL"));
			// item.setMe(rs2.getString("MEINS"));
			//
			// item.setCajas(rs2.getString("VERME"));
			//
			// try {
			//
			// stmnt4 = con
			// .prepareStatement(PENDIENTES_POR_PICKEAR_POR_CARRIL);
			//
			// stmnt4.setString(1, item.getLGNUM());
			// stmnt4.setString(2, item.getLGTYP());
			// stmnt4.setString(3, item.getLGPLA());
			//
			// rs4 = stmnt4.executeQuery();
			//
			// if (rs4.next()) {
			// int cantidad = rs4.getInt(1);
			//
			// item.setHusPendientes(cantidad + "");
			// }
			//
			// } catch (Exception e) {
			// LOCATION.error("Error: PENDIENTES_POR_PICKEAR_POR_CARRIL "
			// + e.toString());
			// }
			//
			// carrilList.add(item);
			//
			// }

			carrilesDTO.setItem(carrilList);
			logger.error("END List");

		} catch (SQLException e) {
			logger.error("Error: GETCARRILES_EMBARQUE_PT " + e.toString());
		} catch (Exception en) {
			logger.error("Error: GETCARRILES_EMBARQUE_PT " + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.getMessage();
			}
		}
		return carrilesDTO;

	}

	public ResultDTO contabilizadoOK(String entry) {
		Connection con = new DBConnection().createConnection();
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
		logger.error("ingresaDetalleEnvase DAO Embarque");
		Connection con = new DBConnection().createConnection();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement ps = null;

		try {
			logger.error("ingresaDetalleEnvase DAO Embarque PT :" + VBELN);

			for (int x = 0; x < carrilesDTO.getItem().size(); x++) {
				BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());
				int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

				for (int y = 0; y < ingresarZpicking; y++) {
					ps = con.prepareStatement(INGRESA_ZPICKING);
					ps.setString(1, VBELN);
					ps.setString(2, carrilesDTO.getItem().get(x).getLgnum());
					ps.setString(3, carrilesDTO.getItem().get(x).getLgtyp());
					ps.setString(4, carrilesDTO.getItem().get(x).getLgpla());
					ps.setString(5, carrilesDTO.getItem().get(x).getMaterial());

					ps.setString(6, user);
					ps.setString(7, werks);
					ps.setString(8, carrilesDTO.getItem().get(x).getLgnum()
							+ carrilesDTO.getItem().get(x).getLgtyp() + carrilesDTO.getItem().get(x).getLgpla());

					ps.executeUpdate();
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
		Connection con = new DBConnection().createConnection();
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
		Connection con = new DBConnection().createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		HashMap<String, String> hashhMap = new HashMap<String, String>();

		try {

			logger.error("Error validaEntregaPickingCompleto");
			stmn = con.prepareStatement(VALIDA_PICK_FINALIZADO);

			stmn.setString(1, Utils.zeroFill(entregaInput.getEntrega(), 10));
			stmn.setString(2, entregaInput.getWerks());

			logger.error("Error: " + Utils.zeroFill(entregaInput.getEntrega(), 10));

			logger.error("Error: " + entregaInput.getWerks());
			rs = stmn.executeQuery();

			logger.error("Error despues");
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

	public EntregaInputDTO reservaUbicaciones(EntregaInputDTO entregaInput) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = new DBConnection().createConnection();

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}
		entregaInput.setResultDT(result);
		return entregaInput;

	}

	public ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu) {

		Connection con = new DBConnection().createConnection();

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
		Connection con = new DBConnection().createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;
		try {

			logger.error("Datos: " + hu + "-" + werks + "-" + lgtyp + "-" + lgpla + '-');
			stmn = con.prepareStatement(GET_HU);

			stmn.setString(1, hu);
			stmn.setString(2, werks);
			stmn.setString(3, lgtyp);
			stmn.setString(4, lgpla);
			rs = stmn.executeQuery();
			if (rs.next()) {
				logger.error("ok");
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
					entrega.setBestq(rs2.getString("bestq"));

					logger.error(
							entrega.getMaterial() + entrega.getDescripcion() + entrega.getCajas() + entrega.getMe());

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
			logger.error("Error SQLException: " + e.toString());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
			logger.error("Error Exception: " + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
				logger.error("Error Exception2: " + e.toString());
			}
		}
		entrega.setResultDT(result);

		return entrega;

	}

	public ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = new DBConnection().createConnection();

		CallableStatement callableStatement = null;

		try {

			// @HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGNUM, @LGTYP,
			// @LGPLA, @RETURN
			// CONSUME_HUS = "exec sp_bcps_wm_consume_hus ?,?,?,?,?,?,?,?,?,?";

			// @HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGNUM, @LGTYP,
			// @LGPLA, @RETURN

			logger.error("CONSUME HUS");

			callableStatement = con.prepareCall(CONSUME_HUS);

			callableStatement.setString(1, entregaInput.getHu1());
			callableStatement.setString(2, entregaInput.getHu2());
			callableStatement.setString(3, entregaInput.getUsuarioMontacarga());
			callableStatement.setString(4, Utils.zeroFill(entregaInput.getEntrega(), 10));
			callableStatement.setString(5, Utils.zeroFill(entregaInput.getMatnr(), 18));
			callableStatement.setString(6, entregaInput.getWerks());
			callableStatement.setString(7, entregaInput.getuOrigen0());
			callableStatement.setString(8, entregaInput.getuOrigen1());
			callableStatement.setString(9, entregaInput.getuOrigen2());
			callableStatement.registerOutParameter(10, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;
			logger.error("AFTER EXECUTE: " + id);
			id = callableStatement.getInt(10);
			result.setId(id);
			logger.error("AFTER EXECUTE2: " + id);

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

	public ResultDTO contabilizaEntregaExport(EmbarqueDTO embarqueDTO, String user) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = new DBConnection().createConnection();

		CallableStatement callableStatement = null;
		try {
			callableStatement = con.prepareCall(CONTABILIZAR_EMBARQUE_PT_EXPORT);
			callableStatement.setString(1, Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10));
			callableStatement.setString(2, embarqueDTO.getWerks());
			callableStatement.setString(3, user);
			callableStatement.setString(4, embarqueDTO.getVkorg());
			callableStatement.setString(5, embarqueDTO.getLfart());
			callableStatement.setInt(6, 9);

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

	public ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = new DBConnection().createConnection();

		CallableStatement callableStatement = null;

		try {

			// exec SP_CONTABILIZAR_ENTREGA_ENTRANTE_EMBARQUE_PT @EMBARQUE,
			// @WERKS, @USER, @VKORG, @LFART, @RETURN

			callableStatement = con.prepareCall(CONTABILIZAR_EMBARQUE_PT);

			callableStatement.setString(1, Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10));
			callableStatement.setString(2, embarqueDTO.getWerks());
			callableStatement.setString(3, user);
			callableStatement.setString(4, embarqueDTO.getVkorg());
			callableStatement.setString(5, embarqueDTO.getLfart());
			callableStatement.setInt(6, 9);

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

	public ResultDTO limpiaPendientesXUsuario(String vbeln, String user) {
		ResultDTO result = new ResultDTO();
		Connection con = new DBConnection().createConnection();
		PreparedStatement stmn = null;

		try {

			logger.error("Limpia pendientes DAO :" + vbeln);

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public ResultDTO limpiaPendientes(String vbeln) {
		ResultDTO result = new ResultDTO();
		Connection con = new DBConnection().createConnection();
		PreparedStatement stmn = null;

		try {

			logger.error("Limpia pendientes DAO :" + vbeln);
			stmn = con.prepareStatement(LIMPIA_PENDIENTE);
			stmn.setString(1, vbeln);

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
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public String obtenerFecha() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String queryResult = "VACIO";
		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(FECHA);
			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					queryResult = rs.getString(1);
				}
			}
		} catch (SQLException ex) {
			/**
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}

		return queryResult;
	}

	public int isRstAllowed(HuDTO huDTO) {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		int count = 0;
		try {
			con = new DBConnection().createConnection();
			stm = con.prepareStatement(REST_ALLOWED);
			stm.setString(1, Base64.encodeBase64String(
					(HUsRepository.RST_VALUE + huDTO.getWerks() + "_" + huDTO.getLfart()).getBytes()));
			rs = stm.executeQuery();
			if (rs.next()) {
				count = rs.getInt("TOT");
			}
		} catch (SQLException e) {
			/**
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, e);
			 */
			logger.error("Error en base de datos", e);
		} finally {
			DBConnection.closeConnection(con);
		}
		return count;
	}

	public String obtenerCliente(String noEntrega, String num) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String queryResult = "VACIO";
		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(CLIENTE);
			pst.setString(1, noEntrega);
			rs = pst.executeQuery();

			if (rs != null && num.equals("1")) {
				while (rs.next()) {
					queryResult = rs.getString(1);
				}
			} else if (rs != null && num.equals("2")) {
				while (rs.next()) {
					queryResult = rs.getString(2);
				}
			}
		} catch (SQLException ex) {
			/*
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}
		return queryResult;
	}

	public String obtenerPlanta(String noEntrega) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String queryResult = "VACIO";
		String queryResult1 = "VACIO";
		String queryResult2 = "VACIO";
		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(PLANTA);
			pst.setString(1, noEntrega);
			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					queryResult1 = rs.getString(1);
					queryResult2 = rs.getString(2);
				}
				queryResult = queryResult1 + queryResult2;
			}
		} catch (SQLException ex) {
			/*
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}

		return queryResult;
	}

	public String obtenerDatosCliente(String noEntrega) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String queryResult = "VACIO";
		String queryResult1 = "VACIO";
		String queryResult2 = "VACIO";
		String queryResult3 = "VACIO";
		String queryResult4 = "VACIO";
		String queryResult5 = "VACIO";
		String queryResult6 = "VACIO";
		String queryResult7 = "VACIO";
		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(DATOSCLIENTE);
			pst.setString(1, noEntrega);
			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					queryResult1 = rs.getString(1);
					queryResult2 = rs.getString(2);
					queryResult3 = rs.getString(3);
					queryResult4 = rs.getString(4);
					queryResult5 = rs.getString(5);
					queryResult6 = rs.getString(6);
					queryResult7 = rs.getString(7);
				}
				queryResult = queryResult1 + " " + queryResult2 + " " + queryResult3 + " No. " + queryResult4 + " Col. "
						+ queryResult5 + " " + queryResult6 + " R.F.C. " + queryResult7;

			}
		} catch (SQLException ex) {
			/*
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}

		return queryResult;
	}

	public String obtenerDirPlanta(String noEntrega) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String queryResult = "VACIO";
		String queryResult1 = "VACIO";
		String queryResult2 = "VACIO";
		String queryResult3 = "VACIO";
		String queryResult4 = "VACIO";
		String queryResult5 = "VACIO";

		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(DIRPLANTA);
			pst.setString(1, noEntrega);
			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					queryResult1 = rs.getString(1);
					queryResult2 = rs.getString(2);
					queryResult3 = rs.getString(3);
					queryResult4 = rs.getString(4);
					queryResult5 = rs.getString(5);
				}
				queryResult = queryResult1 + " No. " + queryResult2 + " C.P. " + queryResult3 + " " + queryResult4
						+ " R.F.C. " + queryResult5;

			}
		} catch (SQLException ex) {
			/*
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}

		return queryResult;
	}

	public RemisionDatasource obtenerTabla(String noEntrega) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		RemisionDatasource datasource = new RemisionDatasource();
		Remision r;
		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(TABLA);
			pst.setString(1, noEntrega);
			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					r = new Remision();

					try {
						r.setClave(Integer.parseInt(rs.getString(1)) + "");
					} catch (Exception e) {
						r.setClave(rs.getString(1));
					}

					r.setDescripcion(rs.getString(2));
					r.setCantidad(rs.getString(3));

					datasource.addRemision(r);
				}
			}

		} catch (SQLException ex) {
			/*
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}

		return datasource;

	}

	public ExportacionDatasource obtenerTablaExp(String noEntrega) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ExportacionDatasource datasource = new ExportacionDatasource();
		Remision r;
		con = new DBConnection().createConnection();
		try {
			pst = con.prepareStatement(getDataLIPS);
			pst.setString(1, noEntrega);
			rs = pst.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					r = new Remision();
					try {
						r.setClave(Integer.parseInt(rs.getString("MATNR")) + "");
					} catch (Exception e) {
						r.setClave(rs.getString("MATNR"));
					}

					r.setDescripcion(rs.getString("BISMT"));
					r.setCantidad(rs.getString("LFIMG"));
					r.setPrecioUnitario("$ " + rs.getString("KBETR"));
					datasource.addRemision(r);
				}
			}

		} catch (SQLException ex) {
			/*
			 * Logger.getLogger(EmbarquePTDAO.class.getName()).log(Level.SEVERE, null, ex);
			 */
			logger.error("Error en base de datos", ex);
		} finally {
			DBConnection.closeConnection(con);
		}

		return datasource;

	}

	public ResultDTO cambiarCantidadOrdenProduccion(EmbarqueDetalleDTO embarqueDetalleDTO, String user, String werks) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = new DBConnection().createConnection();

		CallableStatement callableStatement = null;

		try {

			// @HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGNUM, @LGTYP,
			// @LGPLA, @RETURN
			// CONSUME_HUS = "exec sp_bcps_wm_consume_hus ?,?,?,?,?,?,?,?,?,?";

			// @HU1, @HU2, @USER, @VBELN, @MATNR, @WERKS, @LGNUM, @LGTYP,
			// @LGPLA, @RETURN

			callableStatement = con.prepareCall(CAMBIA_CANTIDAD_ORDEN);

			callableStatement.setString(1, embarqueDetalleDTO.getVbeln());
			callableStatement.setString(2, embarqueDetalleDTO.getMaterial());
			callableStatement.setString(3, embarqueDetalleDTO.getCajas());
			callableStatement.setString(4, werks);
			callableStatement.setString(5, user);
			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
			callableStatement.execute();

			result.setId(callableStatement.getInt(6));

			if (result.getId() == 1) {
				callableStatement = con.prepareCall(CAMBIA_CANTIDAD_ORDEN_ZCONT);
				callableStatement.setString(1, embarqueDetalleDTO.getVbeln());
				callableStatement.setString(2, embarqueDetalleDTO.getMaterial());
				callableStatement.setString(3, embarqueDetalleDTO.getCajas());
				callableStatement.setString(4, werks);
				callableStatement.setString(5, user);
				callableStatement.setString(6, embarqueDetalleDTO.getPosicion());
				callableStatement.registerOutParameter(7, java.sql.Types.INTEGER);

				callableStatement.execute();
				result.setId(callableStatement.getInt(7));
			}
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

	public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {

		HashMap<String, String> map = new HashMap<String, String>();
		Connection con = new DBConnection().createConnection();

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
			logger.error("SQLException:" + e.toString());
			map = null;
		} catch (Exception en) {
			logger.error("Exception:" + en.toString());
			map = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				logger.error("Exception:" + e.toString());

			}
		}

		return map;
	}

	public HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks) {

		HashMap<String, String> map = new HashMap<String, String>();
		Connection con = new DBConnection().createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(GET_CARRILES_EMBARQUE_PT_CON_MATERIAL_BLOQUEADO);
			stmn.setString(1, matnr);
			stmn.setString(2, werks);
			rs = stmn.executeQuery();
			logger.error("MAterial aconsultar: " + matnr + "->centro:" + werks + ".");

			while (rs.next()) {

				map.put(rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim(), "");

			}

		} catch (SQLException e) {
			logger.error("SQLException:" + e.toString());
			map = null;
		} catch (Exception en) {
			logger.error("Exception:" + en.toString());
			map = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				logger.error("Exception:" + e.toString());

			}
		}

		return map;
	}

}
