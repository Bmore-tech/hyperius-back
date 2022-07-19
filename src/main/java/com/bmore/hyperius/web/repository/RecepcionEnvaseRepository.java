package com.bmore.hyperius.web.repository;

import java.util.HashMap;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EntregaDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface RecepcionEnvaseRepository {
  EntregaDTO getEntrega(EntregaDTO entregaInput);

  HashMap<String, String> getLgortsEntrega(String entrega);

  HashMap<String, String> getLgortsTabla();

  EntregaDTO getEntregaDetalle(EntregaDTO entrega);

  EntregaDTO getEntregaDetalleSoloCabecera(String vbeln);

  HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks);

  CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z, String LGORT, String VBELN,
      HashMap<String, String> carrilesBloqueados);

  ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

  EntregaInputDTO validarEntregaPickin(String entry);

  ResultDTO validarHU_(String entry, String HU);

  ResultDTO VRPTVALUE(String entry, String HU);

  ResultDTO borrarZHU(String entry, String HU);

  EntregaInputDTO getPositions(String entry, String HU);

  ResultDTO entryContabilizada(String entry);

  int getFaltantes(String entry);

  ResultDTO contabilizadoOK(String entry);

  ResultDTO getVBELNFromHuSAP(String hu, String werks);

  ResultDTO getVBELNFromHuBCPS(String hu, String werks);

  ResultDTO validarEntregaEnPicking(String VBELN);

  ResultDTO reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas);

  CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu);

  EntregaDetalleDTO getDataHU(String hu);

  EntregaDetalleDTO getDataHU_LQUA(String hu);

  ResultDTO confirmaPickingHU(String VBELN, String hu);

  CarrilesUbicacionDTO compararUbicacionesHUs(String hu1, String hu2);

  ResultDTO rollBackPickingHU(String VBELN, String hu);

  ResultDTO insertProcesoContingencia_3(EntregaInputDTO entrega, String hu);

  ResultDTO aumentaInventario(EntregaInputDTO entrega, String hu);

  ResultDTO insertProcesoContingencia_4_14_32(String werks, String VBELN, String lfart, String user);

  EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput);
}
