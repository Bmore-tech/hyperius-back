package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;

/**
 * Interface para realizar las operaciones de negocio de Transporte.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface TransportesService {

	public TransportesDTO existeTransporteEntrada(TransportesDTO transporteDTO);

	public TransportesDTO existeTransporteSalida(TransportesDTO transporteDTO);

	public ResultDTO updateStatusTransporte(TransportesDTO transporteDTO, String user);
}
