package com.bmore.hyperius.web.repository;

import java.util.HashMap;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface AlimentacionLineaRepository {

  Integer confirmaHUenDepa(OrdenProduccionInputDTO ordenProduccionInput, String hu);

  Integer consumeInventario(String hu, OrdenProduccionInputDTO ordenProduccionInput);

  OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks);

  CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String IP_PR_Z,
      HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado);

  HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks);

  HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks);

  OrdenProduccionDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

  OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput);

  ResultDTO ingresaDetalleEnvase(String aufnr, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

  int insertProcesoContingencia_5(OrdenProduccionInputDTO ordenProduccionInput, String hu);

  int limpiaPendientes(String vbeln);

  int limpiaPendientesXUsuario(String vbeln, String user);

  OrdenProduccionInputDTO obtieneDepaletizadora(OrdenProduccionInputDTO ordenProduccionInput);

  OrdenProduccionInputDTO obtieneReservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput);

  int reservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput);

  int reservaUbicacionHU2(OrdenProduccionInputDTO ordenProduccionInput);

  ResultDTO validaPickeoPrevioHU(OrdenProduccionInputDTO ordenProduccionInput, String hu);

  ResultDTO validarEntregaPickin(OrdenProduccionInputDTO ordenProduccionInput);
}
