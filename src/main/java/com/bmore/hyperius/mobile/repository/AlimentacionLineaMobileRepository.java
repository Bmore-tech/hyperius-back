package com.bmore.hyperius.mobile.repository;

import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.utils.ResultDT;

public interface AlimentacionLineaMobileRepository {

  // public static String getLGPLA(String hu);
  public String getLGPLA(String hu);

  public ResultDT validaOrden(OrdenProduccionInput ordenProduccionInput);

  public OrdenProduccionDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

  public OrdenProduccionInput obtieneDepaletizadora(OrdenProduccionInput ordenProduccionInput);

  public ResultDT confirmaHUsenDepa(OrdenProduccionInput ordenProduccionInput);

  public ResultDT validaPickeoPrevioHU(OrdenProduccionInput ordenProduccionInput, String hu);

  public int limpiaPendientesXUsuario(String vbeln, String user);

  public OrdenProduccionInput reservaUbicaciones(OrdenProduccionInput ordenProduccionInput);

  // public static String getWerks(String ordProd);
  public String getWerks(String ordProd);
}
