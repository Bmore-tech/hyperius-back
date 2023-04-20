package com.bmore.hyperius.web.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.EmbarquePTRepository;
import com.bmore.hyperius.web.service.EmbarquePTService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmbarquePTServiceImpl implements EmbarquePTService {

  @Autowired
  private EmbarquePTRepository embarquePTRepository;

  @Override
  public EmbarqueDTO validaEmbarque(EmbarqueDTO embarque) {

    EmbarqueDTO embarqueReturn = new EmbarqueDTO();
    ResultDTO resultDT = new ResultDTO();

    embarque.setOrdenEmbarque(Utils.zeroFill(embarque.getOrdenEmbarque(), 10));

    // valida que entrega exista para centro
    try {
      embarqueReturn = embarquePTRepository.getEmbarque(embarque);
    } catch (Exception e) {
      log.error("error" , e);
    }

    log.error("retorno consulta embarque DAO12345_ :" + embarque.getOrdenEmbarque() + " " + embarque.getWerks()
        + " " + embarqueReturn.getResultDT().getId() + " " + embarqueReturn.getResultDT().getMsg()
        + embarqueReturn.getFabrica() + " " + embarqueReturn.getFechaDocumento());

    switch (embarqueReturn.getResultDT().getId()) {

      case 1:// existe entrega

        resultDT.setId(embarqueReturn.getResultDT().getId());
        resultDT.setMsg(embarqueReturn.getResultDT().getMsg());

        embarqueReturn.setPicking("false");
        embarqueReturn.setContabilizar("false");
        embarqueReturn.setContabilizada("false");

        EmbarqueDTO embarqueReturn2 = new EmbarqueDTO();

        embarqueReturn2 = embarquePTRepository.getEmbarqueDetalle(embarque);

        if (embarqueReturn2.getResultDT().getId() == 1) {

          embarqueReturn.setItems(embarqueReturn2.getItems());

          CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();

          List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

          // Obtiene carriles bloqueados por otro proceso
          HashMap<String, String> carrilesBloqueados = embarquePTRepository.getCarrilesBloqueados("4",
              embarque.getWerks());

          for (int x = 0; x < embarqueReturn2.getItems().getItem().size(); x++) {

            // Obtiene carriles con materiales bloqueados
            HashMap<String, String> carrilesMaterialBloqueado = embarquePTRepository.getCarrilesMaterialBloqueado(
                embarque.getItems().getItem().get(x).getMaterial(), embarque.getWerks());

            CarrilesUbicacionDTO carrilesDAO = embarquePTRepository.getCarriles(embarque.getWerks(),
                embarqueReturn2.getItems().getItem().get(x).getMaterial(), "ID_04", "4", carrilesBloqueados,
                carrilesMaterialBloqueado);

            embarqueReturn2.getItems().getItem().get(x).setMaterial(Utils.zeroClean(

                embarqueReturn2.getItems().getItem().get(x).getMaterial()));

            try {
              embarqueReturn2.getItems().getItem().get(x).setCajasAsignadas(
                  "" + new BigDecimal(embarqueReturn2.getItems().getItem().get(x).getCajasAsignadas())
                      .setScale(3));
            } catch (Exception e) {

            }

            for (int y = 0; y < carrilesDAO.getItem().size(); y++) {

              carrilesDAO.getItem().get(y)
                  .setMaterial(embarqueReturn2.getItems().getItem().get(x).getMaterial());

              carrilesDAO.getItem().get(y).setIdCarril(carrilesDAO.getItem().get(y).getLgnum() + ""
                  + carrilesDAO.getItem().get(y).getLgtyp() + "" + carrilesDAO.getItem().get(y).getLgpla()
                  + "" + carrilesDAO.getItem().get(y).getCajas());

              carriles.add(carrilesDAO.getItem().get(y));
            }
          }

          carrilesList.setItem(carriles);
          embarqueReturn.setCarriles(carrilesList);

        } else {
          resultDT.setId(2);
          resultDT.setMsg(embarqueReturn2.getResultDT().getMsg());
        }

        // valida si se puede pickear

        int cont = 0;
        int x = 0;
        log.error("Validar cantidades");
        for (x = 0; x < embarqueReturn.getItems().getItem().size(); x++) {

          EmbarqueDetalleDTO item = embarqueReturn.getItems().getItem().get(x);

          try {

            if (new BigDecimal(item.getCajas().trim()).setScale(3)
                .equals(new BigDecimal(item.getCajasAsignadas().trim()).setScale(3))) {

              if (new BigDecimal(item.getCajas().trim()).intValue() > 0)
                cont++;
            }
          } catch (Exception e) {

            log.error("Error al sumar cajas: " + " " + e.toString());

          }
        }

        if (cont == x && x > 0) {// totalmente pickeado no permitir hacer
          // modificaciones
          ResultDTO resultDT2 = new ResultDTO();

          resultDT2 = embarquePTRepository.contabilizadoOK(embarque.getOrdenEmbarque());
          embarqueReturn.setPicking("false");
          if (resultDT2.getId() == 1) {

            embarqueReturn.setContabilizar("false");
            embarqueReturn.setContabilizada("true");
            embarqueReturn.setPicking("false");

          } else {
            embarqueReturn.setContabilizar("true");
            embarqueReturn.setContabilizada("false");
          }

        } else {

          EntregaInputDTO entregaInput = new EntregaInputDTO();
          entregaInput.setEntrega(embarque.getOrdenEmbarque());
          entregaInput.setWerks(embarque.getWerks());

          log.error("Error: entro a validar el ZPICKING");

          EntregaInputDTO resultDT1 = embarquePTRepository.validarEntregaPickinCompleto(entregaInput);

          log.error("Error: entro a validar el ZPICKING: " + resultDT1.getResultDT().getId());

          if (resultDT1.getResultDT().getId() == 1) {

            embarqueReturn.setPicking("true");
            embarqueReturn.setContabilizar("false");
            embarqueReturn.setContabilizada("false");

          } else {
            embarqueReturn.setPicking("false");
            embarqueReturn.setContabilizar("false");
            embarqueReturn.setContabilizada("false");
          }
        }

        break;
      case 2:
        // Entrega no existe
        break;
      case 3:// Entrega en picking

        break;
      case 4:// entrega contabilizada
             // resultDT.setId(1);
             // entregaReturn.setPicking("false");
             // entregaReturn.setContabilizar("false");
             // entregaReturn.setContabilizada("true");
        break;
      case 5:
        resultDT.setId(2);
        resultDT.setMsg("Engrega con error, en validaEntrega");
        // Entrega con error en datos de cabecera
        break;
      case 6:// entrega se puede contabilizar

        // resultDT.setId(1);
        // embarqueReturn.setPicking("false");
        // embarqueReturn.setContabilizada("false");
        // embarqueReturn.setContabilizar("true");

        break;

      default:
        resultDT.setId(2);
        resultDT.setMsg("Engrega con error, case default en validaEntrega");
        break;
    }

    embarqueReturn.setResultDT(resultDT);

    return embarqueReturn;
  }

  @Override
  public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks) {
    for (int x = 0; x < carriles.getItem().size(); x++) {
      carriles.getItem().get(x).setMaterial(Utils.zeroFill(carriles.getItem().get(x).getMaterial(), 18));
      carriles.getItem().get(x).setEntrega(Utils.zeroFill(carriles.getItem().get(x).getEntrega(), 10));
    }

    return embarquePTRepository.ingresaDetalleEnvase(carriles.getItem().get(0).getEntrega(), carriles, user, werks);
  }

  @Override
  public ResultDTO contabilizarEntregaEntrante(EmbarqueDTO embarqueDTO, String user) {
    embarqueDTO.setOrdenEmbarque(Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10));

    ResultDTO resultDT = new ResultDTO();
    if (embarqueDTO.getLfart().equals("Y010")) {
      resultDT = embarquePTRepository.contabilizaEntregaExport(embarqueDTO, user);
    } else {
      resultDT = embarquePTRepository.contabilizaEntrega(embarqueDTO, user);
    }

    switch (resultDT.getId()) {

      case 1:
        resultDT.setMsg("Entrega contabilizada con exito");
        break;
      case 4:
        resultDT.setMsg(
            "La entrega de salida no tiene asociado un transporte, contacte al departamento de sistemas");
        break;
      case 5:
        resultDT.setMsg("Error al ingresar inicio de carga");
        break;
      case 6:
        resultDT.setMsg("Error al ingresar registro 9 de contabilizacion");
        break;
      case 7:
        resultDT.setMsg("Error al ingresar registro 12 de doble facturacion");
        break;
      case 8:
        resultDT.setMsg("Error al actulizar el status del transporte");
        break;
      case 9:
        resultDT.setMsg("Entrega contabilizada con exito");
        break;
      case 10:
        resultDT.setMsg("Error al ingresar inicio de carga");
        break;
      case 11:
        resultDT.setMsg("Entrega contabilizada con exito");
        break;

      // 1 CORRECTO
      // 4 LA ENGREGA DE SALIDA NO TIENE ASOSCIADO UN TRANSPORTE, CONTACTE AL
      // DEPARTAMENTO DE SISTEMAS
      // 5 ERROR AL INSERTAR EN ZCONTINGENCIA CON ID_PROC
      // 6 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 9

      // 7 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 12

      // 8 ERROR AL ACTUALIZAR STATUS 5 TRANSPORTE
      // 9 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 1
      // 10 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 10
      // 11 NO ES FACTURABLE

    }

    log.error(resultDT.toString());
    return resultDT;

  }

  @Override
  public ResultDTO limpiarPendientes(String vbeln) {
    vbeln = Utils.zeroFill(vbeln, 10);
    return embarquePTRepository.limpiaPendientes(vbeln);
  }

  @Override
  public boolean isWerksAllowed(HuDTO huDTO) {
    boolean isValid = false;

    if (embarquePTRepository.isRstAllowed(huDTO) > 0) {
      isValid = true;
    }

    return isValid;
  }

  @Override
  public ResultDTO cambiarCantidadOrdenProduccion(EmbarqueDetalleDTO embarqueDetalleDTO, String user, String werks) {

    ResultDTO resultDt = new ResultDTO();
    embarqueDetalleDTO.setMaterial(Utils.zeroFill(embarqueDetalleDTO.getMaterial(), 18));
    embarqueDetalleDTO.setVbeln(Utils.zeroFill(embarqueDetalleDTO.getVbeln(), 10));

    resultDt = embarquePTRepository.cambiarCantidadOrdenProduccion(embarqueDetalleDTO, user, werks);

    switch (resultDt.getId()) {

      case 1:

        resultDt.setMsg("Orden modificada con exito");

        break;

      default:
        resultDt.setMsg("Error al modificar la cantidad de la orden.");
        break;

    }

    return resultDt;
  }
}
