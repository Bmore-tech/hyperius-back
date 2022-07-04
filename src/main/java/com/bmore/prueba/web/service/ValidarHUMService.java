package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.HusDTO;

/**
 * Interface para realizar las operaciones de negocio de Validación de HUM.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface ValidarHUMService {

	public HusDTO validarHU(String hu, String werks);

	public HusDTO validarHUVidrio(String exidv, String werks);
}
