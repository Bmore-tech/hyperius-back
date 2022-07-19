package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface IMEmbarquePTRepository {

  ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput, String hu);

  ResultDTO consumeHUs(EntregaInputDTO entregaInput);

  ResultDTO contabilizadoOK(String entry);

  ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user);

  EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

  EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput);

  EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarqueDTO);

  CarrilesUbicacionDTO getStock(String werks, String matnr);

  ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

  ResultDTO obtieneDescripcionMaterial(String matnr, String vblen);

  ResultDTO obtieneEntregaDeTransporte(String tknum);

  EntregaInputDTO obtieneReservaUbicacionHU1(EntregaInputDTO entregaInput);

  ResultDTO reservaHus(EntregaInputDTO entregaInput);

  ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu);

  EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput);

  EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput);
}
