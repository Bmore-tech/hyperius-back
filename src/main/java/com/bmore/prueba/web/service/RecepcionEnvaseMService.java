package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.EntregaInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Recepción Envase
 * Montacargas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface RecepcionEnvaseMService {

	public ResultDTO confirmaPickingHU(EntregaInputDTO entregaEntranteInput);

	public int getFaltantes(String entry);

	public EntregaInputDTO pickearHU(EntregaInputDTO entregaInput, int hu1oHu2);

	public EntregaInputDTO validaEntregaEntrante(EntregaInputDTO entregaInput);
}
