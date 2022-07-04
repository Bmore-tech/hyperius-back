package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.NormasEmbalajeDTO;
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.PaletizadorasDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

/**
 * Interface para realizar las operaciones de negocio de Control de
 * Paletizadora.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 03-08-2020
 */
public interface ControlPaletizadoraService {

	public ResultDTO actualizaOrdenEnPaletizadora(PaletizadoraDTO paletizadora);

	public ResultDTO cambiarNormaEmbalaje(PaletizadoraDTO paletizadoraDTO);

	public ResultDTO embalarHus(PaletizadoraDTO paletizadora, String userId);

	public ResultDTO generaHusBCPS(PaletizadoraDTO paletizadora);

	public ResultDTO marcarHusParaImprimir(PaletizadoraDTO paletizadora);

	public ResultDTO obtieneCantidadHUS(String aufnr);

	public NormasEmbalajeDTO obtieneNormasEmbalaje(String aufnr, String werks, String unidadMedida, String cantidad,
			int opc, String material);

	public PaletizadorasDTO obtienePaletizadoras(String werks);
}
