package com.bmore.hyperius.web.repository.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CargaInformacionBodyDTO;
import com.bmore.hyperius.web.dto.CargaInformacionDTO;
import com.bmore.hyperius.web.dto.DescargaInformacionBodyDTO;
import com.bmore.hyperius.web.dto.DescargaInformacionDTO;
import com.bmore.hyperius.web.dto.PlaneacionBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

@Repository
public class CifrasControlRepositoryOld {
    
  @Autowired
  private DBConnection dbConnection;

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	public static final String GET_DESCARGA_INFORMACION = "SELECT zC.WERKS werks, "
			+ " (SELECT COUNT(distinct(TKNUM))  FROM VTTP VT WITH(NOLOCK) INNER JOIN  LIPS LP on VT.VBELN = LP.VBELN  WHERE LP.WERKS = zC.WERKS AND LP.WERKS = LP.XWERKS) as 'FoliosEmbarque', "
			+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN  "
			+ " WHERE lk.LFART != 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasSalientes', "
			+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN "
			+ " WHERE lk.LFART = 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasEntrantes', "
			+ " (SELECT COUNT(AUFNR) FROM AUFK WITH(NOLOCK) WHERE WERKS = zC.WERKS) as 'OrdenesProduccion', "
			+ " (SELECT COUNT(VK.EXIDV)  FROM VEKP VK WITH(NOLOCK) WHERE VK.WERKS = zC.WERKS AND vk.WERKS = vk.XWERKS) as 'UnidadesManipulacion' "
			+ " FROM zCentrosBCPS zC WHERE zC.WERKS LIKE 'PV%' "
			+ " UNION "
			+ " SELECT zC.WERKS werks, "
			+ " (SELECT COUNT(distinct(TKNUM))  FROM VTTP VT WITH(NOLOCK) INNER JOIN  LIPS LP on VT.VBELN = LP.VBELN  WHERE LP.WERKS = zC.WERKS AND LP.WERKS = LP.XWERKS) as 'FoliosEmbarque', "
			+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN  "
			+ " WHERE lk.LFART != 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasSalientes', "
			+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN "
			+ " WHERE lk.LFART = 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasEntrantes', "
			+ " (SELECT COUNT(AUFNR) FROM AUFK WITH(NOLOCK) WHERE WERKS = zC.WERKS) as 'OrdenesProduccion',  "
			+ " (SELECT COUNT(DISTINCT(LQ.LENUM)) FROM LQUA LQ WITH(NOLOCK) WHERE LQ.WERKS = zC.WERKS AND LQ.LENUM is not null) as 'UnidadesManipulacion' "
			+ " FROM zCentrosBCPS zC WHERE zC.WERKS NOT LIKE 'PV%'";

