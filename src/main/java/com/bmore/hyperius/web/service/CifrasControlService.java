package com.bmore.hyperius.web.service;

import java.io.File;

import com.bmore.hyperius.web.dto.CargaInformacionDTO;
import com.bmore.hyperius.web.dto.DescargaInformacionDTO;
import com.bmore.hyperius.web.dto.PlaneacionBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Cifras de Control.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface CifrasControlService {

	public File generateReports(CargaInformacionDTO cargaInformacionDTO);

	public File generateReports(DescargaInformacionDTO descargaInformacionDTO);

	public File generateReports(ReporteAvanceDTO reporteAvanceDTO);

	public CargaInformacionDTO getCargaInformacion();

	public DescargaInformacionDTO getDescargaInformacion();

	public ReporteAvanceDTO getReporteOperaciones();

	public ResultDTO saveReportePlaneacion(PlaneacionBodyDTO planeacionBodyDTO);
}
