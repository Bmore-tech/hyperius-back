package com.bmore.hyperius.web.repository.old;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTOItem;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

public class AlimentacionLineaRepositoryOld {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	static String PENDIENTES_POR_PICKEAR_POR_CARRIL = "select count(*) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
			+ "where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='2' and (Status is null or Status ='1')  ";

	static String VALIDA_ORDEN_PRODUCCION = "select AUFK.AUFNR,  dbo.conFec(AUFK.ERDAT) as ERDAT, AUFK.WERKS, zCentrosBCPS.descripcion "
			+ "from HCMDB.dbo.AUFK AUFK WITH(NOLOCK) inner join HCMDB.dbo.zCentrosBCPS zCentrosBCPS WITH(NOLOCK) on AUFK.WERKS = zCentrosBCPS.werks "
			+ "and AUFK.AUFNR=? and AUFK.WERKS=?";

	static String DATOS_ORDEN = "SELECT distinct(RESB.RSPOS),RESB.MATNR,MAKT.MAKTX,RESB.BDMNG,RESB.ENMNG,RESB.MEINS "
			+ "FROM HCMDB.dbo.RESB RESB WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) "
			+ "ON RESB.MATNR = MAKT.MATNR " + "WHERE ( MAKT.MAKTX LIKE 'BOTE%' OR " + "MAKT.MAKTX LIKE 'BARRIL%' OR "
			+ "MAKT.MAKTX LIKE 'ENV%' ) " + "AND RESB.AUFNR = ?";

	static String CANTIDAD_CAJAS = "SELECT RESB.MATNR, sum(convert(DECIMAL(18, 3), BDMNG)) as cantidadOriginal, sum(convert(DECIMAL(18, 3), ENMNG))as cantidadAsignada	"
			+ "FROM HCMDB.dbo.RESB RESB WITH(NOLOCK) where MATNR = ? and " + "RESB.AUFNR = ? group by RESB.MATNR";

	static String GET_DATA_HU = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx,BESTQ as BESTQ "
			+ " from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR "
			+ " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";

	static String GET_HU = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";

	static String GET_CARRILES_BLOQUEADOS = "select distinct(CARRIL) "
			+ "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";

	static String GET_CARRILES_ALIMENTACION = "exec sp_bcps_get_carriles_por_material ?,?,?,?,?";

	static String GET_CARRILES_ALIMENTACION_CON_MATERIAL_BLOQUEADO = "select distinct LGNUM, LGTYP, LGPLA from LQUA with(nolock) where "
			+ "MATNR=? and (BESTQ='S' or BESTQ ='Q') and WERKS=?";

	static String VALIDA_PICK = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where VBELN = ? and WERKS = ? and idProceso='2'";

	static String INGRESA_ZPICKING = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
			+ "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,'2',?,?)";

	static String SUMA_ZPICKING_ORDEN_PRODUCCION = "select sum(convert(DECIMAL(18, 3), VERME))as cantidad from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM "
			+ "in(SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln =? and matnr=? and "
			+ "status='2' and EXIDV is not null and idProceso='2')";

	// /////////////HH

	static String RESERVA_UBICACION_HU1 = "update top (1) HCMDB.dbo.ZPickingEntregaEntrante set usuarioMontacarga = ? , Status='1' where "
			+ "usuarioMontacarga is null and EXIDV is null and Status is null and VBELN = ? and idProceso='2'";

	static String OBTIENE_RESERVA_UBICACION_HU1 = "select * from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where usuarioMontacarga = ? and VBELN = ? and status = 1 and idProceso='2'";

	static String RESERVA_UBICACION_HU2 = " update top (1) HCMDB.dbo.ZPickingEntregaEntrante set usuarioMontacarga = ?,  Status='1' where "
			+ "usuarioMontacarga is null and EXIDV is null and Status is null and VBELN = ? and idProceso='2' and LGNUM+LGTYP+LGPLA in "
			+ "(select LGNUM+LGTYP+LGPLA from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where Status='1' and  usuarioMontacarga = ?) ";

	String OBTIENE_DEPALETIZADORA = "select LGTYP,LGPLA  from HCMDB.dbo.RESB WITH(NOLOCK) where MATNR= ? and AUFNR = ?";

	static String CONFIRMA_HU_EN_ZPICKING = "update top (1) HCMDB.dbo.ZPickingEntregaEntrante set status = '2', EXIDV = ? where  usuarioMontacarga = ? "
			+ "and Status = '1' and EXIDV is null and VBELN = ? and idProceso='2'";

