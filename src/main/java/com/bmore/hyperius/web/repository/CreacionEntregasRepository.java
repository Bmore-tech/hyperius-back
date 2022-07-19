package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.CreacionEntregasDTO;
import com.bmore.hyperius.web.dto.CrecionEntregaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface CreacionEntregasRepository {
  CreacionEntregasDTO obtieneMaterialDAO();

  CreacionEntregasDTO obtieneTarimasDAO(CrecionEntregaDTO materialDTO);

  CreacionEntregasDTO obtieneCentrosDAO();

  CreacionEntregasDTO obtieneAgenciasDAO();

  CreacionEntregasDTO obtieneTransportesDAO(CrecionEntregaDTO transporteDTO);

  ResultDTO creacionEntregaBCPS(CrecionEntregaDTO item, String user);

  ResultDTO creacionLipsZcontingenciaEntregaBCPS(CrecionEntregaDTO item, String user, String entrega);

  ResultDTO updateVTTP(String transporte, String entrega, String werks, String user);

  CreacionEntregasDTO getEntregas();

  CreacionEntregasDTO getEntrega(String vbeln);

  ResultDTO eliminarEntrega(String vbeln, String transporte, String werks, String usuario);
}
