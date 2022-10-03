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
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.AlimentacionLineaRepository;
import com.bmore.hyperius.web.service.AlimentacionLineaService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlimentacionLineaServiceImpl implements AlimentacionLineaService {

  // private AlimentacionLineaRepositoryOld alimentacionLineaRepositoryOld = new
  // AlimentacionLineaRepositoryOld();

  @Autowired
  private AlimentacionLineaRepository alimentacionLineaRepository;

  @Override
  public OrdenProduccionDTO validaOrden(OrdenProduccionDTO orden) {

    OrdenProduccionDTO ordenReturn = new OrdenProduccionDTO();
    ResultDTO resultDT = new ResultDTO();

    // valida que orden exista para centro

    orden.setOrdenProduccion(Utils.zeroFill(orden.getOrdenProduccion(), 12));

    ordenReturn = alimentacionLineaRepository.getOrden(orden);

    log.error("retorno consulta Entrega DAO12345 :" + orden.getOrdenProduccion() + " " + orden.getWerks() + " "
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

        entregaReturn2 = alimentacionLineaRepository.detalleOrdenProduccion(orden.getOrdenProduccion(),
            orden.getWerks());
        log.error("result detalle entregaReturn2: " + entregaReturn2.getResultDT().getId());

        if (entregaReturn2.getContabilizar().equals("false")) {

          if (entregaReturn2.getResultDT().getId() == 1) {
            ordenReturn.setItems(entregaReturn2.getItems());

            CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();

            List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

            // Obtiene carriles bloqueados por otro proceso
            HashMap<String, String> carrilesBloqueados = alimentacionLineaRepository.getCarrilesBloqueados("2",
                orden.getWerks());

            for (int x = 0; x < entregaReturn2.getItems().getItem().size(); x++) {

              // Obtiene carriles con materiales bloqueados
              HashMap<String, String> carrilesMaterialBloqueado = alimentacionLineaRepository
                  .getCarrilesMaterialBloqueado(entregaReturn2.getItems().getItem().get(x).getMaterial(),
                      orden.getWerks());

              CarrilesUbicacionDTO carrilesDAO = alimentacionLineaRepository.getCarriles(orden.getWerks(),
                  entregaReturn2.getItems().getItem().get(x).getMaterial(), "ID_02", "2",
                  carrilesBloqueados, carrilesMaterialBloqueado);

              entregaReturn2.getItems().getItem().get(x)
                  .setMaterial(Utils.zeroClean(entregaReturn2.getItems().getItem().get(x).getMaterial()));

              try {
                entregaReturn2.getItems().getItem().get(x).setCajasAsignadas(
                    "" + new BigDecimal(entregaReturn2.getItems().getItem().get(x).getCajasAsignadas())
                        .setScale(3));
              } catch (Exception e) {

              }

              for (int y = 0; y < carrilesDAO.getItem().size(); y++) {

                carrilesDAO.getItem().get(y)
                    .setMaterial(entregaReturn2.getItems().getItem().get(x).getMaterial());

                carrilesDAO.getItem().get(y)
                    .setIdCarril(carrilesDAO.getItem().get(y).getLgnum() + ""
                        + carrilesDAO.getItem().get(y).getLgtyp() + ""
                        + carrilesDAO.getItem().get(y).getLgpla()
                        + carrilesDAO.getItem().get(y).getCajas());

                carriles.add(carrilesDAO.getItem().get(y));

              }
            }

            carrilesList.setItem(carriles);
            ordenReturn.setCarriles(carrilesList);
          } else {
            resultDT.setId(2);
            resultDT.setMsg(entregaReturn2.getResultDT().getMsg());
          }
        } else {

          // ResultDT resultDT2 = new ResultDT();
          //
          // resultDT2 = alimentacionLineaDAO.validarcontabilizar(orden
          // .getOrdenProduccion());
          //
          // LOCATION.error("Error resultDT2 contabilizado: "
          // + resultDT2.getId());
          //
          // if (resultDT2.getId() == 1) {
          // ordenReturn.setPicking("false");
          // ordenReturn.setContabilizar("false");
          // ordenReturn.setContabilizada("true");
          // } else {
          //
          // ordenReturn.setPicking("false");
          // ordenReturn.setContabilizar("true");
          // ordenReturn.setContabilizada("false");
          // }
          //
          // resultDT.setId(1);
          // LOCATION.error("Error resultDT2 contabilizado: "
          // + resultDT2.getId());

        }

        break;
      case 2:
        // Entrega no existe
        resultDT.setId(2);
        resultDT.setMsg("Orden de produccion no existe");
        break;
      case 3:// Entrega en picking //AQUI VA CAMBIO MODIFICAR CANTIDADES
        resultDT.setId(2);
        resultDT.setMsg("Entrega en picking");
        // ResultDT resultDT2 = new ResultDT();
        //
        // resultDT2 = alimentacionLineaDAO.validarcontabilizar(orden
        // .getOrdenProduccion());
        //
        // if (resultDT2.getId() == 1) {
        //
        // if (resultDT2.getTypeI() == 0) {// posible contabilizar
        //
        // resultDT2 = alimentacionLineaDAO.validarcontabilizar(orden
        // .getOrdenProduccion());
        //
        // LOCATION.error("Error resultDT2 contabilizado: "
        // + resultDT2.getId());
        //
        // if (resultDT2.getId() == 1) {
        // ordenReturn.setPicking("false");
        // ordenReturn.setContabilizar("false");
        // ordenReturn.setContabilizada("true");
        // } else {
        //
        // ordenReturn.setPicking("false");
        // ordenReturn.setContabilizar("true");
        // ordenReturn.setContabilizada("false");
        // }
        //
        // resultDT.setId(1);
        // LOCATION.error("Error resultDT2 contabilizado: "
        // + resultDT2.getId());
        //
        // } else {// Falta consumir N Hus
        // ordenReturn.setPicking("true");
        // ordenReturn.setContabilizar("false");
        // ordenReturn.setContabilizada("false");
        // resultDT.setId(1);
        // }
        //
        // } else {
        // resultDT.setId(2);
        // resultDT.setMsg(resultDT2.getMsg());
        // }

        break;
      case 4:// entrega contabilizada
             // resultDT.setId(1);
             // entregaReturn.setPicking("false");
             // entregaReturn.setContabilizar("false");
             // entregaReturn.setContabilizada("true");
        resultDT.setId(1);
        resultDT.setMsg("Entrega contabilizada");
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
    log.error("retorno consulta OrdenProduccion:" + orden.getOrdenProduccion() + " " + orden.getWerks() + " "
        + ordenReturn.getResultDT().getId() + " " + ordenReturn.getResultDT().getMsg()
        + ordenReturn.getFabrica() + " " + ordenReturn.getFechaDocumento());
    return ordenReturn;
  }

  @Override
  public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks) {
    for (int x = 0; x < carriles.getItem().size(); x++) {

      carriles.getItem().get(x).setMaterial(Utils.zeroFill(carriles.getItem().get(x).getMaterial(), 18));
      carriles.getItem().get(x).setEntrega(Utils.zeroFill(carriles.getItem().get(x).getEntrega(), 12));

    }

    return alimentacionLineaRepository.ingresaDetalleEnvase(carriles.getItem().get(0).getEntrega(), carriles, user,
        werks);
  }

  @Override
  public int limpiarPendientes(String vbeln) {
    vbeln = Utils.zeroFill(vbeln, 12);

    return alimentacionLineaRepository.limpiaPendientes(vbeln);
  }
}