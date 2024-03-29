package com.bmore.hyperius.web.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.old.UbicacionPTRepository;
import com.bmore.hyperius.web.service.UbicacionPTService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UbicacionPTServiceImpl implements UbicacionPTService {

  @Autowired
  private UbicacionPTRepository ubicacionPTRepository;

  @Override
  public OrdenProduccionDTO validaOrden(OrdenProduccionDTO orden) {

    OrdenProduccionDTO ordenReturn = new OrdenProduccionDTO();
    ResultDTO resultDT = new ResultDTO();

    // valida que orden exista para centro

    orden.setOrdenProduccion(Utils.zeroFill(orden.getOrdenProduccion(), 12));

    ordenReturn = ubicacionPTRepository.getOrden(orden);

    log.error("retorno consulta OrdenPT DAO12345 :" + orden.getOrdenProduccion() + " " + orden.getWerks() + " "
        + ordenReturn.getResultDT().getId() + " " + ordenReturn.getResultDT().getMsg()
        + ordenReturn.getFabrica() + " " + ordenReturn.getFechaDocumento());

    switch (ordenReturn.getResultDT().getId()) {

      case 1:// existe orden

        resultDT.setId(ordenReturn.getResultDT().getId());
        resultDT.setMsg(ordenReturn.getResultDT().getMsg());
        log.error("Error entregaReturn: " + ordenReturn.getResultDT().getId());

        log.error("Existe entrega: " + resultDT.getId());

        ordenReturn.setPicking("false");
        ordenReturn.setContabilizar("false");
        ordenReturn.setContabilizada("false");

        OrdenProduccionDTO entregaReturn2 = new OrdenProduccionDTO();

        entregaReturn2 = ubicacionPTRepository.detalleOrdenProduccion(orden.getOrdenProduccion(), orden.getWerks());

        // Obtiene carriles bloqueados por otro proceso

        HashMap<String, String> carrilesBloqueados = ubicacionPTRepository.getCarrilesBloqueados("3", orden.getWerks());

        if (entregaReturn2.getResultDT().getId() == 1) {
          ordenReturn.setItems(entregaReturn2.getItems());

          CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();

          List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

          for (int x = 0; x < entregaReturn2.getItems().getItem().size(); x++) {

            try {
              entregaReturn2.getItems().getItem().get(x).setCajasAsignadas(
                  "" + new BigDecimal(entregaReturn2.getItems().getItem().get(x).getCajasAsignadas())
                      .setScale(3));
            } catch (Exception e) {

            }

            OrdenProduccionDetalleDTO materialItem = entregaReturn2.getItems().getItem().get(x);

            String matnr = materialItem.getMaterial();

            materialItem.setMaterial(Utils.zeroClean(materialItem.getMaterial()));

            materialItem.setPosicion(Utils.zeroClean(materialItem.getPosicion()));

            materialItem.setCajas(Utils.zeroClean(materialItem.getCajas()));

            CarrilesUbicacionDTO carrilesDAO = new CarrilesUbicacionDTO();
            // CarrilesUbicacionDTO carrilesDAOLV02 = new
            // CarrilesUbicacionDTO();

            String idLgort = "LV01";

            if (orden.getWerks().equals("EMZ1")) {
              idLgort = "PT01";
            }

            carrilesDAO = ubicacionPTRepository.getCarriles(orden.getWerks(), matnr, "ID_03", "3", idLgort,
                Utils.zeroFill(orden.getOrdenProduccion(), 12), carrilesBloqueados);

            for (int y = 0; y < carrilesDAO.getItem().size(); y++) {

              carrilesDAO.getItem().get(y).setMaterial(materialItem.getMaterial());

              carrilesDAO.getItem().get(y).setIdCarril(carrilesDAO.getItem().get(y).getLgnum() + ""
                  + carrilesDAO.getItem().get(y).getLgtyp() + "" + carrilesDAO.getItem().get(y).getLgpla()
                  + carrilesDAO.getItem().get(y).getCajas());

              carriles.add(carrilesDAO.getItem().get(y));

            }

            // if (orden.getWerks().equals("PC01")) {
            //
            // carrilesDAOLV02 = ubicacionPTRepository.getCarriles(orden
            // .getWerks(), matnr, "ID_03", "3", "LV02");
            //
            // for (int y = 0; y < carrilesDAOLV02.getItem().size();
            // y++) {
            //
            // carrilesDAOLV02.getItem().get(y).setMaterial(
            // materialItem.getMaterial());
            //
            // carrilesDAOLV02.getItem().get(y).setIdCarril(
            // carrilesDAOLV02.getItem().get(y).getLGNUM()
            // + ""
            // + carrilesDAOLV02.getItem().get(y)
            // .getLGTYP()
            // + ""
            // + carrilesDAOLV02.getItem().get(y)
            // .getLGPLA()
            // + carrilesDAOLV02.getItem().get(y)
            // .getCajas());
            //
            // carriles.add(carrilesDAOLV02.getItem().get(y));
            //
            // }
            //
            // }
          }

          carrilesList.setItem(carriles);
          ordenReturn.setCarriles(carrilesList);

        } else {
          resultDT.setId(2);
          resultDT.setMsg(entregaReturn2.getResultDT().getMsg());
        }
        // } else {
        //
        // }

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
  public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks) {

    for (int x = 0; x < carriles.getItem().size(); x++) {

      carriles.getItem().get(x).setEntrega(Utils.zeroFill(carriles.getItem().get(x).getEntrega(), 12));

      carriles.getItem().get(x).setMaterial(Utils.zeroFill(carriles.getItem().get(x).getMaterial(), 18));

    }

    return ubicacionPTRepository.ingresaDetalleEnvase(carriles.getItem().get(0).getEntrega(), carriles, user, werks);
  }
}