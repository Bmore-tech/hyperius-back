package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.HuDTO;
import com.bmore.prueba.web.dto.HusDTO;

/**
 * Interface para realizar las operaciones de negocio de HU.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface HUsService {

	public HusDTO obtieneHus(HuDTO huDTO);
}
