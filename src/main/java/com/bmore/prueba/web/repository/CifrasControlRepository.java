package com.bmore.prueba.web.repository;

import java.util.List;

import com.bmore.prueba.web.dto.CargaInformacionDTO;
import com.bmore.prueba.web.dto.DescargaInformacionDTO;
import com.bmore.prueba.web.dto.PlaneacionBodyDTO;
import com.bmore.prueba.web.dto.ReporteAvanceDTO;
import com.bmore.prueba.web.dto.ResultDTO;

public interface CifrasControlRepository {

	public List<String> generateDataReport(DescargaInformacionDTO descargaInformacionDTO);

	public CargaInformacionDTO getCargaInformacion();

	public DescargaInformacionDTO getDescargaInformacion();

	public ReporteAvanceDTO getReporteOperaciones();

	public ResultDTO savePlaneacion(PlaneacionBodyDTO planeacionBodyDTO);
}
