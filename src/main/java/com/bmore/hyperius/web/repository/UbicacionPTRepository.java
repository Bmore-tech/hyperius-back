package com.bmore.hyperius.web.repository;

import java.util.HashMap;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface UbicacionPTRepository {

  public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput);

  public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks);

  public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z, String LGORT,
      String VBELN, HashMap<String, String> carrilesBloqueados);

  public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

  public OrdenProduccionDetalleDTO getDataHU(String hu);

  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu);

  public ResultDTO confirmaPickingHU(String VBELN, String hu);

  public ResultDTO reservarCarrilHU(String entrega, String hu, String matnr);

  public ResultDTO aumentaInventario(OrdenProduccionInputDTO orden, String hu);

  public ResultDTO insertProcesoContingencia_7(OrdenProduccionInputDTO orden, String hu);

  public int getFaltantes(String entry);

  public ResultDTO getAUFNRFromHu(String hu, String werks);

  public OrdenProduccionInputDTO validarOrdenEnPickin(String entry);

  public OrdenProduccionDTO detalleOrdenProduccionSoloCabecera(String aufnr, String werks);

  public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks);
}
