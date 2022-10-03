package com.bmore.hyperius.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.AlmacenDTO;
import com.bmore.hyperius.web.dto.AlmacenesDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.old.SupervisorUtilsTraspasosRepository;
import com.bmore.hyperius.web.service.SupervisorUtilsTraspasosService;
import com.bmore.hyperius.web.utils.Utils;

@Service
public class SupervisorUtilsTraspasosServiceImpl implements SupervisorUtilsTraspasosService {

  @Autowired
  private SupervisorUtilsTraspasosRepository supervisorUtilsTraspasosRepository;

  // private final Logger LOCATION = LoggerFactory.getLogger(getClass());

  @Override
  public AlmacenesDTO lgortPermitidos(String werks) {
    AlmacenesDTO result = supervisorUtilsTraspasosRepository.lgortPermitidos(werks);

    if (result.getItems().getItem().size() > 0) {
      AlmacenDTO blank = new AlmacenDTO();
      result.getItems().getItem().add(0, blank);
    }

    return result;
  }

  @Override
  public AlmacenesDTO lgnumPermitidos(AlmacenDTO almacen) {
    AlmacenesDTO result = supervisorUtilsTraspasosRepository.lgnumPermitidos(almacen);

    if (result.getItems().getItem().size() > 0) {
      AlmacenDTO blank = new AlmacenDTO();
      result.getItems().getItem().add(0, blank);
    }

    return result;
  }

  @Override
  public AlmacenesDTO lgtypPermitidos(AlmacenDTO almacen) {
    AlmacenesDTO result = supervisorUtilsTraspasosRepository.lgtypPermitidos(almacen);

    if (result.getItems().getItem().size() > 0) {
      AlmacenDTO blank = new AlmacenDTO();
      result.getItems().getItem().add(0, blank);
    }

    return result;
  }

  @Override
  public AlmacenesDTO lgplaPermitidos(AlmacenDTO almacen) {
    AlmacenesDTO result = supervisorUtilsTraspasosRepository.lgplaPermitidos(almacen);

    if (result.getItems().getItem().size() > 0) {
      AlmacenDTO blank = new AlmacenDTO();
      result.getItems().getItem().add(0, blank);
    }

    return result;
  }

  @Override
  public InventarioDTO lquaBusquedaTraspasos(AlmacenDTO almacen) {
    InventarioDTO result = new InventarioDTO();

    if (almacen.getCharg() == null || almacen.getCharg().equals("")) {
      result = supervisorUtilsTraspasosRepository.lquaBusquedaTraspasos(almacen, 1);
    } else {

      result = supervisorUtilsTraspasosRepository.lquaBusquedaTraspasos(almacen, 2);
    }

    if (result.getResultDT().getId() == 1) {
      for (int x = 0; x < result.getItems().getItem().size(); x++) {
        result.getItems().getItem().get(x)
            .setMatnr(Utils.zeroClean(result.getItems().getItem().get(x).getMatnr()));

      }
    }

    return result;
  }

  @Override
  public ResultDTO traspaso(InventarioDetalleDTOItem inventarioDetalleDTOItem, String user) {
    String logErrores = "";
    ResultDTO resultM = new ResultDTO();

    resultM.setId(1);

    for (int x = 0; x < inventarioDetalleDTOItem.getItem().size(); x++) {
      ResultDTO result = new ResultDTO();
      result = supervisorUtilsTraspasosRepository.traspaso(inventarioDetalleDTOItem.getItem().get(x), user);

      switch (result.getId()) {
        case 1:// Todo OK

          break;
        case 3:
          resultM.setId(result.getId());
          logErrores += "No fue posible ingresar la nueva ubicación de la HU "
              + inventarioDetalleDTOItem.getItem().get(x).getLenum() + " en la tabla ZContingencia\n";
          break;
        case 4:
          resultM.setId(result.getId());
          logErrores += "No fue posible actualizar la nueva ubicación de la HU "
              + inventarioDetalleDTOItem.getItem().get(x).getLenum() + " en la tabla LQUA\n";
          break;
        case 800:
          resultM.setId(result.getId());
          logErrores += "HU: " + inventarioDetalleDTOItem.getItem().get(x).getLenum() + " " + result.getMsg()
              + "\n";
          break;
      }

    }

    if (resultM.getId() == 1) {
      resultM.setMsg(
          "El traspaso se realizó correctamente, por  favor verifique que se realice el traspaso físico de las HUs");
    } else {
      resultM.setMsg(logErrores);
    }

    return resultM;
  }
}
