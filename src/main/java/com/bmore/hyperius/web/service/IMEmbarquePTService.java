package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de IM Embarque.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface IMEmbarquePTService {

	public ResultDTO contabilizarEntregaEntrante(EmbarqueDTO embarqueDTO, String user);

	public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks);

	public EmbarqueDTO validaEmbarque(EmbarqueDTO embarque);
}
