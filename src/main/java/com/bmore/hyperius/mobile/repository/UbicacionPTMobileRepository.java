package com.bmore.hyperius.mobile.repository;

import com.bmore.hyperius.mobile.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.utils.ResultDT;

public interface UbicacionPTMobileRepository {

  public OrdenProduccionDetalleDTO getDataHU(String hu);

  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu);

  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacarga);

  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr,
      String lgtyp, String lgpla, String usuarioMontacarga);

  public int getFaltantes(String entry);

  public ResultDT getAUFNRFromHu(String hu, String werks);

  public OrdenProduccionInput validarOrdenEnPickin(String entry);

  public ResultDT confirmaHusEnCarrill(OrdenProduccionInput ordenProduccionInput);

  public ResultDT limpiaPendientesXUsuario(String vbeln, String user);

  public String getWerks(String Hu);

}
