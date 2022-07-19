package com.bmore.hyperius.mobile.repository;

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.dto.EntregaDetalleDTO;

public interface EmbarquePTMobileRepository {

  public EntregaInput validarEntregaPickin(EntregaInput entregaInput);

  public EntregaInput reservaUbicaciones(EntregaInput entregaInput);

  public ResultDT validaPickeoPrevioHU(EntregaInput entregaInput, String hu);

  public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

  public ResultDT confirmaHusEnCamionFurgon(EntregaInput entregaInput);

  public ResultDT limpiaPendientesXUsuario(String vbeln, String user);

  public String getWerks(String entrega);
}
