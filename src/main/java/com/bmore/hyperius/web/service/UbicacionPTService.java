package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Ubicación.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface UbicacionPTService {

	public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks);

	public OrdenProduccionDTO validaOrden(OrdenProduccionDTO orden);
}
