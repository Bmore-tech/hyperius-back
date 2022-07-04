package com.bmore.hyperius.web.repository.old;

import java.io.File;
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

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDetalleDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDetalleDTOItem;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregasTransportesDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDetalleDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDetalleItemDTO;
import com.bmore.hyperius.web.dto.FTPConfDTO;
import com.bmore.hyperius.web.dto.HUsEnTransporteDetalleDTO;
import com.bmore.hyperius.web.dto.HusEnTransporteDTO;
import com.bmore.hyperius.web.dto.HusEnTransporteDetalleDTOItem;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ListaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.UsuarioDTO;
import com.bmore.hyperius.web.dto.UsuarioItemDTO;
import com.bmore.hyperius.web.dto.UsuariosDTO;
import com.bmore.hyperius.web.utils.Utils;

public class SupervisorUtilsRepository {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());
	
	private String pathOut = "E:" + File.separator + "RepoSentinel" + File.separator + "final";

	// static String OBTIENE_ENTREGAS =
	// "select  DISTINCT(LIKP.VBELN),LIKP.LFART, VTTP.TKNUM, zContingencia.ENTREGA AS CONTABILIZADA "
	// +
	// " from LIKP LIKP WITH(NOLOCK) left join HCMDB.dbo.VTTP VTTP WITH(NOLOCK) on LIKP.VBELN = VTTP.VBELN "
	// +
	// " inner join HCMDB.dbo.LIPS LIPS WITH(NOLOCK) on LIKP.VBELN = LIPS.VBELN "
	// +
	// " left join HCMDB.dbo.zContingencia zContingencia WITH(NOLOCK) on LIKP.VBELN = zContingencia.ENTREGA"
	// + " and (zContingencia.IDPROC='9' or zContingencia.IDPROC='4') "
	// + " where LIPS.WERKS= ?";

	static String OBTIENE_ENTREGAS = "SELECT VBELN, LFART, TKNUM, CONTABILIZADA, EDI FROM VS_BCPS_SUPERVISOR_UTILS_ENTREGAS WITH(NOLOCK) WHERE WERKS = ?";

	static String OBTIENE_ENTREGA = "select distinct(VTTP.VBELN),LIKP.LFART, VTTP.TKNUM,zContingencia.ENTREGA AS CONTABILIZADA FROM VTTP WITH(NOLOCK) "
			+ " left join HCMDB.dbo.zContingencia zContingencia WITH(NOLOCK) on VTTP.VBELN = zContingencia.ENTREGA and"
			+ " (zContingencia.IDPROC='9' or zContingencia.IDPROC='4') "
			+ " inner join HCMDB.dbo.LIPS LIPS with(nolock) on VTTP.VBELN = LIPS.VBELN "
			+ " inner join HCMDB.dbo.LIKP LIKP with(nolock) on VTTP.VBELN = LIKP.VBELN "
			+ " where TKNUM= ? and LIPS.WERKS= ?";

	// static String OBTIENE_ENTREGAS_AGENCIAS =
	// "select distinct (VTTP.VBELN),VTTP.TKNUM from VTTP left join LIPS on dbo.VTTP.VBELN = LIPS.VBELN where LIPS.WERKS = ?";

	static String OBTIENE_ENTREGAS_AGENCIAS = "SELECT TKNUM, VBELN, VBELV FROM VS_BCPS_ENTREGAS_AGENCIAS WITH(NOLOCK) WHERE WERKS = ?";

	static String OBTIENE_CARRILES_BLOQUEADOS = "SELECT VBELN, LGNUM, LGTYP, LGPLA, NOMBRE, PROCESO, MARCA_TIEMPO FROM VS_BCPS_UTILS_BLOQUEO WHERE werks = ?";

	static String OBTIENE_ENTREGA_DETALLE_AGENCIA = "select POSNR, MATNR, ARKTX, PSTYV from HCMDB.dbo.LIPS with(nolock) where VBELN= ? and "
			+ "PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and convert(decimal(18, 3), LFIMG) > 0";

	static String OBTIENE_ENTREGA_DETALLE_AGENCIA_CANTIDAD_X_MATNR = "select sum(convert(decimal(18, 3), LFIMG)), MEINS from HCMDB.dbo.lips WITH(NOLOCK) "
			+ "where VBELN = ? and MATNR = ? and PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1)  "
			+ "and convert(decimal(18, 3), LFIMG) > 0 group by MEINS";

	static String OBTIENE_INVENTARIO = "select MATNR,VERME,COUNT(MATNR) as CANTIDAD_HUS, LGNUM,LGTYP,LGPLA from HCMDB.dbo.LQUA WITH(NOLOCK)"
			+ " where WERKS=? and SKZUA is null group by MATNR, VERME, LGNUM, LGTYP, LGPLA order by MATNR";

	static String OBTIENE_INVENTARIO_LOTES = "select MATNR,VERME,COUNT(MATNR) as CANTIDAD_HUS, LGNUM,LGTYP,LGPLA,CHARG from HCMDB.dbo.LQUA WITH(NOLOCK)"
			+ " where WERKS=? and SKZUA is null group by MATNR, VERME, LGNUM, LGTYP, LGPLA,CHARG order by MATNR";

	static String CARRILES_BLOQUEADOS = "SELECT DISTINCT LGNUM, LGTYP, LGPLA, "
			+ "husTransporte = (SELECT COUNT(*) FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks AND VBELN = ZPEE.VBELN AND LGNUM=ZPEE.LGNUM AND LGTYP=ZPEE.LGTYP AND LGPLA=ZPEE.LGPLA AND (status = '1' or (EXIDV is not null and status is null))), "
			+ "husAsignadas = (SELECT COUNT(*) FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks AND VBELN = ZPEE.VBELN AND LGNUM=ZPEE.LGNUM AND LGTYP=ZPEE.LGTYP AND LGPLA=ZPEE.LGPLA) "
			+ "FROM ZPickingEntregaEntrante ZPEE WITH(NOLOCK) "
			+ "WHERE idProceso = ?  AND VBELN = ? AND werks = ? and (Status is null or Status ='1') order by LGNUM,LGTYP,LGPLA";

	static String MATERIALES_BLOQUEADOS = "select distinct MATNR, "
			+ "husTransporte=(SELECT COUNT(*) FROM ZPickingVidrio WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks  AND VBELN = ZPEE.VBELN AND status = '1'), "
			+ "husAsignadas=(SELECT COUNT(*) FROM ZPickingVidrio WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks  AND VBELN = ZPEE.VBELN ) "
			+ "from ZPickingVidrio ZPEE with(nolock) where VBELN = ? "
			+ "and WERKS = ? and idProceso = ? and (Status is null or Status ='1') order by MATNR";

	static String LIBERA_CARRIL_PENDIENTES = "exec sp_bcps_wm_libera_pendientes_carril ?,?,?,?,?,?,?,?";

	static String HUS_WM_BLOQUEADOS = "select EXIDV, Status, usuarioMontacarga, matnr from ZPickingEntregaEntrante with(nolock) where VBELN = ? and idProceso = ? and werks = ? and LGNUM= ? AND LGTYP= ? AND LGPLA= ? order by Status desc";

	static String HUS_IM_BLOQUEADOS = "select EXIDV, Status, usuarioMontacarga, matnr from ZPickingVidrio with(nolock) where VBELN = ? and idProceso = ? and werks= ? and MATNR= ? order by Status desc";

	static String EXISTE_CARRIL = "SELECT DISTINCT LAGP.LGNUM,ZPEE.IDPROCESO,ZPEE.USUARIOSUPERVISOR,ZUSUARIO.NOMBRE  FROM LAGP LAGP WITH(NOLOCK) "
			+ "LEFT JOIN ZPICKINGENTREGAENTRANTE ZPEE WITH(NOLOCK) ON LAGP.LGNUM = ZPEE.LGNUM AND "
			+ "LAGP.LGTYP = ZPEE.LGTYP AND LAGP.LGPLA = ZPEE.LGPLA AND (STATUS IS NULL OR STATUS ='1') "
			+ "LEFT JOIN ZUSUARIO ZUSUARIO WITH(NOLOCK) ON ZPEE.USUARIOSUPERVISOR = ZUSUARIO.IDRED "
			+ "WHERE LAGP.LGNUM = ? AND LAGP.LGTYP = ? AND LAGP.LGPLA = ?";

	static String GET_CENTROS = "SELECT DISTINCT WERKS FROM ZCENTROSBCPS WITH(NOLOCK)";

	static String GET_FTP_CONF = "select IP,PORT,[USER],[PASSWORD],FOLDER from  TB_BCPS_SFTP_DATAMART with(nolock)";

	static String GET_TABLAS_LEGADO = "select NOMBRE_TABLA,CERVECERAS,ENVASES,VIDRIERAS from TB_BCPS_TABLE_NAME with(nolock) order by  NOMBRE_TABLA";

	static String GET_ULTIMO_CAMPO = "exec SP_BCPS_GET_OBJECT_ID ?,?,?";

	static String ELIMINA_DUPLICADOS = "exec sp_bcps_utils_elimina_duplicados_v2";

	static String INITIAL_SNAPSHOT = "exec SP_BCPS_INITIAL_SNAPSHOT ?";

	static String VALIDA_INICIO_BCPS = "select top 1 * from zContingencia with(nolock) where CENTRO = ? ";

	static String BUSCAR_USUARIO = "SELECT ZUSR.NOMBRE, ZUSR.WERKS, ZCBP.descripcion from ZUSUARIO ZUSR WITH(NOLOCK) "
			+ "INNER JOIN zCentrosBCPS ZCBP WITH(NOLOCK) ON ZUSR.WERKS = ZCBP.werks "
			+ "where idRed = ? ";

	static String SP_USUARIOS = "exec sp_bcps_utils_valid_user ?,?,?,?,?,?";

	static String SP_ELIMINA_DATOS_X_CENTRO = "exec sp_bcps_utils_delete_data_by_werks ?,?";

	private static final String REMOVEQUALITY = "exec SP_BCPS_BLOCK_QUALITY ";

	public EntregasTransportesDTO obtieneEntrega(String tknum, String werks) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		EntregasTransportesDTO entregasTransportesDTO = new EntregasTransportesDTO();
		EntregasTransportesDetalleItemDTO entregasTransportesDetalleDTOItem = new EntregasTransportesDetalleItemDTO();
		List<EntregasTransportesDetalleDTO> entregasTransportesDetalleDTOItemList = new ArrayList<EntregasTransportesDetalleDTO>();

		try {

			stmnt2 = con.prepareStatement(OBTIENE_ENTREGA);

			stmnt2.setString(1, tknum);
			stmnt2.setString(2, werks);
			rs2 = stmnt2.executeQuery();

			if (rs2.next()) {

				EntregasTransportesDetalleDTO entregasTransportesDetalleDTO = new EntregasTransportesDetalleDTO();

				entregasTransportesDetalleDTO.setTknum(rs2.getString("TKNUM"));
				entregasTransportesDetalleDTO.setVblenEntrante(rs2
						.getString("VBELN"));
				entregasTransportesDetalleDTO.setStatus(rs2
						.getString("CONTABILIZADA"));

				entregasTransportesDetalleDTO.setLfart(rs2.getString("LFART"));

				result.setId(1);
				result.setMsg("Registro encontrado");
				entregasTransportesDetalleDTOItemList
						.add(entregasTransportesDetalleDTO);
			} else {
				result.setId(2);
				result.setMsg("Registro NO Encontrado");
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

		entregasTransportesDetalleDTOItem
				.setItem(entregasTransportesDetalleDTOItemList);
		entregasTransportesDTO.setItems(entregasTransportesDetalleDTOItem);
		entregasTransportesDTO.setResultDT(result);

		return entregasTransportesDTO;
	}

	public EntregasTransportesDTO obtieneEntregas(String werks) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		EntregasTransportesDTO entregasTransportesDTO = new EntregasTransportesDTO();
		EntregasTransportesDetalleItemDTO entregasTransportesDetalleDTOItem = new EntregasTransportesDetalleItemDTO();
		List<EntregasTransportesDetalleDTO> entregasTransportesDetalleDTOItemList = new ArrayList<EntregasTransportesDetalleDTO>();

		result.setId(2);
		result.setMsg("Registros no encontrados");

		try {

			stmnt2 = con.prepareStatement(OBTIENE_ENTREGAS);

			stmnt2.setString(1, werks);

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				EntregasTransportesDetalleDTO entregasTransportesDetalleDTO = new EntregasTransportesDetalleDTO();

				entregasTransportesDetalleDTO.setTknum(rs2.getString("TKNUM"));
				entregasTransportesDetalleDTO.setVblenEntrante(rs2
						.getString("VBELN"));
				entregasTransportesDetalleDTO.setStatus(rs2
						.getString("CONTABILIZADA"));

				entregasTransportesDetalleDTO.setLfart(rs2.getString("LFART"));
				entregasTransportesDetalleDTO.setEdi(rs2.getString("EDI"));
				result.setId(1);
				result.setMsg("Registro encontrado");
				entregasTransportesDetalleDTOItemList
						.add(entregasTransportesDetalleDTO);
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

		entregasTransportesDetalleDTOItem
				.setItem(entregasTransportesDetalleDTOItemList);
		entregasTransportesDTO.setItems(entregasTransportesDetalleDTOItem);
		entregasTransportesDTO.setResultDT(result);

		return entregasTransportesDTO;
	}

	public InventarioDTO obtieneInventario(String werks) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		InventarioDTO inventarioDTO = new InventarioDTO();
		InventarioDetalleDTOItem inventarioDetalleDTOItem = new InventarioDetalleDTOItem();
		List<InventarioDetalleDTO> inventarioDetalleDTOItemList = new ArrayList<InventarioDetalleDTO>();

		result.setId(2);
		result.setMsg("Registros no encontrados");

		try {

			stmnt2 = con.prepareStatement(OBTIENE_INVENTARIO);

			stmnt2.setString(1, werks);

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				InventarioDetalleDTO inventarioDetalleDTO = new InventarioDetalleDTO();

				inventarioDetalleDTO.setMatnr(rs2.getString("MATNR"));
				inventarioDetalleDTO.setVerme(rs2.getString("VERME"));
				inventarioDetalleDTO.setCantidadHus(rs2
						.getString("CANTIDAD_HUS"));
				inventarioDetalleDTO.setLgnum(rs2.getString("LGNUM"));
				inventarioDetalleDTO.setLgtyp(rs2.getString("LGTYP"));
				inventarioDetalleDTO.setLgpla(rs2.getString("LGPLA"));

				result.setId(1);
				result.setMsg("Registro encontrado");
				inventarioDetalleDTOItemList.add(inventarioDetalleDTO);
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

		inventarioDetalleDTOItem.setItem(inventarioDetalleDTOItemList);
		inventarioDTO.setItems(inventarioDetalleDTOItem);
		inventarioDTO.setResultDT(result);

		return inventarioDTO;
	}

	public InventarioDTO obtieneInventarioLotes(String werks) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		InventarioDTO inventarioDTO = new InventarioDTO();
		InventarioDetalleDTOItem inventarioDetalleDTOItem = new InventarioDetalleDTOItem();
		List<InventarioDetalleDTO> inventarioDetalleDTOItemList = new ArrayList<InventarioDetalleDTO>();

		result.setId(2);
		result.setMsg("Registros no encontrados");

		try {

			stmnt2 = con.prepareStatement(OBTIENE_INVENTARIO_LOTES);

			stmnt2.setString(1, werks);

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				InventarioDetalleDTO inventarioDetalleDTO = new InventarioDetalleDTO();

				inventarioDetalleDTO.setMatnr(rs2.getString("MATNR"));
				inventarioDetalleDTO.setVerme(rs2.getString("VERME"));
				inventarioDetalleDTO.setCantidadHus(rs2
						.getString("CANTIDAD_HUS"));
				inventarioDetalleDTO.setLgnum(rs2.getString("LGNUM"));
				inventarioDetalleDTO.setLgtyp(rs2.getString("LGTYP"));
				inventarioDetalleDTO.setLgpla(rs2.getString("LGPLA"));
				inventarioDetalleDTO.setCharg(rs2.getString("CHARG"));

				result.setId(1);
				result.setMsg("Registro encontrado");
				inventarioDetalleDTOItemList.add(inventarioDetalleDTO);
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

		inventarioDetalleDTOItem.setItem(inventarioDetalleDTOItemList);
		inventarioDTO.setItems(inventarioDetalleDTOItem);
		inventarioDTO.setResultDT(result);

		return inventarioDTO;
	}

	public HusEnTransporteDTO obtieneCarrilesBloqueados(String proceso,
			String vbeln, String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
		HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItem = new HusEnTransporteDetalleDTOItem();
		List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<HUsEnTransporteDetalleDTO>();

		result.setId(2);
		result.setMsg("No hay carriles bloqueados");

		try {

			stmnt2 = con.prepareStatement(CARRILES_BLOQUEADOS);

			stmnt2.setString(1, proceso);
			stmnt2.setString(2, vbeln);
			stmnt2.setString(3, werks);

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO = new HUsEnTransporteDetalleDTO();

				husEnTransporteDetalleDTO.setLgnum(rs2.getString("LGNUM"));
				husEnTransporteDetalleDTO.setLgtyp(rs2.getString("LGTYP"));
				husEnTransporteDetalleDTO.setLgpla(rs2.getString("LGPLA"));

				husEnTransporteDetalleDTO.setHusAsignadas(rs2
						.getString("husAsignadas"));

				husEnTransporteDetalleDTO.setHusEnTransporte(rs2
						.getString("husTransporte"));

				husEnTransporteDetalleDTO.setVbeln(vbeln);

				result.setId(1);
				result.setMsg("Registro encontrado");
				husEnTransporteDetalleDTOItemList
						.add(husEnTransporteDetalleDTO);
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

		husEnTransporteDetalleDTOItem
				.setItem(husEnTransporteDetalleDTOItemList);
		husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItem);
		husEnTransporteDTO.setResultDT(result);

		return husEnTransporteDTO;
	}

	public HusEnTransporteDTO obtieneMaterialesBloqueados(String proceso,
			String vbeln, String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
		HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItem = new HusEnTransporteDetalleDTOItem();
		List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<HUsEnTransporteDetalleDTO>();

		result.setId(2);
		result.setMsg("No hay carriles bloqueados");

		try {

			stmnt2 = con.prepareStatement(MATERIALES_BLOQUEADOS);

			stmnt2.setString(1, vbeln);
			stmnt2.setString(2, werks);
			stmnt2.setString(3, proceso);

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO = new HUsEnTransporteDetalleDTO();

				husEnTransporteDetalleDTO.setMatnr(rs2.getString("MATNR"));
				husEnTransporteDetalleDTO.setHusAsignadas(rs2
						.getString("husAsignadas"));
				husEnTransporteDetalleDTO.setHusEnTransporte(rs2
						.getString("husTransporte"));

				result.setId(1);
				result.setMsg("Registro encontrado");
				husEnTransporteDetalleDTOItemList
						.add(husEnTransporteDetalleDTO);
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

		husEnTransporteDetalleDTOItem
				.setItem(husEnTransporteDetalleDTOItemList);
		husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItem);
		husEnTransporteDTO.setResultDT(result);

		return husEnTransporteDTO;
	}

	public ResultDTO limpiaCarril(CarrilUbicacionDTO carril) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(LIBERA_CARRIL_PENDIENTES);

			LOCATION.error("Carril: " + carril.getEntrega() + " "
					+ carril.getIdProceso() + " " + carril.getLgnum() + " "
					+ carril.getLgtyp() + " " + carril.getLgpla()
					+ carril.getMaterial() + " " + carril.getTipoAlmacen());

			callableStatement.setString(1, carril.getEntrega());
			callableStatement.setString(2, carril.getIdProceso());
			callableStatement.setString(3, carril.getLgnum());
			callableStatement.setString(4, carril.getLgtyp());
			callableStatement.setString(5, carril.getLgpla());
			callableStatement.setString(6, carril.getMaterial());
			callableStatement.setString(7, carril.getTipoAlmacen());
			callableStatement.registerOutParameter(8, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(8);
			result.setId(id);

		} catch (SQLException e) {
			result.setId(800);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(800);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(800);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public HusEnTransporteDTO obtieneHusBloqueados(
			HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
		HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItems = new HusEnTransporteDetalleDTOItem();
		List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<HUsEnTransporteDetalleDTO>();

		result.setId(2);
		result.setMsg("No hay hus en transporte");

		try {

			stmnt2 = con.prepareStatement(HUS_WM_BLOQUEADOS);

			stmnt2.setString(1, husEnTransporteDetalleDTO.getVbeln());
			stmnt2.setString(2, husEnTransporteDetalleDTO.getIdProceso());
			stmnt2.setString(3, werks);
			stmnt2.setString(4, husEnTransporteDetalleDTO.getLgnum());
			stmnt2.setString(5, husEnTransporteDetalleDTO.getLgtyp());
			stmnt2.setString(6, husEnTransporteDetalleDTO.getLgpla());

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				HUsEnTransporteDetalleDTO husEnTransporteDetalleDTOItem = new HUsEnTransporteDetalleDTO();

				husEnTransporteDetalleDTOItem.setHu(rs2.getString("exidv"));
				husEnTransporteDetalleDTOItem
						.setStatus(rs2.getString("status"));
				husEnTransporteDetalleDTOItem.setUsuarioMontacarguista(rs2
						.getString("usuarioMontacarga"));
				husEnTransporteDetalleDTOItem.setMatnr(rs2.getString("matnr"));

				husEnTransporteDetalleDTOItem
						.setVbeln(husEnTransporteDetalleDTO.getVbeln());

				result.setId(1);
				result.setMsg("Registro encontrado");
				husEnTransporteDetalleDTOItemList
						.add(husEnTransporteDetalleDTOItem);
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

		husEnTransporteDetalleDTOItems
				.setItem(husEnTransporteDetalleDTOItemList);
		husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItems);
		husEnTransporteDTO.setResultDT(result);

		return husEnTransporteDTO;
	}

	public HusEnTransporteDTO obtieneHusIMBloqueados(
			HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
		HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItems = new HusEnTransporteDetalleDTOItem();
		List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<HUsEnTransporteDetalleDTO>();

		result.setId(2);
		result.setMsg("No hay materiales bloqueados");

		try {

			stmnt2 = con.prepareStatement(HUS_IM_BLOQUEADOS);

			stmnt2.setString(1, husEnTransporteDetalleDTO.getVbeln());
			stmnt2.setString(2, husEnTransporteDetalleDTO.getIdProceso());
			stmnt2.setString(3, werks);
			stmnt2.setString(4, husEnTransporteDetalleDTO.getMatnr());

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				HUsEnTransporteDetalleDTO husEnTransporteDetalleDTOItem = new HUsEnTransporteDetalleDTO();

				husEnTransporteDetalleDTOItem.setHu(rs2.getString("exidv"));
				husEnTransporteDetalleDTOItem
						.setStatus(rs2.getString("status"));
				husEnTransporteDetalleDTOItem.setUsuarioMontacarguista(rs2
						.getString("usuarioMontacarga"));
				husEnTransporteDetalleDTOItem.setMatnr(rs2.getString("matnr"));

				husEnTransporteDetalleDTOItem
						.setVbeln(husEnTransporteDetalleDTO.getVbeln());

				result.setId(1);
				result.setMsg("Registro encontrado");
				husEnTransporteDetalleDTOItemList
						.add(husEnTransporteDetalleDTOItem);
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

		husEnTransporteDetalleDTOItems
				.setItem(husEnTransporteDetalleDTOItemList);
		husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItems);
		husEnTransporteDTO.setResultDT(result);

		return husEnTransporteDTO;
	}

	public ResultDTO validarCarril(CarrilUbicacionDTO carril) {

		Connection con = DBConnection.createConnection();

		ResultDTO resultDT = new ResultDTO();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {

			stmn = con.prepareStatement(EXISTE_CARRIL);

			stmn.setString(1, carril.getLgnum());
			stmn.setString(2, carril.getLgtyp());
			stmn.setString(3, carril.getLgpla());

			rs = stmn.executeQuery();
			if (rs.next()) {

				if (rs.getString("idProceso") == null
						|| rs.getString("idProceso").equals(
								carril.getIdProceso())) {

					resultDT.setId(1);
					resultDT.setMsg("El carril existe.");

				} else {
					resultDT.setId(3);
					// resultDT
					// .setMsg("Existe carril, pero está siendo utilizado por otro proceso, elija otro. Usuario: "
					// + rs.getString("usuarioSupervisor")
					// + " - "
					// + rs.getString("nombre"));

					resultDT
							.setMsg("El carril esta siendo utilizado por el usuario: "
									+ rs.getString("usuarioSupervisor")
									+ " - "
									+ rs.getString("nombre"));
				}

			} else {
				resultDT.setId(2);
				resultDT.setMsg("El carril no existe");
			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg("Error SQL: " + e.toString());
			LOCATION.error("SQLException: " + e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			LOCATION.error("Exception: " + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
			}
		}

		return resultDT;
	}

	public ListaDTO getCentros() {

		PreparedStatement stm = null;
		ResultSet rs = null;

		ListaDTO listaDTO = new ListaDTO();

		Connection con = DBConnection.createConnection();
		List<String> centros = new ArrayList<String>();
		ResultDTO resultDT = new ResultDTO();

		try {
			stm = con.prepareStatement(GET_CENTROS);

			rs = stm.executeQuery();
			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuperar los centros");

			while (rs.next()) {
				centros.add(rs.getString("WERKS"));
				resultDT.setId(1);
				resultDT.setMsg("Centros recuperados con exito");
			}

		} catch (SQLException e) {
			LOCATION.error("SQLException : " + e.toString());
			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuperar los centros: "
					+ e.toString());
		} catch (Exception e) {
			LOCATION.error("Exception1 : " + e.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception2 : " + e.toString());
				resultDT.setId(0);
				resultDT.setMsg("No fue posible recuperar los centros: "
						+ e.toString());
			}
		}

		listaDTO.setLista(centros);
		listaDTO.setResultDT(resultDT);

		return listaDTO;
	}

	public FTPConfDTO getFTPConf() {

		FTPConfDTO ftpConf = new FTPConfDTO();
		ResultDTO resultDT = new ResultDTO();

		PreparedStatement stm = null;
		ResultSet rs = null;

		Connection con = DBConnection.createConnection();
		try {
			stm = con.prepareStatement(GET_FTP_CONF);

			rs = stm.executeQuery();

			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuper la configuración del FTP.");

			if (rs.next()) {

				ftpConf.setIp(rs.getString("IP"));
				ftpConf.setPassword(rs.getString("PASSWORD"));
				ftpConf.setPuerto(rs.getString("PORT"));
				ftpConf.setUser(rs.getString("USER"));
				ftpConf.setFolder(rs.getString("FOLDER"));

				resultDT.setId(1);
				resultDT.setMsg("Configuración FTP recuperada con exito.");

			}

		} catch (SQLException e) {
			LOCATION.error("SQLException : " + e.toString());
			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuper la configuración del FTP: "
					+ e.toString());

		} catch (Exception e) {

			LOCATION.error("Exception1 : " + e.toString());
			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuper la configuración del FTP: "
					+ e.toString());

		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception2 : " + e.toString());
				resultDT.setId(0);
				resultDT
						.setMsg("No fue posible recuper la configuración del FTP.: "
								+ e.toString());
			}
		}

		ftpConf.setResultDT(resultDT);
		return ftpConf;
	}

	public ListaDTO getTablas(String werks) {

		PreparedStatement stm = null;
		ResultSet rs = null;

		ListaDTO listaDTO = new ListaDTO();
		Connection con = DBConnection.createConnection();
		List<String> centros = new ArrayList<String>();
		ResultDTO resultDT = new ResultDTO();

		try {
			stm = con.prepareStatement(GET_TABLAS_LEGADO);

			rs = stm.executeQuery();

			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuperar las tablas");

			while (rs.next()) {

				if ( (werks.startsWith("PC") || 
						(werks.startsWith("PA")))
						&& rs.getBoolean("CERVECERAS") == true) {

					LOCATION
							.error("ENTRE PC: " + rs.getString("NOMBRE_TABLA"));
					centros.add(rs.getString("NOMBRE_TABLA"));
					resultDT.setId(1);
					resultDT.setMsg("Las tablas fueron recuperadas con éxito");

				} else if (werks.startsWith("PV")
						&& rs.getBoolean("VIDRIERAS") == true) {

					LOCATION
							.error("ENTRE PV: " + rs.getString("NOMBRE_TABLA"));

					centros.add(rs.getString("NOMBRE_TABLA"));
					resultDT.setId(1);
					resultDT.setMsg("Las tablas fueron recuperadas con éxito");

				} else if ((werks.startsWith("EM") || werks.startsWith("TM"))
						&& rs.getBoolean("ENVASES") == true) {

					LOCATION
							.error("ENTRE EM: " + rs.getString("NOMBRE_TABLA"));

					centros.add(rs.getString("NOMBRE_TABLA"));
					resultDT.setId(1);
					resultDT.setMsg("Las tablas fueron recuperadas con éxito");

				}

			}

		} catch (SQLException e) {
			LOCATION.error("SQLException : " + e.toString());
			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuperar las tablas: "
					+ e.toString());
		} catch (Exception e) {
			LOCATION.error("Exception1 : " + e.toString());
			resultDT.setId(0);
			resultDT.setMsg("No fue posible recuperar las tablas: "
					+ e.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception2 : " + e.toString());
				resultDT.setId(0);
				resultDT.setMsg("No fue posible recuperar las tablas: "
						+ e.toString());
			}
		}

		listaDTO.setLista(centros);
		listaDTO.setResultDT(resultDT);

		return listaDTO;
	}

	public ResultDTO bulk(String tabla, String werks) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement st2 = null;
		PreparedStatement upd = null;

		String tablaSQL = "";
		String txtTabla = "";
		String campo = "";

		CallableStatement stm = null;
		boolean integrity_constraint = false;
		try {

			/**
			 * Tabla especial de ultima HU generada en BCPS, si no existe o la
			 * HU en la tabla es menor a la hu que baja en el TXT se afecta la
			 * tabla
			 **/

			if (tabla.equals("ZPAITT_CONT_HU")) {

				integrity_constraint = true;
				tablaSQL = "TB_BCPS_NEW_HU";
				txtTabla = tabla;

			} else if (tabla.equals("ZPAITT_FACTURA")) {

				integrity_constraint = Boolean.TRUE;
				tablaSQL = "TB_BCPS_ZFACT";
				txtTabla = tabla;

			} else if (tabla.equals("ZPAITT_ENTREGA")) {

				integrity_constraint = Boolean.TRUE;
				tablaSQL = "TB_BCPS_NEW_VBELN";
				txtTabla = tabla;

			} else if (tabla.equals("ZPAITT_HU")) {

				tablaSQL = "ZPAITT_HU_EXT";
				txtTabla = tabla;

			} else if (tabla.equals("ZPAITT_PALL_EYT")) {

				tablaSQL = "ZPAITT_PALLETOBR";
				txtTabla = tabla;

			} else {

				tablaSQL = tabla;
				txtTabla = tablaSQL;

			}

			stm = con.prepareCall(GET_ULTIMO_CAMPO);

			stm.setString(1, tablaSQL);
			stm.registerOutParameter(2, java.sql.Types.VARCHAR);
			stm.registerOutParameter(3, java.sql.Types.INTEGER);

			stm.execute();

			if (stm.getInt(3) == 1) {

				campo = stm.getString(2);

				try {

					st2 = con
							.prepareStatement("bulk insert "
									+ tablaSQL
									+ " from  '" + pathOut + "_"
									+ txtTabla
									+ "_"
									+ werks
									+ ".txt' "
									+ "with (FIELDTERMINATOR = '|-|', KEEPIDENTITY, KEEPNULLS, ROWTERMINATOR ='\n', FIRSTROW=2, CODEPAGE = 'ACP')");
												// , CODEPAGE = 'ACP'
					st2.executeUpdate();

					if (campo.equals("SKZUA") || campo.equals("HU_LGORT")) {

						upd = con.prepareStatement("update HCMDB.dbo."
								+ tablaSQL + " set " + campo
								+ " = case when len(" + campo
								+ ") > 1 then replace(" + campo
								+ ",char(13),'') else replace(" + campo
								+ ",char(13),null) end  where " + campo
								+ " NOT IN ('A','X') and " + campo
								+ " is not null");
						upd.executeUpdate();

					} else {

						upd = con.prepareStatement("update HCMDB.dbo."
								+ tablaSQL + " set " + campo
								+ " = case when len(" + campo + ") > 1"
								+ " then replace(" + campo
								+ ",char(13),'') else replace(" + campo
								+ ",char(13),null) end");
						upd.executeUpdate();

					}
					LOCATION.error("Tabla SQL: " + tablaSQL + "   TxtTabla: "
							+ txtTabla + "  Centro:" + werks);
					resultDT.setId(1);

				} catch (SQLException e) {

					if (integrity_constraint) {

						resultDT.setId(1);

					} else {

						LOCATION.error("ERROR BULK ->SQLException: "
								+ e.toString());
						resultDT.setId(2);
						resultDT.setMsg(e.getMessage());
					}
				} catch (Exception en) {
					LOCATION.error("ERROR BULK ->Exception: " + en.toString());
					resultDT.setId(2);
					resultDT.setMsg(en.getMessage());
				} finally {
					try {
						DBConnection.closeConnection(con);
					} catch (Exception e) {
						LOCATION.error("ERROR BULK ->Exception: "
								+ e.toString());

					}
				}
			} else {
				resultDT.setId(2);
				resultDT.setMsg("No se recupero el ultimo campo de la tabla:"
						+ tabla);
			}

		} catch (SQLException e) {
			LOCATION.error("SQLException : " + e.toString());
			resultDT.setId(2);
			resultDT.setMsg("No fue posible recuperar las tablas: "
					+ e.toString());
		} catch (Exception e) {
			LOCATION.error("Exception1 : " + e.toString());
			resultDT.setId(2);
			resultDT.setMsg("No fue posible recuperar las tablas: "
					+ e.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Exception2 : " + e.toString());

			}
		}
		return resultDT;

	}

	public void removeQuality() {
		Connection con = DBConnection.createConnection();
		CallableStatement callableStatement = null;
		try {
			callableStatement = con.prepareCall(REMOVEQUALITY);
			callableStatement.execute();
		} catch (SQLException e) {
			LOCATION.error(e.getMessage());
		} catch (Exception en) {
			LOCATION.error(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getMessage());
			}
		}
	}

	public ResultDTO eliminaDuplicados() {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(ELIMINA_DUPLICADOS);

			callableStatement.execute();

			result.setId(1);

		} catch (SQLException e) {
			result.setId(800);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(800);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(800);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}

	public ResultDTO validaInicioBCPS(String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		try {

			stmnt2 = con.prepareStatement(VALIDA_INICIO_BCPS);

			stmnt2.setString(1, werks);

			rs2 = stmnt2.executeQuery();

			if (rs2.next()) {

				result.setId(2);
				result.setMsg("BCPS inicio operaciones con el centro: " + werks
						+ ", ya no es posible cargar datos.");

			} else {

				result.setId(1);

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

	public UsuarioItemDTO buscarUsuario(String idUser) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;
		UsuarioItemDTO usuariItemDTO = new UsuarioItemDTO();
		UsuariosDTO usuariosDTO = new UsuariosDTO();

		List<UsuarioDTO> listaUsuarios = new ArrayList<UsuarioDTO>();

		try {

			stmnt2 = con.prepareStatement(BUSCAR_USUARIO);

			stmnt2.setString(1, idUser);

			rs2 = stmnt2.executeQuery();

			if (rs2.next()) {
				UsuarioDTO usuarioDTO = new UsuarioDTO();

				usuarioDTO.setIdUsuario(idUser);
				usuarioDTO.setName(rs2.getString("NOMBRE"));
				usuarioDTO.setWerks(rs2.getString("WERKS"));
				usuarioDTO.setDescWerks(rs2.getString("descripcion"));

				listaUsuarios.add(usuarioDTO);

				result.setId(1);
				result.setMsg("Usuario encontrado");

			} else {

				result.setId(2);
				result.setMsg("Usuario no encontrado.");

			}

		} catch (SQLException e) {
			LOCATION
					.error("Error SQLException buscarUsuario :" + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error Exception buscarUsuario :" + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error buscarUsuario :" + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		usuariItemDTO.setResult(result);
		usuariosDTO.setItem(listaUsuarios);
		usuariItemDTO.setItem(usuariosDTO);

		return usuariItemDTO;
	}

	public ResultDTO eliminarUsuario(String idUser) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;
		try {

			callableStatement = con.prepareCall(SP_USUARIOS);
			// exec sp_bcps_utils_valid_user @USERID, @WERKS, @NOMBRE,
			// @PASSWORD, @RETURN OUTPUT

			callableStatement.setString(1, idUser);
			callableStatement.setString(2, "");
			callableStatement.setString(3, "");
			callableStatement.setString(4, "");
			callableStatement.setInt(5, 1);
			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			result.setId(id);

		} catch (SQLException e) {
			LOCATION
					.error("Error SQLException buscarUsuario :" + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error Exception buscarUsuario :" + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error buscarUsuario :" + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return result;
	}

	public ResultDTO crearUsuario(UsuarioDTO user) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;
		try {

			callableStatement = con.prepareCall(SP_USUARIOS);
			// exec sp_bcps_utils_valid_user @USERID, @WERKS, @NOMBRE,
			// @PASSWORD, @RETURN OUTPUT

			callableStatement.setString(1, user.getIdUsuario());
			callableStatement.setString(2, user.getWerks());
			callableStatement.setString(3, user.getName());
			callableStatement.setString(4, "");
			callableStatement.setInt(5, 0);
			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			result.setId(id);

		} catch (SQLException e) {
			LOCATION
					.error("Error SQLException buscarUsuario :" + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error Exception buscarUsuario :" + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error buscarUsuario :" + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return result;
	}

	public ResultDTO modificarUsuario(UsuarioDTO user) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;
		try {

			callableStatement = con.prepareCall(SP_USUARIOS);
			// exec sp_bcps_utils_valid_user @USERID, @WERKS, @NOMBRE,
			// @PASSWORD, @RETURN OUTPUT

			callableStatement.setString(1, user.getIdUsuario());
			callableStatement.setString(2, user.getWerks());
			callableStatement.setString(3, user.getName());
			callableStatement.setString(4, "");
			callableStatement.setInt(5, 0);
			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			result.setId(id);

		} catch (SQLException e) {
			LOCATION
					.error("Error SQLException buscarUsuario :" + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error Exception buscarUsuario :" + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error buscarUsuario :" + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return result;
	}

	public ResultDTO limpiaTablasCentro(String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;
		try {

			callableStatement = con.prepareCall(SP_ELIMINA_DATOS_X_CENTRO);

			callableStatement.setString(1, werks);
			callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(2);
			result.setId(id);
			// result.setId(1);
			LOCATION.error("ID:" + result.getId());
		} catch (SQLException e) {
			LOCATION
					.error("Error SQLException buscarUsuario :" + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error Exception buscarUsuario :" + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error buscarUsuario :" + e.toString());

			}
		}

		return result;
	}

	public EntregasTransportesDTO obtieneEntregasAgencias(UsuarioDTO usuario) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		EntregasTransportesDTO entregasTransportesDTO = new EntregasTransportesDTO();
		EntregasTransportesDetalleItemDTO entregasTransportesDetalleDTOItem = new EntregasTransportesDetalleItemDTO();
		List<EntregasTransportesDetalleDTO> entregasTransportesDetalleDTOItemList = new ArrayList<EntregasTransportesDetalleDTO>();

		result.setId(2);
		result.setMsg("Registros no encontrados");

		try {

			stmnt2 = con.prepareStatement(OBTIENE_ENTREGAS_AGENCIAS);

			stmnt2.setString(1, usuario.getWerks());

			rs2 = stmnt2.executeQuery();

			while (rs2.next()) {

				EntregasTransportesDetalleDTO entregasTransportesDetalleDTO = new EntregasTransportesDetalleDTO();

				entregasTransportesDetalleDTO.setTknum(rs2.getString("TKNUM"));
				entregasTransportesDetalleDTO.setVblenEntrante(rs2
						.getString("VBELN"));
				entregasTransportesDetalleDTO.setVblenSaliente(rs2
						.getString("VBELV"));
				//entregasTransportesDetalleDTO.setLfart(rs2.getString("ORDEN"));

				result.setId(1);
				result.setMsg("Registro encontrado");
				entregasTransportesDetalleDTOItemList
						.add(entregasTransportesDetalleDTO);
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

		entregasTransportesDetalleDTOItem
				.setItem(entregasTransportesDetalleDTOItemList);
		entregasTransportesDTO.setItems(entregasTransportesDetalleDTOItem);
		entregasTransportesDTO.setResultDT(result);

		return entregasTransportesDTO;
	}

	public CarrilesBloqueadosDTO obtieneCarrilesBloqueados(UsuarioDTO usuario) {
		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stm = null;
		ResultSet rs = null;

		CarrilesBloqueadosDTO carrilesBloqueadosDTO = new CarrilesBloqueadosDTO();
		CarrilesBloqueadosDetalleDTOItem carrilesBloqueadosDetalleDTOItem = new CarrilesBloqueadosDetalleDTOItem();
		List<CarrilesBloqueadosDetalleDTO> carrilesBloqueadosDetalleDTOItemList = new ArrayList<CarrilesBloqueadosDetalleDTO>();

		result.setId(2);
		result.setMsg("Registros no encontrados");

		try {

			stm = con.prepareStatement(OBTIENE_CARRILES_BLOQUEADOS);

			stm.setString(1, usuario.getWerks());

			rs = stm.executeQuery();

			while (rs.next()) {

				CarrilesBloqueadosDetalleDTO carrilesBloqueadosDetalleDTO = new CarrilesBloqueadosDetalleDTO();

				// SELECT VBELN, LGNUM, LGTYP, LGPLA, NOMBRE, PROCESO,
				// MARCA_TIEMPO FROM VS_BCPS_UTILS_BLOQUEO
				carrilesBloqueadosDetalleDTO.setVbeln(rs.getString("VBELN"));
				carrilesBloqueadosDetalleDTO.setCarrilBloqueado(Utils.isNull(rs
						.getString("LGNUM"))
						+ " "
						+ Utils.isNull(rs.getString("LGTYP"))
						+ " "
						+ Utils.isNull(rs.getString("LGPLA")));
				carrilesBloqueadosDetalleDTO.setUser(rs.getString("NOMBRE"));
				carrilesBloqueadosDetalleDTO.setProceso(rs.getString("PROCESO"));
				carrilesBloqueadosDetalleDTO.setMarcaTiempo(rs.getString("MARCA_TIEMPO"));
				result.setId(1);
				result.setMsg("Registro encontrado");
				carrilesBloqueadosDetalleDTOItemList
						.add(carrilesBloqueadosDetalleDTO);
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

		carrilesBloqueadosDetalleDTOItem
				.setItem(carrilesBloqueadosDetalleDTOItemList);
		carrilesBloqueadosDTO.setItems(carrilesBloqueadosDetalleDTOItem);
		carrilesBloqueadosDTO.setResultDT(result);

		return carrilesBloqueadosDTO;
	}

	public EmbarqueDTO obtieneEntregasAgenciasDetalle(String vbeln) {

		ResultDTO result = new ResultDTO();
		EmbarqueDetalleDTOItem embarqueDetalleDTOItem = new EmbarqueDetalleDTOItem();

		List<EmbarqueDetalleDTO> items = new ArrayList<EmbarqueDetalleDTO>();

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		PreparedStatement stmn2 = null;

		ResultSet rs = null;
		ResultSet rs2 = null;

		EmbarqueDTO embarque = new EmbarqueDTO();

		try {

			stmn = con.prepareStatement(OBTIENE_ENTREGA_DETALLE_AGENCIA);
			LOCATION.error("VBLEN EN DAO:" + vbeln);
			stmn.setString(1, vbeln);
			rs = stmn.executeQuery();

			HashMap<String, String> map = new HashMap<String, String>();
			LOCATION.error("after execute query");

			while (rs.next()) {
				LOCATION.error("while");
				if (map.get(rs.getString("MATNR")) == null) {
					LOCATION.error("if");
					map.put(rs.getString("MATNR"), rs.getString("MATNR"));

					EmbarqueDetalleDTO item = new EmbarqueDetalleDTO();
					item.setMaterial(rs.getString("MATNR"));
					item.setPosicion(rs.getString("POSNR"));
					item.setDescripcion(rs.getString("ARKTX"));

					LOCATION.error("GET CAJAS");
					stmn2 = con
							.prepareStatement(OBTIENE_ENTREGA_DETALLE_AGENCIA_CANTIDAD_X_MATNR);
					stmn2.setString(1, vbeln);
					stmn2.setString(2, item.getMaterial());

					LOCATION.error("MATERIAL ->>>>>>>>>>>>>>: "
							+ item.getMaterial());

					rs2 = stmn2.executeQuery();

					LOCATION.error("stmn4.executeQuery() ->>>>>>>>>>>>>>: "
							+ item.getMaterial());

					if (rs2.next()) {

						try {
							item.setCajas(new BigDecimal(rs2.getString(1))
									.setScale(3, RoundingMode.HALF_UP)
									+ "");
						} catch (Exception e) {
							item.setCajas(rs2.getString(1));
						}

						item.setMe(rs2.getString(2));
					} else {
						item.setCajas("0");
					}

					LOCATION.error("Cajas asignadas");

					items.add(item);
					LOCATION.error("Item agregado");

				}

			}

			LOCATION.error("Size Lista " + items.size());
			if (items.size() > 0) {

				result.setId(1);
				result.setMsg("Detalle de entrega encontrado");
			} else {
				result.setId(2);
				result.setMsg("Detalle de entrega NO encontrado");
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

		embarqueDetalleDTOItem.setItem(items);

		embarque.setItems(embarqueDetalleDTOItem);
		embarque.setResultDT(result);

		return embarque;
	}

	public ResultDTO initialSnapshot(String werks) {

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(INITIAL_SNAPSHOT);

			callableStatement.setString(1, werks);
			callableStatement.execute();

			result.setId(1);

		} catch (SQLException e) {
			result.setId(800);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(800);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(800);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}
}
