package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.NormasEmbalajeDTO;
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.PaletizadorasDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.print.Etiquetas;

public interface ControlPaletizadoraRepository {

  public ResultDTO cambiarNormaEmbalaje(PaletizadoraDTO paletizadoraDTO);

  public ResultDTO cambiarNormaEmbalajeBCPS(PaletizadoraDTO paletizadoraDTO);

  public ResultDTO embalarHus(PaletizadoraDTO paletizadoraDTO, String keyTimeStamp, String userId);

  public ResultDTO generaHusBCPS(PaletizadoraDTO paletizadoraDTO, String keyTimeStamp);

  public ResultDTO guardaPaletizadora(PaletizadoraDTO paletizadora);

  public ResultDTO marcarHusParaImprimir(PaletizadoraDTO paletizadora);

  public ResultDTO obtieneCantidadHUS(String aufnr);

  public NormasEmbalajeDTO obtieneEquivalenciasUM(String matnr, String um);

  public Etiquetas obtieneHusParaImprimir(String aufnr, String key);

  public NormasEmbalajeDTO obtieneLetyps(String werks, String matnr);

  public PaletizadorasDTO obtienePaletizadoras(String werks);

  public NormasEmbalajeDTO obtieneTarimas(String matnr);

  public NormasEmbalajeDTO obtieneTarimas(String letyp, String legnum);
}
