package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Entrada PT de
 * Montacargas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface EntradaPTMService {

	public ResultDTO confirmaPickingHU(OrdenProduccionInputDTO ordenProduccionInput);

	public int getFaltantes(String entry);

	public OrdenProduccionInputDTO pickearHU(OrdenProduccionInputDTO OrdenProduccionInput, int hu1oHu2);

	public OrdenProduccionInputDTO validaOrdenProduccion(OrdenProduccionInputDTO OrdenProduccionInput);
}
