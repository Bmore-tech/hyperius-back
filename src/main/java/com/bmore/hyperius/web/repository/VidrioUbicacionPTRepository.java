package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface VidrioUbicacionPTRepository {

  public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput);

  public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks);

  public ResultDTO vidrioUbicaPT(OrdenProduccionInputDTO ordenProduccion);
}
