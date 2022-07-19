package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.AlmacenDTO;
import com.bmore.hyperius.web.dto.AlmacenesDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface SupervisorUtilsTraspasosRepository {

  public AlmacenesDTO lgortPermitidos(String werks);

  public AlmacenesDTO lgnumPermitidos(AlmacenDTO almacenDTO);

  public AlmacenesDTO lgtypPermitidos(AlmacenDTO almacenDTO);

  public AlmacenesDTO lgplaPermitidos(AlmacenDTO almacenDTO);

  public InventarioDTO lquaBusquedaTraspasos(AlmacenDTO almacenDTO, int opc);

  public ResultDTO traspaso(InventarioDetalleDTO inventarioDetalleDTO, String user);
}
