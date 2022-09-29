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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTOItem;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class UbicacionPTRepository {

  @Autowired
  private Utils utils;
  
  @Autowired
  private DBConnection dbConnection;

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	private static String VALIDA_ORDEN_PRODUCCION = "select AUFK.AUFNR,  dbo.conFec(AUFK.ERDAT) as ERDAT, AUFK.WERKS, zCentrosBCPS.descripcion "
			+ "from HCMDB.dbo.AUFK AUFK WITH(NOLOCK) inner join HCMDB.dbo.zCentrosBCPS zCentrosBCPS WITH(NOLOCK) on AUFK.WERKS = zCentrosBCPS.werks "
			+ "and AUFK.AUFNR=? and AUFK.WERKS=?";

	private static String DATOS_ORDEN = "select POSNR,AFPO.MATNR,MAKT.MAKTX,PSMNG,WEMNG,MEINS from HCMDB.dbo.AFPO AFPO WITH(NOLOCK)"
			+ "left join HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on AFPO.MATNR = MAKT.MATNR where AFPO.AUFNR=?";

	private static String GET_CARRILES_BLOQUEADOS = "select distinct(CARRIL) "
			+ "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";

	private static String GET_CARRILES = "EXEC SP_BCPS_GET_CARRILES_POR_MATERIAL ?,?,?,?,?";

	private static String GET_CARRILES_VACIOS = "EXEC SP_GET_CARRILES_VACIOS ?,?,?,?,?";

	private static String GET_CARRILES_PROPUESTOS = "SELECT distinct LGNUM, LGTYP, LGPLA FROM ZPickingEntregaEntrante with(nolock) where VBELN = ? and werks = ? and idProceso = ? and matnr = ? and (Status  is null or Status ='1')";

	private static String SUMA_ZPICKING_ORDEN_PRODUCCION = "select sum(cast(VERME as float))as cantidad from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM "
			+ "in(SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where vbeln =? and matnr=? and status='X' and EXIDV is not null  and idProceso='3')";

	private static String DETAIL_ENTRY = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
			+ "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,?,?,?)";

	private static String PENDIENTES_POR_PICKEAR_POR_CARRIL = "select count(*) as cantidad from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
			+ "where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='3' and Status is null";

	private static String PICKING_COMPLETO_POSICION = "select count (*) as cantidad from ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where MATNR=? and VBELN = ? and idProceso='3' and Status ='X'";

	private static String HUS_EN_ORDEN_PRODUCCION = "select count(*) as cantidad from VEPO WITH(NOLOCK) where matnr=? and VENUM in "
			+ "(select venum from VEKP WITH(NOLOCK) where VPOBJKEY=(select VPOBJKEY from AUFK WITH(NOLOCK) where AUFNR = ?))";

	private static String GET_DATA_HU = "select vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH,VHILM from "
			+ "HCMDB.dbo.MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
			+ "VEKP.venum = VEPO.venum where VEKP.EXIDV=? and VEPO.VELIN ='1'";

	private static String CONSULTA_RESERVA_CARRIL_HU = "SELECT LGNUM, LGTYP, LGPLA, STATUS from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
			+ "where VBELN=? and EXIDV=? and idProceso='3'";

	private static String CONFIRMA_PICKING_HU = "update HCMDB.dbo.ZPickingEntregaEntrante set Status = 'X' where VBELN = ? and EXIDV=? and idProceso='3'";

	private static String RESERVAR_CARRIL_HU = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
			+ "set EXIDV = ? where VBELN = ? and MATNR = ? and  Status is null and EXIDV is null and idProceso='3'";

	private static String UPDATE_LQUA = "insert into HCMDB.dbo.LQUA (LGNUM,MATNR,WERKS,BESTQ,LGTYP,LGPLA,MEINS,GESME,VERME,LENUM,LGORT,SKZUE,SKZUA,LETYP) VALUES "
			+ "(?,?,?,'',?,?,?,?,?,?,?,NULL,NULL,"
			+ "(select top (1) LETYP from ZPAITT_TTW WITH(NOLOCK) where WERKS= ? and MATNR=(select top (1) VHILM FROM VEKP WITH(NOLOCK) where EXIDV= ? and WERKS= ?)))";

	private static String INSERT_PROCESO_ZCONTINGENCIA_7 = "insert into HCMDB.dbo.zContingencia(IDPROC,FECHA,HORA,CENTRO,HU,ORDEN_PRODUCCION,CANTIDAD,USUARIO,TARIMA,TIPO_ALMACEN,DESTINO) "
			+ "select IDPROC=7, convert(date,getdate()), convert(time, getdate()), WERKS = ?, HU=?,ORDEN_PRODUCCION=?,CANTIDAD= ?,USUARIO= ?,TARIMA=?,TIPO_ALMACEN=?,DESTINO=?";

	private static String GETFALTANTES = "select count(*) from "
			+ "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and Status is null and idProceso='3'";

	private static String GET_AUFNR_FROM_HU = "SELECT VPOBJKEY from HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
			+ "on VEPO.VENUM = VEKP.VENUM and VEKP.EXIDV= ? and VEPO.WERKS = ? and VEPO.VELIN='1'";

// TODO Remove unused code found by UCDetector
// 	static String VALIDAR_ORDEN_PRODUCCION_EN_PICKING = "SELECT VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN=? and idProceso='3'";

	private static String VALIDA_PICK = "select distinct(MATNR) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and idProceso='3'";

	// private static String LIMPIA_PENDIENTE_USUARIO = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and usuarioMontacarga = ? and idProceso='3'";

	public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput) {

		OrdenProduccionDTO orden = new OrdenProduccionDTO();
		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;

		try {
			LOCATION.error("Dentro de DAO: " + ordenInput.getOrdenProduccion());
			LOCATION.error("Dentro de DAO: " + ordenInput.getWerks());
			
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
				result.setMsg("Recuperacion de datos de cabecera correcta");

			} else {
				result.setId(2);
				result.setMsg("La orden de producción no existe");
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
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		try {
			stmn = con.prepareStatement(DATOS_ORDEN);
			stmn.setString(1, aufnr);

			rs = stmn.executeQuery();
			int cant = 0;
			// int contabilizado = 0;

			while (rs.next()) {

				cant++;
				OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

				item.setPosicion(rs.getString("POSNR"));
				item.setMaterial(rs.getString("MATNR"));
				item.setDescripcion(rs.getString("MAKTX"));
				item.setCajas(rs.getString("PSMNG"));
				item.setCajasAsignadas(rs.getString("WEMNG"));
				item.setMe(rs.getString("MEINS"));

				try {

					stmn2 = con.prepareStatement(SUMA_ZPICKING_ORDEN_PRODUCCION);

					stmn2.setString(1, aufnr);
					stmn2.setString(2, item.getMaterial());

					rs2 = stmn2.executeQuery();

					if (rs2.next()) {

						String cantidad = rs2.getString("cantidad");

						item.setCajasAsignadas(
								(Float.parseFloat(cantidad) + Float.parseFloat(rs.getString("WEMNG"))) + "");

					}

				} catch (Exception e) {
					LOCATION.error("Error en SUMA_ZPICKING_ORDEN_PRODUCCION: " + e);
				}

				try {

					stmn2 = con.prepareStatement(HUS_EN_ORDEN_PRODUCCION);

					stmn2.setString(1, item.getMaterial());
					stmn2.setString(2, aufnr);

					rs2 = stmn2.executeQuery();

					if (rs2.next()) {
						String cantidad = rs2.getString("cantidad");
						item.setHus(cantidad);
					}

				} catch (Exception e) {
					LOCATION.error("Error en HUS_EN_ORDEN_PRODUCCION: " + e);
				}

				try {

					stmn2 = con.prepareStatement(PICKING_COMPLETO_POSICION);

					stmn2.setString(1, item.getMaterial());
					stmn2.setString(2, aufnr);

					rs2 = stmn2.executeQuery();

					if (rs2.next()) {
						String cantidad = rs2.getString("cantidad");
						item.setHusPendientes(cantidad);
					}

				} catch (Exception e) {
					LOCATION.error("Error en PENDIENTES_POR_PICKEAR_POR_ORDEN_PRODUCCION: " + e);
				}

				detalle.add(item);

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
				// DBConnection.closeConnection(con);
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

	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z, String LGORT,
			String VBELN, HashMap<String, String> carrilesBloqueados) {

		CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();
		Connection con = dbConnection.createConnection();

		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

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

			while (rs2.next()) {

				if (carrilesBloqueados
						.get(rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {

					CarrilUbicacionDTO item = new CarrilUbicacionDTO();
					item.setLgnum(rs2.getString("LGNUM"));
					item.setLgtyp(rs2.getString("LGTYP"));
					item.setLgpla(rs2.getString("LGPLA"));
					item.setCantidadHus(rs2.getString("TOTAL"));
					item.setCajas(rs2.getString("VERME"));
					item.setMe(rs2.getString("MEINS"));
					item.setMaxle(rs2.getString("MAXLE"));
					hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
					carrilList.add(item);
				}

			}
			rs2.close();
			stmnt2.close();

			stmnt2 = con.prepareStatement(GET_CARRILES_VACIOS);
			stmnt2.setString(1, MATNR);
			stmnt2.setString(2, LGORT);
			stmnt2.setString(3, WERKS);
			stmnt2.setString(4, ID_PR);
			stmnt2.setString(5, ID_PR_Z);
			rs2 = stmnt2.executeQuery();
			while (rs2.next()) {

				if (carrilesBloqueados
						.get(rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {
					CarrilUbicacionDTO item = new CarrilUbicacionDTO();
					item.setLgnum(rs2.getString("LGNUM"));
					item.setLgtyp(rs2.getString("LGTYP"));
					item.setLgpla(rs2.getString("LGPLA"));
					item.setCantidadHus("0");
					hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
					carrilList.add(item);
				}
			}
			rs2.close();
			stmnt2.close();

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

				} catch (Exception e) {
					LOCATION.error("Error: PENDIENTES_POR_PICKEAR_POR_CARRIL " + e.toString());
				}
			}

			LOCATION.error("Size carriles SQL:" + carrilList.size());
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
					LOCATION.error("Ingresando");
					stmntX = con.prepareStatement(DETAIL_ENTRY);
					stmntX.setString(1, VBELN);
					stmntX.setString(2, carrilesDTO.getItem().get(x).getLgnum());
					stmntX.setString(3, carrilesDTO.getItem().get(x).getLgtyp());
					stmntX.setString(4, carrilesDTO.getItem().get(x).getLgpla());
					stmntX.setString(5, Utils.zeroFill(carrilesDTO.getItem().get(x).getMaterial(), 18));
					stmntX.setString(6, user);
					stmntX.setString(7, "3");
					stmntX.setString(8, werks);
					stmntX.setString(9, carrilesDTO.getItem().get(x).getLgnum()
							+ carrilesDTO.getItem().get(x).getLgtyp() + carrilesDTO.getItem().get(x).getLgpla());
					stmntX.executeUpdate();
				}
			}
			resultDT.setId(1);
			resultDT.setMsg("Se registro la orden de produccion correctamete, mandar a montacarga");
		} catch (SQLException e) {
			LOCATION.error("SQLException: " + e.toString());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("SQLException: " + en.toString());
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception: " + e.toString());
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return resultDT;
	}

	public OrdenProduccionDetalleDTO getDataHU(String hu) {

		ResultDTO result = new ResultDTO();
		OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();
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
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		orden.setResultDT(result);

		return orden;

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

	public ResultDTO confirmaPickingHU(String VBELN, String hu) {
		ResultDTO result = new ResultDTO();
		result.setId(0);
		result.setMsg("Error de conexion a BD");
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

	public ResultDTO reservarCarrilHU(String entrega, String hu, String matnr) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {
			stmn = con.prepareStatement(RESERVAR_CARRIL_HU);

			stmn.setString(1, hu);
			stmn.setString(2, entrega);
			stmn.setString(3, matnr);

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

	public ResultDTO aumentaInventario(OrdenProduccionInputDTO orden, String hu) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			LOCATION.error("Se consume inventario: ");

			// static String UPDATE_LQUA =
			// "insert into HCMDB.dbo.LQUA
			// (LGNUM,MATNR,WERKS,BESTQ,LGTYP,LGPLA,MEINS,GESME,VERME,LENUM,LGORT) VALUES "
			// +
			// "(?,?,?,(select DISTINCT(Clase_Inspeccion) from HCMDB.dbo.MaterialQUA as QA
			// where Centro= ? and Material=?),?,?,?,?,?,?,?)";

			stmn = con.prepareStatement(UPDATE_LQUA);
			stmn.setString(1, orden.getuDestino0());// LGNUM
			stmn.setString(2, orden.getMatnr());// MATNR
			stmn.setString(3, orden.getWerks());// WERKS
			// stmn.setString(4, orden.getWerks());// BESTQ
			// stmn.setString(5, orden.getMatnr());// BESTQ
			stmn.setString(4, orden.getuDestino1());// LGTYP
			stmn.setString(5, orden.getuDestino2()); // LGPLA
			stmn.setString(6, orden.getCantT());// MEINS
			stmn.setString(7, orden.getCant());// GESME
			stmn.setString(8, orden.getCant());// VERME
			stmn.setString(9, hu);// LENUM
			stmn.setString(10, "LV01");// LGORT //EN PT solo se ingresa al LV01
			stmn.setString(11, orden.getWerks());// LGORT //EN PT solo se
			// ingresa al LV01
			stmn.setString(12, hu);// LGORT //EN PT solo se ingresa al LV01
			stmn.setString(13, orden.getWerks());// LGORT //EN PT solo se
			// ingresa al LV01

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("Inventario aumentado");

				LOCATION.error("ubicacion LGNUM: " + orden.getuDestino0() + " LGTYP: " + orden.getuDestino1()
						+ " LGPLA:" + orden.getuDestino2());

				utils.actualizarInventarioCarriles(orden.getuDestino0(), orden.getuDestino1(), orden.getuDestino2());

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

	public ResultDTO insertProcesoContingencia_7(OrdenProduccionInputDTO orden, String hu) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;

		try {

			stmn = con.prepareStatement(INSERT_PROCESO_ZCONTINGENCIA_7);
			stmn.setString(1, orden.getWerks());
			stmn.setString(2, hu);
			stmn.setString(3, orden.getOrdeProduccion());
			stmn.setString(4, orden.getCant());
			stmn.setString(5, orden.getUsuarioMontacarga());
			stmn.setString(6, orden.getTarima());
			stmn.setString(7, orden.getuDestino1());
			stmn.setString(8, orden.getuDestino2());

			if (stmn.executeUpdate() > 0) {
				result.setId(1);
				result.setMsg("7to paso ejecutado con exito");
			} else {
				result.setId(0);
				result.setMsg("7to paso ejecutado con error");
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

	public ResultDTO getAUFNRFromHu(String hu, String werks) {

		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		resultDT.setTypeS("");

		try {
			stmn = con.prepareStatement(GET_AUFNR_FROM_HU);

			stmn.setString(1, hu);
			stmn.setString(2, werks);
			LOCATION.error("Werks daoUbicacionPT: " + werks);
			rs = stmn.executeQuery();
			if (rs.next()) {

				resultDT.setTypeS(rs.getString("VPOBJKEY"));

				resultDT.setId(1);
				resultDT.setMsg("Orden de producción encontrada");

			} else {
				resultDT.setId(0);
				resultDT.setMsg("Orden de producción no encontrada vía HU y con centro de montacarguista: " + werks);
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

	public OrdenProduccionInputDTO validarOrdenEnPickin(String entry) {

		OrdenProduccionInputDTO orden = new OrdenProduccionInputDTO();
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
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		orden.setMateriales(map);
		orden.setResultDT(result);
		return orden;
	}

// TODO Remove unused code found by UCDetector
// 	public ResultDTO limpiaPendientesXUsuario(String vbeln, String user) {
// 		ResultDTO result = new ResultDTO();
// 		Connection con = dbConnection.createConnection();
// 		PreparedStatement stmn = null;
// 
// 		try {
// 
// 			LOCATION.error("Limpia pendientes DAO :" + vbeln);
// 			stmn = con.prepareStatement(LIMPIA_PENDIENTE_USUARIO);
// 			stmn.setString(1, vbeln);
// 			stmn.setString(2, user);
// 
// 			if (stmn.executeUpdate() > 0) {
// 				result.setId(1);
// 				result.setMsg("Limpieza ejecutada con exito");
// 			} else {
// 				result.setId(1);
// 				result.setMsg("Limpieza ejecutada con exito");
// 			}
// 		} catch (SQLException e) {
// 			result.setId(2);
// 			result.setMsg(e.getMessage());
// 		} catch (Exception en) {
// 			result.setId(2);
// 			result.setMsg(en.getMessage());
// 		} finally {
// 			try {
// 				// DBConnection.closeConnection(con);
// 			} catch (Exception e) {
// 				result.setId(2);
// 				result.setMsg(e.getMessage());
// 			}
// 		}
// 		return result;
// 	}

	public OrdenProduccionDTO detalleOrdenProduccionSoloCabecera(String aufnr, String werks) {
		OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();

		CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
		List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

		OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
		List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			LOCATION.error("AUFNR_en DAO: " + aufnr);
			stmn = con.prepareStatement(DATOS_ORDEN);
			stmn.setString(1, aufnr);

			rs = stmn.executeQuery();
			int cant = 0;

			while (rs.next()) {
				LOCATION.error("while++");
				cant++;
				OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

				item.setPosicion(rs.getString("POSNR"));
				item.setMaterial(rs.getString("MATNR"));
				item.setDescripcion(rs.getString("MAKTX"));
				item.setCajas(rs.getString("PSMNG"));
				item.setCajasAsignadas(rs.getString("WEMNG"));
				item.setMe(rs.getString("MEINS"));

				detalle.add(item);

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
				// DBConnection.closeConnection(con);
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

}
