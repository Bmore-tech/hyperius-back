package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Alimentación Línea.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface AlimentacionLineaService {

	public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks);

	public int limpiarPendientes(String vbeln);

	public OrdenProduccionDTO validaOrden(OrdenProduccionDTO orden);
}
