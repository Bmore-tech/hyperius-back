package com.bmore.prueba.web.service;

import com.bmore.prueba.web.dto.CreacionEntregaItemDTO;
import com.bmore.prueba.web.dto.CreacionEntregasDTO;
import com.bmore.prueba.web.dto.CrecionEntregaDTO;
import com.bmore.prueba.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Creación de Entregas.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface CreacionEntregasService {

	public ResultDTO crearEntrega(CreacionEntregaItemDTO crearEntrega, String werks, String user);

	public ResultDTO eliminarEntrega(CrecionEntregaDTO eliminarEntrega, String werks, String usuario);

	public CreacionEntregasDTO getEntrega(String vbeln);

	public CreacionEntregasDTO getEntregas();

	public CreacionEntregasDTO obtieneAgencias();

	public CreacionEntregasDTO obtieneCentros();

	public CreacionEntregasDTO obtieneMateriales();

	public CreacionEntregasDTO obtieneTarimas(CrecionEntregaDTO materialDTO);

	public CreacionEntregasDTO obtieneTransportes(CrecionEntregaDTO materialDTO);
}
