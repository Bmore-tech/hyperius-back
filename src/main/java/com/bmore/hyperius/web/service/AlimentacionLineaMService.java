package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Alimentación Línea
 * Montacargas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface AlimentacionLineaMService {

	public OrdenProduccionInputDTO confirmaHusEnDepa(OrdenProduccionInputDTO ordenProduccionInput);

	public OrdenProduccionInputDTO pickearHU(OrdenProduccionInputDTO ordenProduccionInput, int hu1oHu2);

	public int limpiarPendientesXUsuario(String vbeln, String user);

	public OrdenProduccionInputDTO validaOrdenProduccion(OrdenProduccionInputDTO ordenProduccionInput);
}
