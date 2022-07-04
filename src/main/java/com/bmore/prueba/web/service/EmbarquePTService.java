package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.EmbarqueDTO;
import com.bmore.prueba.web.dto.EmbarqueDetalleDTO;
import com.bmore.prueba.web.dto.HuDTO;
import com.bmore.prueba.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Embarque PT.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface EmbarquePTService {

	public ResultDTO cambiarCantidadOrdenProduccion(EmbarqueDetalleDTO embarqueDetalleDTO, String user, String werks);

	public ResultDTO contabilizarEntregaEntrante(EmbarqueDTO embarqueDTO, String user);

	public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks);

	public boolean isWerksAllowed(HuDTO huDTO);

	public ResultDTO limpiarPendientes(String vbeln);

	public EmbarqueDTO validaEmbarque(EmbarqueDTO embarque);
}
