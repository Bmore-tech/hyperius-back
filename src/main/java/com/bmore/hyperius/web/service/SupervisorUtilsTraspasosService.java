package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.AlmacenDTO;
import com.bmore.hyperius.web.dto.AlmacenesDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Supervisor Utils y
 * Traspasos.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface SupervisorUtilsTraspasosService {

	public AlmacenesDTO lgnumPermitidos(AlmacenDTO almacen);

	public AlmacenesDTO lgortPermitidos(String werks);

	public AlmacenesDTO lgplaPermitidos(AlmacenDTO almacen);

	public AlmacenesDTO lgtypPermitidos(AlmacenDTO almacen);

	public InventarioDTO lquaBusquedaTraspasos(AlmacenDTO almacen);

	public ResultDTO traspaso(InventarioDetalleDTOItem inventarioDetalleDTOItem, String user);
}
