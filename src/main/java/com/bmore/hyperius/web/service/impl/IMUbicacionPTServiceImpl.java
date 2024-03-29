package com.bmore.hyperius.web.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.old.VidrioUbicacionPTRepository;
import com.bmore.hyperius.web.service.IMUbicacionPTService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IMUbicacionPTServiceImpl implements IMUbicacionPTService {

  @Autowired
  private VidrioUbicacionPTRepository vidrioUbicacionPTRepository;

  @Override
  public OrdenProduccionDTO validaOrden(OrdenProduccionDTO orden) {

    OrdenProduccionDTO ordenReturn = new OrdenProduccionDTO();

    ResultDTO resultDT = new ResultDTO();

    // valida que orden exista para centro

    orden.setOrdenProduccion(Utils.zeroFill(orden.getOrdenProduccion(), 12));

    ordenReturn = vidrioUbicacionPTRepository.getOrden(orden);

    switch (ordenReturn.getResultDT().getId()) {

      case 1:// existe orden

        resultDT.setId(ordenReturn.getResultDT().getId());
        resultDT.setMsg(ordenReturn.getResultDT().getMsg());

        ordenReturn.setPicking("false");
        ordenReturn.setContabilizar("false");
        ordenReturn.setContabilizada("false");

        OrdenProduccionDTO entregaReturn2 = new OrdenProduccionDTO();

        entregaReturn2 = vidrioUbicacionPTRepository.detalleOrdenProduccion(orden.getOrdenProduccion(),
            orden.getWerks());

        log.error("result detalle entregaReturn2: " + entregaReturn2.getResultDT().getId());

        if (entregaReturn2.getResultDT().getId() == 1) {
          ordenReturn.setItems(entregaReturn2.getItems());

          for (int x = 0; x < entregaReturn2.getItems().getItem().size(); x++) {

            try {
              entregaReturn2.getItems().getItem().get(x).setCajasAsignadas(
                  "" + new BigDecimal(entregaReturn2.getItems().getItem().get(x).getCajasAsignadas())
                      .setScale(3));
            } catch (Exception e) {

            }

            OrdenProduccionDetalleDTO materialItem = entregaReturn2.getItems().getItem().get(x);

            materialItem.setMaterial(Utils.zeroClean(materialItem.getMaterial()));

            materialItem.setPosicion(Utils.zeroClean(materialItem.getPosicion()));

            materialItem.setCajas(Utils.zeroClean(materialItem.getCajas()));
          }

        } else {
          resultDT.setId(2);
          resultDT.setMsg(entregaReturn2.getResultDT().getMsg());

        }

        break;
      case 2:
        // Entrega no existe
        break;
      case 3:// Entrega en picking //AQUI VA CAMBIO MODIFICAR CANTIDADES

        break;
      case 4:// entrega contabilizada

        break;
      case 5:
        resultDT.setId(2);
        // Entrega con error en datos de cabecera
        break;

      default:
        resultDT.setId(2);
        resultDT.setMsg("Orden de produccion con error, case default en validaOrdenProduccion");
        break;
    }

    ordenReturn.setResultDT(resultDT);

    return ordenReturn;
  }

  @Override
  public ResultDTO vidrioUbicaPT(OrdenProduccionInputDTO ordenProduccion) {

    ResultDTO result = new ResultDTO();

    result = vidrioUbicacionPTRepository.vidrioUbicaPT(ordenProduccion);

    switch (result.getId()) {

      // 2 - No existe HU
      // 3 - HU ya consumida
      // 4 - No viene lote en HU de VEPO
      // 5 - Error al ingresar registro a MCHB
      // 6 - Error al consumir HU de VEKP
      // 7 - Error al ingresar a zcontingencia el hu

      case 1:
        result.setMsg("Hu ingresada correctamente.");
        break;
      case 2:
        result.setMsg("No existe HU.");
        break;
      case 3:
        result.setMsg("HU ya consumida.");
        break;
      case 4:
        result.setMsg("No viene lote en HU de VEPO.");
        break;
      case 5:
        result.setMsg("Error al ingresar registro a MCHB.");
        break;
      case 6:
        result.setMsg("Error al consumir HU de VEKP.");
        break;
      case 7:
        result.setMsg("Error al ingresar a zcontingencia el hu.");
        break;

    }

    return result;

  }
}
