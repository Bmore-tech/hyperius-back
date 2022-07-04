package com.bmore.prueba.web.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.prueba.web.dto.CargaInformacionBodyDTO;
import com.bmore.prueba.web.dto.CargaInformacionDTO;
import com.bmore.prueba.web.dto.DescargaInformacionBodyDTO;
import com.bmore.prueba.web.dto.DescargaInformacionDTO;
import com.bmore.prueba.web.dto.PlaneacionBodyDTO;
import com.bmore.prueba.web.dto.ReporteAvanceBodyDTO;
import com.bmore.prueba.web.dto.ReporteAvanceDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.CifrasControlRepository;

@Repository
public class CifrasControlRepositoryImpl implements CifrasControlRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<String> generateDataReport(DescargaInformacionDTO descargaInformacionDTO) {
		String q1 = "SELECT DISTINCT (TKNUM) AS TKNUM FROM VTTP VT WITH(NOLOCK) "
				+ "INNER JOIN LIPS LP ON VT.VBELN = LP.VBELN WHERE LP.WERKS = ? AND LP.WERKS = LP.XWERKS";
		String q2 = "SELECT DISTINCT (lp.VBELN) VBELN FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk WITH(NOLOCK) ON lp.VBELN = lk.VBELN "
				+ "WHERE lk.LFART = 'EL' and lp.WERKS = ? and lp.WERKS =  lp.XWERKS";
		String q3 = "SELECT DISTINCT (lp.VBELN) VBELN FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk WITH(NOLOCK) ON lp.VBELN = lk.VBELN "
				+ "WHERE lk.LFART != 'EL' and lp.WERKS = ? and lp.WERKS =  lp.XWERKS";
		String q4 = "SELECT DISTINCT(AUFNR) AUFNR FROM AUFK WITH(NOLOCK) WHERE WERKS = ?";
		String q5 = "SELECT DISTINCT(VK.EXIDV) EXIDV  FROM VEKP VK WITH(NOLOCK) WHERE VK.WERKS = ? AND VK.WERKS = VK.XWERKS";
		String q6 = "SELECT DISTINCT(LQ.LENUM) FROM LQUA LQ WITH(NOLOCK) WHERE LQ.WERKS = ? LQ.LENUM is not null";
		String query = "";
		Object[] args = { descargaInformacionDTO.getResultDT().getMsg() };

		if (descargaInformacionDTO.getResultDT().getId() == 1) {
			query = q1;
		} else if (descargaInformacionDTO.getResultDT().getId() == 2) {
			query = q2;
		} else if (descargaInformacionDTO.getResultDT().getId() == 3) {
			query = q3;
		} else if (descargaInformacionDTO.getResultDT().getId() == 4) {
			query = q4;
		} else if (descargaInformacionDTO.getResultDT().getId() == 5) {
			if (descargaInformacionDTO.getResultDT().getMsg().contains("PV")) {
				query = q5;
			} else {
				query = q6;
			}
		}

		return jdbcTemplate.query(query, args, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
	}

