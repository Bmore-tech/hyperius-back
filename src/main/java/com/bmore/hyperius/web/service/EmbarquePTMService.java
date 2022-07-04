package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Embarque PT
 * Montacargas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface EmbarquePTMService {

	public EntregaInputDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput);

	public ResultDTO limpiarPendientesXUsuario(String vbeln, String user);

	public EntregaInputDTO pickearHU(EntregaInputDTO entregaInput, int hu1oHu2);

	public EntregaInputDTO validarEntrega(EntregaInputDTO entregaInput);
}