	public static final String GET_CARGA_INFORMACION = "SELECT zCo.WERKS, "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 2 and zCoi.CENTRO = zCo.WERKS) 'FolioRecibido', "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 10 and zCoi.CENTRO = zCo.WERKS) 'FolioEmbarcado',  "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE (zCoi.IDPROC = 9 OR zCoi.IDPROC = 29) and zCoi.CENTRO = zCo.WERKS) 'EmbarqueContabilizado', "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE (zCoi.IDPROC = 4 OR zCoi.IDPROC = 14) and zCoi.CENTRO = zCo.WERKS) 'RecepcionContabilizada',  "
			+ " (SELECT COUNT(DISTINCT(zCoi.ORDEN_PRODUCCION)) FROM zContingencia zCoi WITH(NOLOCK) WHERE zCoi.CENTRO = zCo.WERKS) 'OrdenesProduccion', "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 7 and zCoi.CENTRO = zCo.WERKS) 'HuProducidas',  "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 5 and zCoi.CENTRO = zCo.WERKS) 'HuConsumidas',  "
			+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 15 and zCoi.CENTRO = zCo.WERKS) 'HuTraslados' "
			+ " FROM zCentrosBCPS zCo WITH(NOLOCK) order by zCo.WERKS ASC";

	public static final String GET_REPORTE_OPERACIONES_BCPS = "SELECT Zcen.WERKS 'Centro',  "
			+ "(SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 3 OR IDPROC = 13) and CENTRO = Zcen.WERKS) 'Recibidas_Ent', "
			+ "(SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 4 OR IDPROC = 14) and CENTRO = Zcen.WERKS) 'Confirmadas_Ent', "
			+ "((SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 3 OR IDPROC = 13) and CENTRO = Zcen.WERKS) "
			+ "-(SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 4 OR IDPROC = 14) and CENTRO = Zcen.WERKS)) 'Pendientes_Ent', "
			+ "(SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 8 OR IDPROC = 28) and CENTRO = Zcen.WERKS) 'Recibidas_Sal', "
			+ "(SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 9 OR IDPROC = 29) and CENTRO = Zcen.WERKS) 'Confirmadas_Sal', "
			+ "((SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 8 OR IDPROC = 28) and CENTRO = Zcen.WERKS) "
			+ "-(SELECT COUNT(distinct ENTREGA) FROM zContingencia WITH(NOLOCK) WHERE (IDPROC = 9 OR IDPROC = 29) and CENTRO = Zcen.WERKS)) 'Pendientes_Sal', "
			+ "(SELECT COUNT(*) FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE werks = Zcen.WERKS and idProceso = 2) 'Confirmado_Hu_Env', "
			+ "(SELECT COUNT(*) FROM zContingencia WITH(NOLOCK) where CENTRO = Zcen.WERKS and IDPROC = 5) 'Consumidas_Hu_Env', "
			+ "(SELECT count(*) FROM ZPickingEntregaEntrante zpe WITH(NOLOCK) LEFT JOIN zContingencia zco WITH(NOLOCK) on zpe.EXIDV = zco.HU "
			+ "WHERE zpe.idProceso = 2 and zpe.werks = Zcen.WERKS and zpe.EXIDV is null) 'Pendientes_Hu_env', "
			+ "(SELECT COUNT(*) FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE werks = Zcen.WERKS and idProceso = 3 and EXIDV is not null) 'Generadas_Hu_Ub', "
			+ "(SELECT COUNT(*) FROM zContingencia WITH(NOLOCK) where CENTRO = Zcen.WERKS and IDPROC = 7) 'Confirmadas_Hu_Ub', "
			+ "((SELECT COUNT(*) FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE werks = Zcen.WERKS and idProceso = 3 and EXIDV is not null) - "
			+ "(SELECT COUNT(*) FROM zContingencia WITH(NOLOCK) where CENTRO = Zcen.WERKS and IDPROC = 7)) 'Pendientes_Hu_Ub' "
			+ " FROM zCentrosBCPS Zcen";

	public static final String GET_DESCARGA_INFORMACION_MIN_MAX = "SELECT min(convert(date,ERDAT)) as 'minInterval', max(convert(date,ERDAT)) as 'maxInterval' FROM LIKP WITH(NOLOCK)";

	public static final String GET_DESCARGA_INFORMACION_FOLIOS_X_WERKS = "SELECT DISTINCT (TKNUM) AS TKNUM FROM VTTP VT WITH(NOLOCK) "
			+ "INNER JOIN LIPS LP on VT.VBELN = LP.VBELN  WHERE LP.WERKS = ? and LP.WERKS = LP.XWERKS";

	public static final String GET_DESCARGA_INFORMACION_ENTREGAS_ENTRANTES_X_WERKS = "SELECT distinct (lp.VBELN) VBELN FROM LIPS lp WITH(NOLOCK) "
			+ " INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN "
			+ "WHERE lk.LFART = 'EL' and lp.WERKS = ? and lp.WERKS =  lp.XWERKS";

	public static final String GET_DESCARGA_INFORMACION_ENTREGAS_SALIENTES_X_WERKS = "SELECT distinct (lp.VBELN) VBELN FROM LIPS lp WITH(NOLOCK) "
			+ " INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN "
			+ "WHERE lk.LFART != 'EL' and lp.WERKS = ? and lp.WERKS =  lp.XWERKS";

	public static final String GET_DESCARGA_INFORMACION_ORDENES_PRODUCCION_X_WERKS = "SELECT DISTINCT(AUFNR) AUFNR FROM AUFK WITH(NOLOCK) WHERE WERKS = ?";

	public static final String GET_DESCARGA_INFORMACION_UNIDADES_MANIPULACION_X_WERKS_PC = "SELECT DISTINCT(LQ.LENUM) FROM LQUA LQ WITH(NOLOCK) WHERE LQ.WERKS = ? LQ.LENUM is not null";

	public static final String GET_DESCARGA_INFORMACION_UNIDADES_MANIPULACION_X_WERKS_PV = "SELECT DISTINCT(VK.EXIDV) EXIDV  FROM VEKP VK WITH(NOLOCK) WHERE VK.WERKS = ? AND VK.WERKS = VK.XWERKS";

	public static final String SAVE_PLANEACION_BY_WERKS = "INSERT INTO TB_BCPS_REPORTE_PLANEACION (WERKS,CAJAS,EMBARQUES) VALUES (?,?,?)";

	public DescargaInformacionDTO getDescargaInformacion() {
		DescargaInformacionDTO descargaInformacionDTO = new DescargaInformacionDTO();
		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		List<DescargaInformacionBodyDTO> descargaInformacionBodyDTOs = new ArrayList<DescargaInformacionBodyDTO>();
		try {
			PreparedStatement stm = con
					.prepareStatement(GET_DESCARGA_INFORMACION);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				DescargaInformacionBodyDTO bodyDTO = new DescargaInformacionBodyDTO();
				bodyDTO.setWerks(rs.getString("werks"));
				bodyDTO.setFolioTransporte(rs.getString("FoliosEmbarque"));
				bodyDTO.setEntregasSalientes(rs.getString("entregasSalientes"));
				bodyDTO.setEntregasEntrantes(rs.getString("entregasEntrantes"));
				bodyDTO.setOrdenesProduccion(rs.getString("OrdenesProduccion"));
				bodyDTO.setUnidadesManipulacion(rs
						.getString("UnidadesManipulacion"));
				descargaInformacionBodyDTOs.add(bodyDTO);
			}
			descargaInformacionDTO.setItemDto(descargaInformacionBodyDTOs);
			stm = con.prepareStatement(GET_DESCARGA_INFORMACION_MIN_MAX);
			rs = stm.executeQuery();
			while (rs.next()) {
				descargaInformacionDTO.setIntervaloIni(rs
						.getString("minInterval"));
				descargaInformacionDTO.setIntervaloFin(rs
						.getString("maxInterval"));
			}
			resultDT.setId(1);
		} catch (SQLException e) {
			resultDT.setId(99);
			resultDT.setMsg(e.getMessage());
			LOCATION.error("CifrasControlDAO - getDescargaInformacion() - "
					+ e.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				resultDT.setId(99);
				resultDT.setMsg(e.getMessage());
				LOCATION
						.error("CifrasControlDAO - getDescargaInformacion() - "
								+ e.getMessage());
			}
		}
		descargaInformacionDTO.setResultDT(resultDT);
		return descargaInformacionDTO;
	}

	public CargaInformacionDTO getCargaInformacion() {
		CargaInformacionDTO cargaInformacionDTO = new CargaInformacionDTO();
		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		List<CargaInformacionBodyDTO> cargaInformacionBodyDTOs = new ArrayList<CargaInformacionBodyDTO>();
		try {
			PreparedStatement stm = con.prepareStatement(GET_CARGA_INFORMACION);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				CargaInformacionBodyDTO bodyDTO = new CargaInformacionBodyDTO();
				bodyDTO.setWerks(rs.getString("werks"));
				bodyDTO.setFolioRecibidos(rs.getString("FolioRecibido"));
				bodyDTO.setFolioEmbarcados(rs.getString("FolioEmbarcado"));
				bodyDTO.setEmbarquesContabilizados(rs
						.getString("EmbarqueContabilizado"));
				bodyDTO.setRecepcionesContabilizadas(rs
						.getString("RecepcionContabilizada"));
				bodyDTO.setOrdenesProcesadas(rs.getString("OrdenesProduccion"));
				bodyDTO.setUnidadesManipulacionProd(rs
						.getString("HuProducidas"));
				bodyDTO.setUnidadesManipulacionCons(rs
						.getString("HuConsumidas"));
				bodyDTO
						.setUnidadesManipulacionTran(rs
								.getString("HuTraslados"));
				cargaInformacionBodyDTOs.add(bodyDTO);
			}
			cargaInformacionDTO.setItemDto(cargaInformacionBodyDTOs);
			resultDT.setId(1);
		} catch (SQLException e) {
			resultDT.setId(99);
			resultDT.setMsg(e.getMessage());
			LOCATION.error("CifrasControlDAO - getCargaInformacion() - "
					+ e.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				resultDT.setId(99);
				resultDT.setMsg(e.getMessage());
				LOCATION.error("CifrasControlDAO - getCargaInformacion() - "
						+ e.getMessage());
			}
		}
		cargaInformacionDTO.setResultDT(resultDT);
		return cargaInformacionDTO;
	}

	public ReporteAvanceDTO getReporteOperaciones() {
		ReporteAvanceDTO reporteAvanceDTO = new ReporteAvanceDTO();
		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		List<ReporteAvanceBodyDTO> reporteAvanceBodyDTOs = new ArrayList<ReporteAvanceBodyDTO>();
		try {
			PreparedStatement stm = con
					.prepareStatement(GET_REPORTE_OPERACIONES_BCPS);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				ReporteAvanceBodyDTO bodyDTO = new ReporteAvanceBodyDTO();
				bodyDTO.setWerks(rs.getString("Centro"));
				bodyDTO.setEntregasEntrantesRecibidas(rs
						.getString("Recibidas_Ent"));
				bodyDTO.setEntregasEntrantesConfirmadas(rs
						.getString("Confirmadas_Ent"));
				bodyDTO.setEntregasEntrantesPendientes(rs
						.getString("Pendientes_Ent"));
				bodyDTO.setEntregasSalientesRecibidas(rs
						.getString("Recibidas_Sal"));
				bodyDTO.setEntregasSalientesConfirmadas(rs
						.getString("Confirmadas_Sal"));
				bodyDTO.setEntregasSalientesPendientes(rs
						.getString("Pendientes_Sal"));
				bodyDTO.setUnidadesManipulacionConsumidasEnvase(rs
						.getString("Confirmado_Hu_Env"));
				bodyDTO.setUnidadesManipulacionConfirmadasEnvase(rs
						.getString("Consumidas_Hu_Env"));
				bodyDTO.setUnidadesManipulacionPendienteEnvase(rs
						.getString("Pendientes_Hu_env"));
				bodyDTO.setUnidadesManipulacionConsumidasUbicacion(rs
						.getString("Generadas_Hu_Ub"));
				bodyDTO.setUnidadesManipulacionConfirmadasUbicacion(rs
						.getString("Confirmadas_Hu_Ub"));
				bodyDTO.setUnidadesManipulacionPendienteUbicacion(rs
						.getString("Pendientes_Hu_Ub"));
				reporteAvanceBodyDTOs.add(bodyDTO);
			}
			reporteAvanceDTO.setItemDto(reporteAvanceBodyDTOs);
			resultDT.setId(1);
		} catch (SQLException e) {
			resultDT.setId(99);
			resultDT.setMsg(e.getMessage());
			LOCATION.error("CifrasControlDAO - getReporteOperaciones() - "
					+ e.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				resultDT.setId(99);
				resultDT.setMsg(e.getMessage());
				LOCATION
						.error("CifrasControlDAO - getReporteOperacioness() - "
								+ e.getMessage());
			}
		}
		reporteAvanceDTO.setResultDT(resultDT);
		return reporteAvanceDTO;
	}

	public List<String> generateDataReport(
			DescargaInformacionDTO descargaInformacionDTO) {
		List<String> toReturn = new ArrayList<String>();
		Connection con = dbConnection.createConnection();
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			if (descargaInformacionDTO.getResultDT().getId() == 1) {
				stm = con
						.prepareStatement(GET_DESCARGA_INFORMACION_FOLIOS_X_WERKS);
			} else if (descargaInformacionDTO.getResultDT().getId() == 2) {
				stm = con
						.prepareStatement(GET_DESCARGA_INFORMACION_ENTREGAS_ENTRANTES_X_WERKS);
			} else if (descargaInformacionDTO.getResultDT().getId() == 3) {
				stm = con
						.prepareStatement(GET_DESCARGA_INFORMACION_ENTREGAS_SALIENTES_X_WERKS);
			} else if (descargaInformacionDTO.getResultDT().getId() == 4) {
				stm = con
						.prepareStatement(GET_DESCARGA_INFORMACION_ORDENES_PRODUCCION_X_WERKS);
			} else if (descargaInformacionDTO.getResultDT().getId() == 5) {
				if (descargaInformacionDTO.getResultDT().getMsg()
						.contains("PV")) {
					stm = con
							.prepareStatement(GET_DESCARGA_INFORMACION_UNIDADES_MANIPULACION_X_WERKS_PV);
				} else {
					stm = con
							.prepareStatement(GET_DESCARGA_INFORMACION_UNIDADES_MANIPULACION_X_WERKS_PC);
				}
			}
			stm.setString(1, descargaInformacionDTO.getResultDT().getMsg());
			rs = stm.executeQuery();
			while (rs.next()) {
				toReturn.add(rs.getString(rs.getMetaData().getColumnName(1)));
			}
		} catch (SQLException e) {
			LOCATION.error("CifrasControlDAO - generateDataReport() - "
					+ e.getMessage());
		}
		return toReturn;
	}

	public ResultDTO savePlaneacion(PlaneacionBodyDTO planeacionBodyDTO) {
		Connection con = dbConnection.createConnection();
		PreparedStatement stm = null;
		ResultDTO result = new ResultDTO();
		try {
			stm = con.prepareStatement(SAVE_PLANEACION_BY_WERKS);
			stm.setString(1, planeacionBodyDTO.getCentro());
			stm.setString(2, planeacionBodyDTO.getCajas());
			stm.setString(3, planeacionBodyDTO.getEmbarques());
			stm.executeUpdate();
			result.setId(1);
			result.setMsg("Información Almacenada Correctamente");
		} catch (SQLException e) {
			LOCATION.error("CifrasControlDAO - savePlaneacion() - "
					+ e.getMessage());
			result.setId(3);
			result.setMsg("CifrasControlDAO - savePlaneacion() - "
					+ e.getMessage());

		}
		return result;
	}
}
