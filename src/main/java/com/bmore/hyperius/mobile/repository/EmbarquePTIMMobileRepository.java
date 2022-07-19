package com.bmore.hyperius.mobile.repository;

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.utils.ResultDT;

public interface EmbarquePTIMMobileRepository {

  public EntregaInput validarEntregaPickin(EntregaInput entregaInput);

  public ResultDT reservaHus(EntregaInput entregaInput);

  public ResultDT obtieneDescripcionMaterial(String matnr, String vblen);

  public ResultDT consumeHUs(EntregaInput entregaInput);

  public ResultDT limpiaPendientesXUsuario(String vbeln, String user);

}
