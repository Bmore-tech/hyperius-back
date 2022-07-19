package com.bmore.hyperius.web.repository;

import java.util.List;

import com.bmore.hyperius.web.dto.ReporteOperacionesDTO;
import com.bmore.hyperius.web.dto.ReporteShippingDTO;
import com.bmore.hyperius.web.dto.ServletReporteProformaDTO;

public interface ServletReporteRepository {
  List<ServletReporteProformaDTO> getDatosProforma();

  List<ReporteOperacionesDTO> getReporteOperacionesDAO(String werks);

  List<ReporteOperacionesDTO> getReporteOperacionesInitialStockDAO(String werks);

  String getTknum(String Vbeln);

  List<ReporteShippingDTO> getDatosShipping(String werks);
}
