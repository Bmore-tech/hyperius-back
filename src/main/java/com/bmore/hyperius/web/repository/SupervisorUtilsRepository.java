package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDTO;
import com.bmore.hyperius.web.dto.FTPConfDTO;
import com.bmore.hyperius.web.dto.HUsEnTransporteDetalleDTO;
import com.bmore.hyperius.web.dto.HusEnTransporteDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.ListaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.UsuarioDTO;
import com.bmore.hyperius.web.dto.UsuarioItemDTO;

public interface SupervisorUtilsRepository {
  EntregasTransportesDTO obtieneEntrega(String tknum, String werks);

  EntregasTransportesDTO obtieneEntregas(String werks);

  InventarioDTO obtieneInventario(String werks);

  InventarioDTO obtieneInventarioLotes(String werks);

  HusEnTransporteDTO obtieneCarrilesBloqueados(String proceso, String vbeln, String werks);

  HusEnTransporteDTO obtieneMaterialesBloqueados(String proceso, String vbeln, String werks);

  ResultDTO limpiaCarril(CarrilUbicacionDTO carril);

  HusEnTransporteDTO obtieneHusBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks);

  HusEnTransporteDTO obtieneHusIMBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks);

  ResultDTO validarCarril(CarrilUbicacionDTO carril);

  ListaDTO getCentros();

  FTPConfDTO getFTPConf();

  ListaDTO getTablas(String werks);

  ResultDTO bulk(String tabla, String werks);

  void removeQuality();

  ResultDTO eliminaDuplicados();

  ResultDTO validaInicioBCPS(String werks);

  UsuarioItemDTO buscarUsuario(String idUser);

  ResultDTO eliminarUsuario(String idUser);

  ResultDTO crearUsuario(UsuarioDTO user);

  ResultDTO modificarUsuario(UsuarioDTO user);

  ResultDTO limpiaTablasCentro(String werks);

  EntregasTransportesDTO obtieneEntregasAgencias(UsuarioDTO usuario);

  CarrilesBloqueadosDTO obtieneCarrilesBloqueados(UsuarioDTO usuario);

  EmbarqueDTO obtieneEntregasAgenciasDetalle(String vbeln);

  ResultDTO initialSnapshot(String werks);
}
