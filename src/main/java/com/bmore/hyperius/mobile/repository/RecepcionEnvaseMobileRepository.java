package com.bmore.hyperius.mobile.repository;

import com.bmore.hyperius.mobile.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.mobile.dto.EntregaDetalleDTO;
import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.utils.ResultDT;

public interface RecepcionEnvaseMobileRepository {

  public String getWerks(String Hu);

  public EntregaInput validarEntregaPickin(String entrega);

  public int getFaltantes(String entrega);

  public ResultDT getVBELNFromHuSAP(String hu, String werks);

  public ResultDT getVBELNFromHuBCPS(String hu, String werks);

  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas);

  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas, String lgtyp,
      String lgpla);

  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu);

  public EntregaDetalleDTO getDataHU(String hu);

  public EntregaDetalleDTO getDataHU_LQUA(String hu);

  public ResultDT confirmaHusEnCarrill(EntregaInput entregaEntranteInput);

  public ResultDT limpiaPendientesXUsuario(String vbeln, String user);

  public ResultDT validaPickeoPrevioHU(EntregaInput entregaInput, String hu);
}