	static String VALIDA_PICKEO_PREVIO_HU = "select EXIDV from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where EXIDV = ? and VBELN = ? and idProceso='2'";

	static String INSERT_PROCESO_ZCONTINGENCIA_5 = " insert into HCMDB.dbo.zContingencia(IDPROC,FECHA,HORA,CENTRO,HU, "
			+ " ORDEN_PRODUCCION,CONTROL_CALIDAD,ALMACEN,USUARIO) "
			+ " select IDPROC=5, convert(date,getdate()), convert(time, getdate()), "
			+ " WERKS = ?, HU=?,ORDEN_PRODUCCION=?,CONTROL_CALIDAD=(select top(1) BESTQ from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM = ? and WERKS = ?), "
			+ " ALMACEN= ?,USUARIO= ?";

	static String LIMPIA_PENDIENTE = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
			+ " where VBELN = ? and Status = '1' and idProceso='2'";

	static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
			+ "where VBELN = ? and Status = '1' and usuarioMontacarga = ? and idProceso='2'";

	static String UPDATE_LQUA = "update HCMDB.dbo.LQUA set SKZUA ='X' where LENUM = ?";

	public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput) {

		OrdenProduccionDTO orden = new OrdenProduccionDTO();
		ResultDTO result = new ResultDTO();

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;

		try {
			logger.info("Dentro de DAO: " + ordenInput.getOrdenProduccion());
			logger.info("Dentro de DAO: " + ordenInput.getWerks());

			stmn = con.prepareStatement(VALIDA_ORDEN_PRODUCCION);
			stmn.setString(1, ordenInput.getOrdenProduccion());
			stmn.setString(2, ordenInput.getWerks());
			rs = stmn.executeQuery();

			if (rs.next()) {

				orden.setOrdenProduccion(rs.getString("AUFNR"));
				orden.setFabrica(rs.getString("WERKS"));
				orden.setFechaDocumento(rs.getString("ERDAT"));
				orden.setFabricaDesc(rs.getString("descripcion"));

				result.setId(1);
				result.setMsg("Recuperacion de Datos de Cabecera Correcta");
				
				logger.info("orden: " + orden.toString());

			} else {
				result.setId(2);
				result.setMsg("Orden de producción no existe");
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

		orden.setResultDT(result);
		return orden;
	}

	public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks) {
		OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();

		CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

		OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
		List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		PreparedStatement stmn3 = null;
		ResultSet rs3 = null;

		try {
			stmn = con.prepareStatement(DATOS_ORDEN);
			stmn.setString(1, aufnr);

			rs = stmn.executeQuery();
			int cant = 0;
			int contabilizado = 0;

			int linea = 1;
			HashMap<String, String> map = new HashMap<String, String>();

			while (rs.next()) {

				if (map.get(rs.getString("MATNR")) == null) {

					map.put(rs.getString("MATNR"), rs.getString("MATNR"));

					logger.error("Linea: " + linea);
					linea++;
					cant++;
					OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

					item.setPosicion(rs.getString("RSPOS"));
					item.setMaterial(rs.getString("MATNR"));
					item.setDescripcion(rs.getString("MAKTX"));
					item.setMe(rs.getString("MEINS"));

					try {

						stmn3 = con.prepareStatement(CANTIDAD_CAJAS);

						stmn3.setString(1, item.getMaterial());
						stmn3.setString(2, aufnr);

						rs3 = stmn3.executeQuery();

						if (rs3.next()) {

							logger.error("antes de ");
							item.setCajas(rs3.getString("cantidadOriginal"));
							item.setCajasAsignadas(rs3.getString("cantidadAsignada"));

							logger.error("Cantidad original: " + rs3.getString("cantidadOriginal"));
							logger.error("cantidadAsignada: " + rs3.getString("cantidadAsignada"));
						}

					} catch (Exception e) {
						logger.error("Error en SUMA_ZPICKING_ORDEN_PRODUCCION: " + e);
					}

					try {

						stmn2 = con.prepareStatement(SUMA_ZPICKING_ORDEN_PRODUCCION);

						stmn2.setString(1, aufnr);
						stmn2.setString(2, item.getMaterial());

						rs2 = stmn2.executeQuery();

						if (rs2.next()) {

							String cantidad = rs2.getString("cantidad");

							item.setCajasAsignadas(
									(Float.parseFloat(cantidad) + Float.parseFloat(rs.getString("ENMNG"))) + "");

						}

					} catch (Exception e) {
						logger.error("Error en SUMA_ZPICKING_ORDEN_PRODUCCION: " + e);
					}

					detalle.add(item);
				}
			}

			if (contabilizado == detalle.size()) {
				logger.error("Contabilizado = true");
				ordenProduccionDTO.setContabilizar("true");
			} else {
				logger.error("Contabilizado = false");
				ordenProduccionDTO.setContabilizar("false");
			}

			if (cant != 0) {
				result.setId(1);
				result.setMsg("Detalle de Orden recuperado con exito");
			} else {
				result.setId(2);
				result.setMsg("No fue posible recuperar el detalle de la orden");
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

		detalleList.setItem(detalle);
		ordenProduccionDTO.setItems(detalleList);

		carrilesList.setItem(carriles);
		ordenProduccionDTO.setCarriles(carrilesList);

		ordenProduccionDTO.setResultDT(result);
		return ordenProduccionDTO;
	}

	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String IP_PR_Z,
			HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado) {

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		PreparedStatement stmnt4 = null;
		ResultSet rs2 = null;
		ResultSet rs4 = null;

		try {
			stmnt2 = con.prepareStatement(GET_CARRILES_ALIMENTACION);
			stmnt2.setString(1, MATNR);
			stmnt2.setString(2, WERKS);
			stmnt2.setString(3, ID_PR);
			stmnt2.setString(4, IP_PR_Z);
			stmnt2.setInt(5, 2);

			rs2 = stmnt2.executeQuery();

			if (rs2 != null) {
				while (rs2.next()) {
					if (carrilesBloqueados.get(
							rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {

						CarrilUbicacionDTO item = new CarrilUbicacionDTO();

						if (carrilesMaterialBloqueado.get(rs2.getString("LGNUM") + rs2.getString("LGTYP")
								+ rs2.getString("LGPLA").trim()) == null) {

							logger.error("Con :0");
							item.setStatusS("0");
						} else {
							item.setStatusS("1");
							logger.error("Con :1");
						}
						item.setLgnum(rs2.getString("LGNUM"));
						item.setLgtyp(rs2.getString("LGTYP"));
						item.setLgpla(rs2.getString("LGPLA"));
						item.setCantidadHus(rs2.getString("TOTAL"));
						item.setMe(rs2.getString("MEINS"));

						item.setCajas(rs2.getString("VERME"));

						// //////////////////////////////
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
							logger.error("Error: " + e.toString());
						}

						carrilList.add(item);
					}
				}
			}

			// //
			// //////////////////////////////////////////////////////////////////////////////////////////////////////
			// // se obtienen los carriles que cuentan con material, pero alguno
			// de
			// // ellos esta bloqueado, asi se avisara al supervisor
			// stmnt2 = con
			// .prepareStatement(GET_CARRILES_ALIMENTACION_CON_MATERIAL_BLOQUEADO);
			// stmnt2.setString(1, MATNR);
			// stmnt2.setString(2, WERKS);
			// stmnt2.setString(3, ID_PR);
			// stmnt2.setString(4, IP_PR_Z);
			//
			// rs2 = stmnt2.executeQuery();
			//
			// if (rs2 != null) {
			// while (rs2.next()) {
			//
			// if (carrilesBloqueados.get(rs2.getString("LGNUM")
			// + rs2.getString("LGTYP")
			// + rs2.getString("LGPLA").trim()) == null) {
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
			// // //////////////////////////////
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
			//
			// // LOCATION.error("Cantidad2: " + cantidad);
			//
			// }
			//
			// } catch (Exception e) {
			// LOCATION.error("Error: " + e.toString());
			// }
			//
			// carrilList.add(item);
			// }
			// }
			// }

			// ////////////////////////////////////////////////////////////////////////////

			carrilesDTO.setItem(carrilList);

		} catch (SQLException e) {

		} catch (Exception en) {

		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.getMessage();
			}
		}
		return carrilesDTO;
	}

	public ResultDTO ingresaDetalleEnvase(String aufnr, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {
		logger.error("ingresaDetalleEnvase DAO AlimentacionLinea");
		Connection con = DBConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();

		PreparedStatement stmntX = null;

		try {

			logger.error("ingresaDetalleEnvase DAO AlimentacionLinea :" + aufnr);

			for (int x = 0; x < carrilesDTO.getItem().size(); x++) {

				BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());

				int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

				for (int y = 0; y < ingresarZpicking; y++) {

					stmntX = con.prepareStatement(INGRESA_ZPICKING);
					stmntX.setString(1, aufnr);
					stmntX.setString(2, carrilesDTO.getItem().get(x).getLgnum());
					stmntX.setString(3, carrilesDTO.getItem().get(x).getLgtyp());
					stmntX.setString(4, carrilesDTO.getItem().get(x).getLgpla());
					stmntX.setString(5, carrilesDTO.getItem().get(x).getMaterial());

					stmntX.setString(6, user);
					stmntX.setString(7, werks);
					stmntX.setString(8, carrilesDTO.getItem().get(x).getLgnum()
							+ carrilesDTO.getItem().get(x).getLgtyp() + carrilesDTO.getItem().get(x).getLgpla());

					stmntX.executeUpdate();
				}

			}
			resultDT.setId(1);
			resultDT.setMsg("Se registro la orden de producción correctamete, mandar a montacarga");

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

	public ResultDTO validarEntregaPickin(OrdenProduccionInputDTO ordenProduccionInput) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(VALIDA_PICK);
			stmn.setString(1, ordenProduccionInput.getOrdeProduccion());
			stmn.setString(2, ordenProduccionInput.getWerks());
			rs = stmn.executeQuery();
			if (rs.next()) {

				// Se caza a que solo es un material en la orden de produccion
				// de otra manera esto no funcionara
				result.setTypeS(rs.getString("MATNR"));
				result.setId(1);
				result.setMsg("ORDEN DE PRODUCCION YA SE ENCUENTRA EN PICKING");

			} else {
				result.setId(2);
				result.setMsg("ORDEN DE PRODUCCION NO DISPONIBLE PARA PICKING");
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

	public OrdenProduccionDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {

		ResultDTO result = new ResultDTO();
		OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();
		Connection con = DBConnection.createConnection();
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

					orden.setMaterial(rs2.getString("matnr"));
					orden.setDescripcion(rs2.getString("maktx"));
					orden.setCajas(rs2.getString("vemng"));
					orden.setMe(rs2.getString("meins"));
					orden.setBestq(rs2.getString("bestq"));
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
		orden.setResultDT(result);

		return orden;

	}

	public ResultDTO reservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput) {

		Connection con = DBConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		int resultado = 0;

		try {
			stmn = con.prepareStatement(RESERVA_UBICACION_HU1);

			stmn.setString(1, ordenProduccionInput.getUsuarioMontacarga());
			stmn.setString(2, ordenProduccionInput.getOrdeProduccion());

			resultado = stmn.executeUpdate();

			if (resultado == 1) {

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

		return resultDT;
	}

	public ResultDTO reservaUbicacionHU2(OrdenProduccionInputDTO ordenProduccionInput) {

		Connection con = DBConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		int resultado = 0;

		try {

			logger.error("orden: " + ordenProduccionInput.getOrdeProduccion());

			logger.error("usuario: " + ordenProduccionInput.getUsuarioMontacarga());

			stmn = con.prepareStatement(RESERVA_UBICACION_HU2);

			stmn.setString(1, ordenProduccionInput.getUsuarioMontacarga());
			stmn.setString(2, ordenProduccionInput.getOrdeProduccion());
			stmn.setString(3, ordenProduccionInput.getUsuarioMontacarga());

			resultado = stmn.executeUpdate();

			if (resultado == 1) {

				resultDT.setId(1);
				resultDT.setMsg("Reserva de carril");

			} else {
				resultDT.setId(2);
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

		return resultDT;
	}

	public OrdenProduccionInputDTO obtieneReservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput) {

		Connection con = DBConnection.createConnection();

		OrdenProduccionInputDTO ordenProduccionInputReturn = new OrdenProduccionInputDTO();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet resultado = null;

		try {
			stmn = con.prepareStatement(OBTIENE_RESERVA_UBICACION_HU1);

			stmn.setString(1, ordenProduccionInput.getUsuarioMontacarga());
			stmn.setString(2, ordenProduccionInput.getOrdeProduccion());

			resultado = stmn.executeQuery();

			if (resultado.next()) {

				ordenProduccionInputReturn.setuOrigen0(resultado.getString("LGNUM"));

				ordenProduccionInputReturn.setuOrigen1(resultado.getString("LGTYP"));
				ordenProduccionInputReturn.setuOrigen2(resultado.getString("LGPLA"));

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

		ordenProduccionInputReturn.setResultDT(resultDT);

		return ordenProduccionInputReturn;
	}

	public OrdenProduccionInputDTO obtieneDepaletizadora(OrdenProduccionInputDTO ordenProduccionInput) {

		Connection con = DBConnection.createConnection();

		OrdenProduccionInputDTO ordenProduccionInputReturn = new OrdenProduccionInputDTO();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet resultado = null;

		try {
			stmn = con.prepareStatement(OBTIENE_DEPALETIZADORA);

			stmn.setString(1, ordenProduccionInput.getMatnr());
			stmn.setString(2, ordenProduccionInput.getOrdeProduccion());

			resultado = stmn.executeQuery();

			if (resultado.next()) {

				ordenProduccionInputReturn.setuDestino2(resultado.getString("LGPLA"));

				resultDT.setId(1);
				resultDT.setMsg("DEPA encontrada");

			} else {
				resultDT.setId(0);
				resultDT.setMsg("Depaletizadora no encontrada, acuda con supervisor");
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

		ordenProduccionInputReturn.setResultDT(resultDT);

		return ordenProduccionInputReturn;
	}

	public ResultDTO confirmaHUenDepa(OrdenProduccionInputDTO ordenProduccionInput, String hu) {

		Connection con = DBConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		int resultado = 0;

		try {

			logger.error("orden: " + ordenProduccionInput.getOrdeProduccion());

			logger.error("usuario: " + ordenProduccionInput.getUsuarioMontacarga());

			stmn = con.prepareStatement(CONFIRMA_HU_EN_ZPICKING);

			stmn.setString(1, hu);
			stmn.setString(2, ordenProduccionInput.getUsuarioMontacarga());
			stmn.setString(3, ordenProduccionInput.getOrdeProduccion());

			resultado = stmn.executeUpdate();

			if (resultado == 1) {

				resultDT.setId(1);
				resultDT.setMsg("Reserva de carril");

			} else {
				resultDT.setId(2);
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

		return resultDT;
	}

	public ResultDTO validaPickeoPrevioHU(OrdenProduccionInputDTO ordenProduccionInput, String hu) {

		Connection con = DBConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet resultado = null;

		try {

			logger.error("orden: " + ordenProduccionInput.getOrdeProduccion());

			logger.error("usuario: " + ordenProduccionInput.getUsuarioMontacarga());

			stmn = con.prepareStatement(VALIDA_PICKEO_PREVIO_HU);

			stmn.setString(1, hu);
			stmn.setString(2, ordenProduccionInput.getOrdeProduccion());

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

	public ResultDTO insertProcesoContingencia_5(OrdenProduccionInputDTO ordenProduccionInput, String hu) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			stmn = con.prepareStatement(INSERT_PROCESO_ZCONTINGENCIA_5);
			stmn.setString(1, ordenProduccionInput.getWerks());
			stmn.setString(2, hu);
			stmn.setString(3, ordenProduccionInput.getOrdeProduccion());
			stmn.setString(4, hu);
			stmn.setString(5, ordenProduccionInput.getWerks());
			stmn.setString(6, ordenProduccionInput.getLgort());
			stmn.setString(7, ordenProduccionInput.getUsuarioMontacarga());

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("5to paso ejecutado con exito");
			} else {
				result.setId(0);
				result.setMsg("5to paso ejecutado con error");
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

	public int limpiaPendientes(String vbeln) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
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
		return result.getId();
	}

	public ResultDTO limpiaPendientesXUsuario(String vbeln, String user) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
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

	public ResultDTO consumeInventario(String hu, OrdenProduccionInputDTO ordenProduccionInput) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			logger.error("Se consume inventario: " + hu);
			stmn = con.prepareStatement(UPDATE_LQUA);
			stmn.setString(1, hu);

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("Inventario consumido");

				logger.error("Alimentacion LGNUM: " + ordenProduccionInput.getuOrigen0() + " LGTYP: "
						+ ordenProduccionInput.getuOrigen1() + " LGPLA:" + ordenProduccionInput.getuOrigen2());

				Utils.actualizarInventarioCarriles(ordenProduccionInput.getuOrigen0(),
						ordenProduccionInput.getuOrigen1(), ordenProduccionInput.getuOrigen2());

			} else {
				result.setId(0);
				result.setMsg("El inventario no fue consumido");
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

	public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {

		HashMap<String, String> map = new HashMap<String, String>();
		Connection con = DBConnection.createConnection();

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
		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(GET_CARRILES_ALIMENTACION_CON_MATERIAL_BLOQUEADO);
			logger.info("isClosed: " + con.isClosed());
			// LOCATION.info("isClosed: " + con.is)

			stmn.setString(1, matnr);
			stmn.setString(2, werks);
			rs = stmn.executeQuery();
			logger.info("MAterial aconsultar: " + matnr + "->centro:" + werks + ".");

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
