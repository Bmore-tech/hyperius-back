package com.bmore.hyperius.web.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.CreacionEntregaItemDTO;
import com.bmore.hyperius.web.dto.CreacionEntregasDTO;
import com.bmore.hyperius.web.dto.CrecionEntregaDTO;
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.ControlPaletizadoraRepository;
import com.bmore.hyperius.web.repository.old.CreacionEntregasRepository;
import com.bmore.hyperius.web.service.CreacionEntregasService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CreacionEntregasServiceImpl implements CreacionEntregasService {

  @Autowired
  private ControlPaletizadoraRepository controlPaletizadoraRepository;

  @Autowired
  private CreacionEntregasRepository creacionEntregasRepository;

  // private ControlPaletizadoraRepositoryOld controlPaletizadoraDAO = new
  // ControlPaletizadoraRepositoryOld();

  @Override
  public CreacionEntregasDTO obtieneMateriales() {
    CreacionEntregasDTO materialesDTO = creacionEntregasRepository.obtieneMaterialDAO();
    if (materialesDTO.getResultDT().getId() == 1) {
      for (int x = 0; x < materialesDTO.getItems().getItem().size(); x++) {
        CrecionEntregaDTO matDto = materialesDTO.getItems().getItem().get(x);
        matDto.setMatnr(Utils.zeroClean(matDto.getMatnr()));
      }
    }
    return materialesDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneTarimas(CrecionEntregaDTO materialDTO) {
    CreacionEntregasDTO tranporteDTO = creacionEntregasRepository.obtieneTarimasDAO(materialDTO);
    if (tranporteDTO.getResultDT().getId() == 1) {
      for (int x = 0; x < tranporteDTO.getItems().getItem().size(); x++) {
        CrecionEntregaDTO matDto = tranporteDTO.getItems().getItem().get(x);
        matDto.setPacknr(Utils.zeroClean(matDto.getPacknr()));
      }
    }
    return tranporteDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneTransportes(CrecionEntregaDTO materialDTO) {
    CreacionEntregasDTO tarimasDTO = creacionEntregasRepository.obtieneTransportesDAO(materialDTO);
    if (tarimasDTO.getResultDT().getId() == 1) {
      for (int x = 0; x < tarimasDTO.getItems().getItem().size(); x++) {
        CrecionEntregaDTO matDto = tarimasDTO.getItems().getItem().get(x);
        matDto.setTknum(Utils.zeroClean(matDto.getTknum()));
        matDto.setWerks(Utils.zeroClean(matDto.getWerks()));
        matDto.setWerksD(Utils.zeroClean(matDto.getWerksD()));
      }
    }
    return tarimasDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneCentros() {
    CreacionEntregasDTO centrosDTO = creacionEntregasRepository.obtieneCentrosDAO();

    return centrosDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneAgencias() {
    CreacionEntregasDTO agenciasDTO = creacionEntregasRepository.obtieneAgenciasDAO();

    for (int x = 0; x < agenciasDTO.getItems().getItem().size(); x++) {
      CrecionEntregaDTO item = agenciasDTO.getItems().getItem().get(x);

      item.setLifnr(Utils.zeroClean(item.getLifnr() + ""));

    }

    return agenciasDTO;
  }

  @Override
  public ResultDTO crearEntrega(CreacionEntregaItemDTO crearEntrega, String werks, String user) {

    ResultDTO resultDT = new ResultDTO();
    String entrega = "";

    // Crear entrega en Tabla unica de entragas BCP y generar cabecera en
    // LIKP

    if (crearEntrega.getItem().size() > 0) {

      try {
        // Puede ser por el tamaño del campo material.
        Long.parseLong(crearEntrega.getItem().get(0).getLifnr().trim());
        crearEntrega.getItem().get(0)
            .setLifnr(Utils.zeroFill(crearEntrega.getItem().get(0).getLifnr().trim(), 10));
      } catch (Exception e) {
        // Proveedor fue alfanumerico, no se agregan 0s
      }

      resultDT = creacionEntregasRepository.creacionEntregaBCPS(crearEntrega.getItem().get(0), user);

      if (resultDT.getId() == 1) {
        entrega = resultDT.getMsg();

        // Crear posiciones en LIPS

        for (int x = 0; x < crearEntrega.getItem().size(); x++) {

          CrecionEntregaDTO itemEntrega = crearEntrega.getItem().get(x);

          itemEntrega.setPacknr(Utils.zeroFill(itemEntrega.getPacknr(), 18));

          itemEntrega.setMatnr(Utils.zeroFill(itemEntrega.getMatnr(), 18));

          itemEntrega.setMaterialPTTarima(Utils.zeroFill(itemEntrega.getMaterialPTTarima(), 18));

          itemEntrega.setPos(Utils.zeroFill(itemEntrega.getPos(), 6));

          // Crear LIPS y Zcontingencia

          itemEntrega.setTknum(Utils.zeroFill(itemEntrega.getTknum(), 10));

          resultDT = creacionEntregasRepository.creacionLipsZcontingenciaEntregaBCPS(itemEntrega, user, entrega);

          if (resultDT.getId() != 1) {

            break;
          } else {
            // Crear HUs en VEKP y VEPO

            PaletizadoraDTO paletizadora = new PaletizadoraDTO();

            /**
             * Reutiliza funcionalidad de embalar HU en entrada de mercancias
             **/

            int numeroHus = 0;

            try {

              numeroHus = new BigDecimal(itemEntrega.getQytHus().trim()).intValue();

            } catch (Exception e) {

              resultDT.setId(2);
              resultDT.setMsg(
                  "No se recibió número de hus a generar para la posición:" + itemEntrega.getPos());
              break;
            }

            for (int y = 0; y < numeroHus; y++) {

              paletizadora.setAufnr(entrega);
              paletizadora.setMaterialPTTarima(Utils.zeroFill(itemEntrega.getMatnr(), 18));

              paletizadora.setTarima(Utils.zeroFill(itemEntrega.getPacknr(), 18));

              paletizadora.setUnidadMedida(itemEntrega.getUnidadMedida());
              paletizadora.setCantidadXTarima(itemEntrega.getVemng());
              paletizadora.setLetyp(itemEntrega.getLetyp());
              paletizadora.setWerks(itemEntrega.getWerksD());
              paletizadora.setCharg("");
              paletizadora.setCharg2("");
              paletizadora.setCantidadXTarima2(itemEntrega.getVemng());
              paletizadora.setUnidadMedida2(itemEntrega.getUnidadMedida());

              resultDT = controlPaletizadoraRepository.embalarHus(paletizadora, "", user);

              switch (resultDT.getId()) {

                case 1:// Todo Ok

                  break;
                case 10:
                  resultDT.setMsg("No se encontro la orden de entrega en LIKP");
                  break;

                default:
                  resultDT.setMsg(
                      "No fue posible generar nueva hu: " + resultDT.getId() + resultDT.getMsg());
                  break;
              }
            }

          }

          if (resultDT.getId() != 1) {
            log.error("Si algo no salo bien");
            break;
          }

        }

        if (resultDT.getId() == 1) {
          // update VTTK, VTTP
          log.error("Si todo va bien");
          resultDT = creacionEntregasRepository.updateVTTP(crearEntrega.getItem().get(0).getTknum(), entrega, werks,
              user);

          if (resultDT.getId() == 1) {
            log.error("Entrega creada con exito");
            resultDT.setId(1);
            resultDT.setMsg("Entrega creada con exito: " + entrega);
          }

        }

      } else {
        // Error al generar entrega
      }

    } else {
      resultDT.setId(2);
      resultDT.setMsg("No se recibieron posiciones para la entrega");

    }

    log.error(" que trae rs:" + resultDT.getId() + " " + resultDT.getMsg());
    return resultDT;
  }

  @Override
  public CreacionEntregasDTO getEntregas() {
    log.error("getEntregas");
    CreacionEntregasDTO entregasDAO = creacionEntregasRepository.getEntregas();

    for (int x = 0; x < entregasDAO.getItems().getItem().size(); x++) {
      CrecionEntregaDTO item = entregasDAO.getItems().getItem().get(x);

      item.setLifnr(Utils.zeroClean(item.getLifnr() + ""));

      item.setWerks(Utils.zeroClean(item.getWerks()));
      item.setWerksD(Utils.zeroClean(item.getWerksD()));
      item.setLifnr(Utils.zeroClean(item.getLifnr()));

      item.setMaterialPTTarima(Utils.zeroClean(item.getMaterialPTTarima()));
      item.setMatnr(Utils.zeroClean(item.getMatnr()));
      item.setPos(Utils.zeroClean(item.getPos()));

    }

    log.error("ResultDT : " + entregasDAO.getResultDT().getId());

    return entregasDAO;

  }

  @Override
  public CreacionEntregasDTO getEntrega(String vbeln) {
    CreacionEntregasDTO entregasDAO = creacionEntregasRepository.getEntrega(vbeln);

    for (int x = 0; x < entregasDAO.getItems().getItem().size(); x++) {
      CrecionEntregaDTO item = entregasDAO.getItems().getItem().get(x);

      item.setLifnr(Utils.zeroClean(item.getLifnr() + ""));

      item.setWerks(Utils.zeroClean(item.getWerks()));
      item.setWerksD(Utils.zeroClean(item.getWerksD()));
      item.setLifnr(Utils.zeroClean(item.getLifnr()));

      item.setPacknr(Utils.zeroClean(item.getPacknr()));

      item.setMatnr(Utils.zeroClean(item.getMatnr()));
      item.setPos(Utils.zeroClean(item.getPos()));

    }

    return entregasDAO;

  }

  @Override
  public ResultDTO eliminarEntrega(CrecionEntregaDTO eliminarEntrega, String werks, String usuario) {
    ResultDTO resultDT = creacionEntregasRepository.eliminarEntrega(eliminarEntrega.getVbeln(),
        eliminarEntrega.getTknum(),
        werks, usuario);

    return resultDT;

  }
}
