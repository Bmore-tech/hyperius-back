package com.bmore.hyperius.web.service;

import java.io.File;
import java.util.List;

import com.bmore.hyperius.web.dto.CreacionEntregasDTO;
import com.bmore.hyperius.web.dto.ReporteOperacionesDTO;
import com.bmore.hyperius.web.dto.ReporteShippingDTO;
import com.bmore.hyperius.web.dto.ServletReporteProformaDTO;

/**
 * Interface para realizar las operaciones de negocio de Reportes.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface ReporteService {

	public String filePath(String reporte, String myTimeStamp, String werks, String ext);

	public File generateEdi(String EDI, String filepath);

	public File generateEntregasCSC(String type, String filepath, CreacionEntregasDTO creacionEntregasDTO);

	public File generateProforma(String type, String filepath, List<ServletReporteProformaDTO> profDTO);

	public File generateReporteOperaciones(String filepath, List<ReporteOperacionesDTO> profDTO,
			List<ReporteOperacionesDTO> profDTO2);

	public File generateShipping(String type, String filepath, List<ReporteShippingDTO> profDTO);

	public File getDocument(String reporte, String myTimeStamp, String werks, String vbeln);
}
