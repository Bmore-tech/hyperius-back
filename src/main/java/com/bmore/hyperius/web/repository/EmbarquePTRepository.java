package com.bmore.hyperius.web.repository;

import java.util.HashMap;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.export.ExportacionDatasource;
import com.bmore.hyperius.web.utils.remission.RemisionDatasource;

public interface EmbarquePTRepository {

  ResultDTO cambiarCantidadOrdenProduccion(EmbarqueDetalleDTO embarqueDetalleDTO, String user, String werks);

  ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput);

  ResultDTO contabilizadoOK(String entry);

  ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user);

  ResultDTO contabilizaEntregaExport(EmbarqueDTO embarqueDTO, String user);

  CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z,
      HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado);

  HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks);

  HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks);

  EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

  EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput);

  EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarque);

  ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

  int isRstAllowed(HuDTO huDTO);

  ResultDTO limpiaPendientes(String vbeln);

  ResultDTO limpiaPendientesXUsuario(String vbeln, String user);

  String obtenerCliente(String noEntrega, String num);

  String obtenerDatosCliente(String noEntrega);

  String obtenerDirPlanta(String noEntrega);

  String obtenerFecha();

  String obtenerPlanta(String noEntrega);

  RemisionDatasource obtenerTabla(String noEntrega);

  ExportacionDatasource obtenerTablaExp(String noEntrega);

  EntregaInputDTO reservaUbicaciones(EntregaInputDTO entregaInput);

  ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu);

  EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput);

  EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput);

}
