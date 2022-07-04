package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.EntregaDTO;
import com.bmore.prueba.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Recepción Envase.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface RecepcionEnvaseService {

	public ResultDTO contabilizarEntregaEntrante(EntregaDTO entregaDTO, String user);

	public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks);

	public EntregaDTO validaEntrega(EntregaDTO entrega);
}
