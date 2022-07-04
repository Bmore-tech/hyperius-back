package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.EntregaInputDTO;

/**
 * Interface para realizar las operaciones de negocio de Embarque PTI
 * Montacargas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface EmbarquePTIMService {

	public EntregaInputDTO consumirHU(EntregaInputDTO entregaInput);

	public EntregaInputDTO validarEntrega(EntregaInputDTO entregaInput);
}
