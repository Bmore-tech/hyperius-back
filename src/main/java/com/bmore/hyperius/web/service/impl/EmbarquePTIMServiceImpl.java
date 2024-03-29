package com.bmore.hyperius.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.old.IMEmbarquePTRepositoryOld;
import com.bmore.hyperius.web.service.EmbarquePTIMService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmbarquePTIMServiceImpl implements EmbarquePTIMService {

  @Autowired
  private IMEmbarquePTRepositoryOld embarquePTRepositoryOld;

  @Override
  public EntregaInputDTO validarEntrega(EntregaInputDTO entregaInput) {

    entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));

    // Limpiar para evitar errores con boton next de navegador
    entregaInput.setHu1(null);
    entregaInput.setHu2(null);
    entregaInput.setConfHus(null);

    EntregaInputDTO entregaInput2 = new EntregaInputDTO();

    // entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(),
    // 10));

    entregaInput2 = embarquePTRepositoryOld.validarEntregaPickin(entregaInput);

    ResultDTO resultDT = entregaInput2.getResultDT();

    if (resultDT.getId() == 1) {

      entregaInput.setMateriales(entregaInput2.getMateriales());

      resultDT = embarquePTRepositoryOld.reservaHus(entregaInput);
      log.error("ID:" + resultDT.getId());

      switch (resultDT.getId()) {

        // 2 - No existe HU
        // 3 - HU ya consumida
        // 4 - No viene lote en HU de VEPO
        // 5 - Error al ingresar registro a MCHB
        // 6 - Error al consumir HU de VEKP
        // 7 - Error al ingresar a zcontingencia el hu

        case 1:

          entregaInput.setMatnr(Utils.zeroClean(resultDT.getTypeS()));// Cantidad
          // Material
          entregaInput.setCant("" + resultDT.getTypeF());// Cantidad
          // embalaje
          entregaInput.setCantT("" + resultDT.getTypeBD());
          entregaInput.setUm(resultDT.getMsg());

          resultDT = embarquePTRepositoryOld.obtieneDescripcionMaterial(resultDT.getTypeS(), entregaInput.getEntrega());

          entregaInput.setMaktx(resultDT.getTypeS());

          resultDT.setMsg("Lugar reservado");
          break;
        case 5:
          resultDT.setMsg("No hay mas hus por pickear");
          break;
        case 10:
          resultDT.setMsg("Error desconocido");
          break;

      }

    }
    log.error("antes de regresar de BO: " + entregaInput.getMatnr());
    entregaInput.setResultDT(resultDT);
    log.error(entregaInput.getResultDT().getMsg());
    return entregaInput;

  }

  @Override
  public EntregaInputDTO consumirHU(EntregaInputDTO entregaInput) {

    entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));

    // Limpiar para evitar errores con boton next de navegador

    ResultDTO resultDT = new ResultDTO();
    entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));

    resultDT = embarquePTRepositoryOld.consumeHUs(entregaInput);

    switch (resultDT.getId()) {

      // --1, HU consumida
      // --2, HU no disponible para consumo
      // --3, HU consumida previamente
      // --4, HU No corresponde con material de entrega
      // --5, Cantidad de HU no corresponde con la asignada
      // --6, HU Bloqueada
      // --7, HU en QA
      // --8, Error al actualizar HU
      // --9, HU no existe

      case 1:
        resultDT.setMsg("Hu ingresada correctamente");
        break;
      case 2:
        resultDT.setMsg("HU no disponible para consumo");
        break;
      case 3:
        resultDT.setMsg("HU consumida previamente");
        break;
      case 4:
        resultDT.setMsg("HU No corresponde con material de entrega");
        break;
      case 5:
        resultDT.setMsg("Cantidad de HU no corresponde con la asignada por supervisor");
        break;
      case 6:
        resultDT.setMsg("HU Bloqueada");
        break;
      case 7:
        resultDT.setMsg("HU en QA, ¿Desea incluirla en el embarque?");
        break;
      case 8:
        resultDT.setMsg("Error al actualizar HU");
        break;
      case 9:
        resultDT.setMsg("HU no existe");
        break;

    }

    log.error("antes de regresar de BO: " + entregaInput.getMatnr());
    entregaInput.setResultDT(resultDT);
    return entregaInput;

  }
}
