package com.bmore.hyperius.web.repository;

import java.util.List;

import com.bmore.hyperius.web.dto.CargaInformacionDTO;
import com.bmore.hyperius.web.dto.DescargaInformacionDTO;
import com.bmore.hyperius.web.dto.PlaneacionBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface CifrasControlRepository {

  List<String> generateDataReport(DescargaInformacionDTO descargaInformacionDTO);

  CargaInformacionDTO getCargaInformacion();

  DescargaInformacionDTO getDescargaInformacion();

  ReporteAvanceDTO getReporteOperaciones();

  ResultDTO savePlaneacion(PlaneacionBodyDTO planeacionBodyDTO);
}
