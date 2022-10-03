package com.bmore.hyperius.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;
import com.bmore.hyperius.web.repository.old.TransportesRepository;
import com.bmore.hyperius.web.service.TransportesService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransportesServiceImpl implements TransportesService {

  @Autowired
  private TransportesRepository transportesRepository;

  @Override
  public TransportesDTO existeTransporteEntrada(TransportesDTO transporteDTO) {

    TransportesDTO transporteDTOReturn = new TransportesDTO();

    // Primer intento trata de obtener transporte en estatus fin de
    // transporte

    transporteDTO.setIdTransporte(Utils.zeroFill(transporteDTO.getIdTransporte(), 10));

    transporteDTOReturn.setResultDT(
        transportesRepository.obtieneTransporte(transporteDTO.getIdTransporte(), transporteDTO.getWerks(), 1));

    if (transporteDTOReturn.getResultDT().getId() != 1) {
      // Primer intento trata de obtener transporte en estatus Registro de
      // transporte en planta
      transporteDTOReturn.setResultDT(
          transportesRepository.obtieneTransporte(transporteDTO.getIdTransporte(), transporteDTO.getWerks(), 2));
    }

    if (transporteDTOReturn.getResultDT().getId() == 1) {

      transporteDTOReturn = transportesRepository.getStatusTransporte(transporteDTO.getIdTransporte(),
          transporteDTO.getWerks(), "6");

      log.error("Fin de transporte: " + transporteDTOReturn.getResultDT().getId() + " "
          + transporteDTO.getIdTransporte());

      if (transporteDTOReturn.getResultDT().getId() == 1) {// Status FIN
        // de
        // transporte
        transporteDTOReturn.setIdStatusTransporte("6");

      } else {// Buscar status de entrada Carga para transporte
        transporteDTOReturn = transportesRepository.getStatusTransporte(transporteDTO.getIdTransporte(),
            transporteDTO.getWerks(), "1");

        log.error("entrada carga: " + transporteDTOReturn.getResultDT().getId() + " "
            + transporteDTO.getIdTransporte());
        if (transporteDTOReturn.getResultDT().getId() == 1) {// Status
          // ENTRADA
          // CARGA
          // de
          // transporte
          transporteDTOReturn.setIdStatusTransporte("1");

        } else {
          transporteDTOReturn.getResultDT().setId(3);
          transporteDTOReturn.getResultDT().setMsg("El transporte ya fue registrado.");
        }
      }

    } else {
      transporteDTOReturn.getResultDT().setId(2);
      transporteDTOReturn.getResultDT().setMsg("Transporte no encontrado");

    }

    return transporteDTOReturn;

  }

  @Override
  public TransportesDTO existeTransporteSalida(TransportesDTO transporteDTO) {

    transporteDTO.setIdTransporte(Utils.zeroFill(transporteDTO.getIdTransporte(), 10));

    TransportesDTO transporteDTOReturn = new TransportesDTO();

    transporteDTOReturn.setResultDT(
        transportesRepository.obtieneTransporte(transporteDTO.getIdTransporte(), transporteDTO.getWerks(), 2));

    if (transporteDTOReturn.getResultDT().getId() == 1) {

      transporteDTOReturn = transportesRepository.getStatusTransporte(transporteDTO.getIdTransporte(),
          transporteDTO.getWerks(), "5");

      log.error("Despacho de expedición: " + transporteDTOReturn.getResultDT().getId() + " "
          + transporteDTO.getIdTransporte());

      if (transporteDTOReturn.getResultDT().getId() == 1) {// Status FIN
        // de
        // transporte
        transporteDTOReturn.setIdStatusTransporte("5");

      } else {
        transporteDTOReturn.getResultDT().setId(3);
        transporteDTOReturn.getResultDT().setMsg("El transporte ya fue registrado.");
      }

    } else {
      transporteDTOReturn.getResultDT().setId(2);
      transporteDTOReturn.getResultDT().setMsg("Transporte no encontrado");

    }

    return transporteDTOReturn;

  }

  @Override
  public ResultDTO updateStatusTransporte(TransportesDTO transporteDTO, String user) {

    ResultDTO resultDT = new ResultDTO();
    ResultDTO resultDT2 = new ResultDTO();

    transporteDTO.setIdTransporte(Utils.zeroFill(transporteDTO.getIdTransporte(), 10));

    if (transporteDTO.getIdStatusTransporte().equals("1")) {

      resultDT = transportesRepository.updateTransporte(transporteDTO.getIdTransporte(), transporteDTO.getWerks(), "2",
          null);

      log.error("REsultado update insertProcesoContingencia1: " + transporteDTO.getWerks() + " "
          + transporteDTO.getIdTransporte() + " " + user + "  resultado: "

          + resultDT2.getId());

      if (resultDT.getId() == 1) {
        resultDT.setMsg("Folio de transporte registrado correctamente");
        resultDT2 = transportesRepository.insertProcesoContingenciaTransportes(transporteDTO.getWerks(),
            transporteDTO.getIdTransporte(), user, "1");

        if (resultDT2.getId() != 1) {
          resultDT = resultDT2;
        }
      }

    } else if (transporteDTO.getIdStatusTransporte().equals("6")) {

      resultDT = transportesRepository.updateTransporte(transporteDTO.getIdTransporte(), transporteDTO.getWerks(), "7",
          null);

      log.error("REsultado update insertProcesoContingenci2: " + transporteDTO.getWerks() + " "
          + transporteDTO.getIdTransporte() + " " + user + "  resultado: "

          + resultDT2.getId());

      if (resultDT.getId() == 1) {
        resultDT.setMsg("Fin de transporte registrado correctamente");
        resultDT2 = transportesRepository.insertProcesoContingenciaTransportes(transporteDTO.getWerks(),
            transporteDTO.getIdTransporte(), user, "2");

        if (resultDT2.getId() != 1) {
          resultDT = resultDT2;
        }
      }

    } else if (transporteDTO.getIdStatusTransporte().equals("5")) {

      resultDT = transportesRepository.updateTransporte(transporteDTO.getIdTransporte(), transporteDTO.getWerks(), "6",
          "X");

      log.error("REsultado update insertProcesoContingenci2: " + transporteDTO.getWerks() + " "
          + transporteDTO.getIdTransporte() + " " + user + "  resultado: "

          + resultDT2.getId());

      if (resultDT.getId() == 1) {
        resultDT.setMsg("Inicio de transporte registrado correctamente");
        resultDT2 = transportesRepository.insertProcesoContingenciaTransportes(transporteDTO.getWerks(),
            transporteDTO.getIdTransporte(), user, "11");

        if (resultDT2.getId() != 1) {
          resultDT = resultDT2;
        }
      }

    } else {
      resultDT.setId(2);
      resultDT.setMsg("Error al actualizar transporte, no se encontro status correcto");
    }

    return resultDT;
  }
}
