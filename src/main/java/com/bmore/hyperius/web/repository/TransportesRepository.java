package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;

public interface TransportesRepository {

  public ResultDTO obtieneTransporte(String tknum, String werks, int idQuery);

  public TransportesDTO getStatusTransporte(String tknum, String werks, String status);

  public ResultDTO updateTransporte(String tknum, String werks, String sttrg, String status);

  public ResultDTO insertProcesoContingenciaTransportes(String werks, String tknum, String user, String status);

}