	@Override
	public CargaInformacionDTO getCargaInformacion() {
		String query = "SELECT zCo.WERKS, "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 2 and zCoi.CENTRO = zCo.WERKS) 'FolioRecibido', "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 10 and zCoi.CENTRO = zCo.WERKS) 'FolioEmbarcado',  "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE (zCoi.IDPROC = 9 OR zCoi.IDPROC = 29) and zCoi.CENTRO = zCo.WERKS) 'EmbarqueContabilizado', "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE (zCoi.IDPROC = 4 OR zCoi.IDPROC = 14) and zCoi.CENTRO = zCo.WERKS) 'RecepcionContabilizada',  "
				+ " (SELECT COUNT(DISTINCT(zCoi.ORDEN_PRODUCCION)) FROM zContingencia zCoi WITH(NOLOCK) WHERE zCoi.CENTRO = zCo.WERKS) 'OrdenesProduccion', "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 7 and zCoi.CENTRO = zCo.WERKS) 'HuProducidas',  "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 5 and zCoi.CENTRO = zCo.WERKS) 'HuConsumidas',  "
				+ " (SELECT COUNT(*) FROM  zContingencia zCoi WITH(NOLOCK) WHERE zCoi.IDPROC = 15 and zCoi.CENTRO = zCo.WERKS) 'HuTraslados' "
				+ " FROM zCentrosBCPS zCo WITH(NOLOCK) order by zCo.WERKS ASC";
		CargaInformacionDTO cargaInformacionDTO = new CargaInformacionDTO();
		ResultDTO resultDT = new ResultDTO();

		List<CargaInformacionBodyDTO> cargaInformacionBodyDTOs = jdbcTemplate.query(query,
				new RowMapper<CargaInformacionBodyDTO>() {

					@Override
					public CargaInformacionBodyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						CargaInformacionBodyDTO bodyDTO = new CargaInformacionBodyDTO();
						bodyDTO.setWerks(rs.getString("werks"));
						bodyDTO.setFolioRecibidos(rs.getString("FolioRecibido"));
						bodyDTO.setFolioEmbarcados(rs.getString("FolioEmbarcado"));
						bodyDTO.setEmbarquesContabilizados(rs.getString("EmbarqueContabilizado"));
						bodyDTO.setRecepcionesContabilizadas(rs.getString("RecepcionContabilizada"));
						bodyDTO.setOrdenesProcesadas(rs.getString("OrdenesProduccion"));
						bodyDTO.setUnidadesManipulacionProd(rs.getString("HuProducidas"));
						bodyDTO.setUnidadesManipulacionCons(rs.getString("HuConsumidas"));
						bodyDTO.setUnidadesManipulacionTran(rs.getString("HuTraslados"));

						resultDT.setId(1);

						return bodyDTO;
					}

				});
		cargaInformacionDTO.setItemDto(cargaInformacionBodyDTOs);
		cargaInformacionDTO.setResultDT(resultDT);

		return cargaInformacionDTO;
	}

	@Override
	public DescargaInformacionDTO getDescargaInformacion() {
		String query = "SELECT zC.WERKS werks, "
				+ " (SELECT COUNT(distinct(TKNUM))  FROM VTTP VT WITH(NOLOCK) INNER JOIN  LIPS LP on VT.VBELN = LP.VBELN  WHERE LP.WERKS = zC.WERKS AND LP.WERKS = LP.XWERKS) as 'FoliosEmbarque', "
				+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN  "
				+ " WHERE lk.LFART != 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasSalientes', "
				+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN "
				+ " WHERE lk.LFART = 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasEntrantes', "
				+ " (SELECT COUNT(AUFNR) FROM AUFK WITH(NOLOCK) WHERE WERKS = zC.WERKS) as 'OrdenesProduccion', "
				+ " (SELECT COUNT(VK.EXIDV)  FROM VEKP VK WITH(NOLOCK) WHERE VK.WERKS = zC.WERKS AND vk.WERKS = vk.XWERKS) as 'UnidadesManipulacion' "
				+ " FROM zCentrosBCPS zC WHERE zC.WERKS LIKE 'PV%' " + " UNION " + " SELECT zC.WERKS werks, "
				+ " (SELECT COUNT(distinct(TKNUM))  FROM VTTP VT WITH(NOLOCK) INNER JOIN  LIPS LP on VT.VBELN = LP.VBELN  WHERE LP.WERKS = zC.WERKS AND LP.WERKS = LP.XWERKS) as 'FoliosEmbarque', "
				+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN  "
				+ " WHERE lk.LFART != 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasSalientes', "
				+ " (SELECT count(distinct lp.VBELN) FROM LIPS lp WITH(NOLOCK) INNER JOIN LIKP lk with(nolock) on lp.VBELN = lk.VBELN "
				+ " WHERE lk.LFART = 'EL' and lp.WERKS = zC.WERKS and lp.WERKS = lp.XWERKS) as 'entregasEntrantes', "
				+ " (SELECT COUNT(AUFNR) FROM AUFK WITH(NOLOCK) WHERE WERKS = zC.WERKS) as 'OrdenesProduccion',  "
				+ " (SELECT COUNT(DISTINCT(LQ.LENUM)) FROM LQUA LQ WITH(NOLOCK) WHERE LQ.WERKS = zC.WERKS AND LQ.LENUM is not null) as 'UnidadesManipulacion' "
				+ " FROM zCentrosBCPS zC WHERE zC.WERKS NOT LIKE 'PV%'";

		DescargaInformacionDTO descargaInformacionDTO = new DescargaInformacionDTO();
		ResultDTO resultDT = new ResultDTO();

		List<DescargaInformacionBodyDTO> descargaInformacionBodyDTOs = jdbcTemplate.query(query,
				new RowMapper<DescargaInformacionBodyDTO>() {

					@Override
					public DescargaInformacionBodyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						DescargaInformacionBodyDTO bodyDTO = new DescargaInformacionBodyDTO();
						bodyDTO.setWerks(rs.getString("werks"));
						bodyDTO.setFolioTransporte(rs.getString("FoliosEmbarque"));
						bodyDTO.setEntregasSalientes(rs.getString("entregasSalientes"));
						bodyDTO.setEntregasEntrantes(rs.getString("entregasEntrantes"));
						bodyDTO.setOrdenesProduccion(rs.getString("OrdenesProduccion"));
						bodyDTO.setUnidadesManipulacion(rs.getString("UnidadesManipulacion"));

						return bodyDTO;
					}

				});

		descargaInformacionDTO.setItemDto(descargaInformacionBodyDTOs);

		String query2 = "SELECT min(convert(date,ERDAT)) as 'minInterval', max(convert(date,ERDAT)) as 'maxInterval' FROM LIKP WITH(NOLOCK)";

		jdbcTemplate.query(query2, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				descargaInformacionDTO.setIntervaloIni(rs.getString("minInterval"));
				descargaInformacionDTO.setIntervaloFin(rs.getString("maxInterval"));

				return null;
			}
		});

		resultDT.setId(1);
		descargaInformacionDTO.setResultDT(resultDT);

		return descargaInformacionDTO;
	}

	@Override
	public ReporteAvanceDTO getReporteOperaciones() {
		String query = "SELECT Zcen.WERKS 'Centro',  "
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

		ReporteAvanceDTO reporteAvanceDTO = new ReporteAvanceDTO();
		ResultDTO resultDT = new ResultDTO();
		List<ReporteAvanceBodyDTO> reporteAvanceBodyDTOs = jdbcTemplate.query(query,
				new RowMapper<ReporteAvanceBodyDTO>() {

					@Override
					public ReporteAvanceBodyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
						ReporteAvanceBodyDTO bodyDTO = new ReporteAvanceBodyDTO();
						bodyDTO.setWerks(rs.getString("Centro"));
						bodyDTO.setEntregasEntrantesRecibidas(rs.getString("Recibidas_Ent"));
						bodyDTO.setEntregasEntrantesConfirmadas(rs.getString("Confirmadas_Ent"));
						bodyDTO.setEntregasEntrantesPendientes(rs.getString("Pendientes_Ent"));
						bodyDTO.setEntregasSalientesRecibidas(rs.getString("Recibidas_Sal"));
						bodyDTO.setEntregasSalientesConfirmadas(rs.getString("Confirmadas_Sal"));
						bodyDTO.setEntregasSalientesPendientes(rs.getString("Pendientes_Sal"));
						bodyDTO.setUnidadesManipulacionConsumidasEnvase(rs.getString("Confirmado_Hu_Env"));
						bodyDTO.setUnidadesManipulacionConfirmadasEnvase(rs.getString("Consumidas_Hu_Env"));
						bodyDTO.setUnidadesManipulacionPendienteEnvase(rs.getString("Pendientes_Hu_env"));
						bodyDTO.setUnidadesManipulacionConsumidasUbicacion(rs.getString("Generadas_Hu_Ub"));
						bodyDTO.setUnidadesManipulacionConfirmadasUbicacion(rs.getString("Confirmadas_Hu_Ub"));
						bodyDTO.setUnidadesManipulacionPendienteUbicacion(rs.getString("Pendientes_Hu_Ub"));

						resultDT.setId(1);

						return bodyDTO;
					}

				});

		reporteAvanceDTO.setItemDto(reporteAvanceBodyDTOs);
		reporteAvanceDTO.setResultDT(resultDT);
		return reporteAvanceDTO;
	}

	@Override
	public ResultDTO savePlaneacion(PlaneacionBodyDTO planeacionBodyDTO) {
		String query = "INSERT INTO TB_BCPS_REPORTE_PLANEACION (WERKS,CAJAS,EMBARQUES) VALUES (?,?,?)";
		Object[] args = { planeacionBodyDTO.getCentro(), planeacionBodyDTO.getCajas(),
				planeacionBodyDTO.getEmbarques() };
		ResultDTO result = new ResultDTO();

		if (jdbcTemplate.update(query, args) > 0) {
			result.setId(1);
			result.setMsg("Información Almacenada Correctamente");
		} else {
			result.setId(2);
		}

		return result;
	}

}
