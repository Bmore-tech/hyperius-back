package com.bmore.hyperius.web.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EntregaDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.old.RecepcionEnvaseRepository;
import com.bmore.hyperius.web.service.RecepcionEnvaseService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecepcionEnvaseServiceImpl implements RecepcionEnvaseService {

  @Autowired
  private RecepcionEnvaseRepository recepcionEnvaseRepository;

  @Override
  public EntregaDTO validaEntrega(EntregaDTO entrega) {
    EntregaDTO entregaReturn = new EntregaDTO();
    ResultDTO resultDT = new ResultDTO();

    // valida que entrega exista para centro
    entrega.setEntrega(Utils.zeroFill(entrega.getEntrega(), 10));
    entregaReturn = recepcionEnvaseRepository.getEntrega(entrega);
    entregaReturn.setWerks(entrega.getWerks());

    log.error("Consulta ->" + entregaReturn.toString());

    HashMap<String, String> hashMap = recepcionEnvaseRepository
        .getLgortsEntrega(Utils.zeroFill(entrega.getEntrega(), 10));

    if (hashMap.get("resultDT.id").equals("1")) {
      switch (entregaReturn.getResultDT().getId()) {
        case 1:// existe entrega
          resultDT.setId(entregaReturn.getResultDT().getId());
          resultDT.setMsg(entregaReturn.getResultDT().getMsg());

          log.error("Error entregaReturn: " + entregaReturn.getResultDT().getId());
          log.error("Error resultDT: " + resultDT.getId());

          entregaReturn.setPicking("false");
          entregaReturn.setContabilizar("false");
          entregaReturn.setContabilizada("false");
          entrega.setWerksBCPS(entregaReturn.getWerksBCPS());

          EntregaDTO entregaReturn2 = new EntregaDTO();

          // Descartar entregas salientes ya contabilizadas
          if (entrega.getLfart().equals("YD15")
              && (entrega.getEntrega().startsWith("04") || entrega.getEntrega().startsWith("4"))) {
            resultDT.setId(3);
            resultDT.setMsg("Por favor ingrese la entrega entrante correspondiente.");
          } else {
            entregaReturn2 = recepcionEnvaseRepository.getEntregaDetalle(entrega);
            log.error("Error entregaReturn2: " + entregaReturn2.getResultDT().getId());

            if (entregaReturn2.getResultDT().getId() == 1) {
              try {
                entregaReturn.setProveedorDesc(entregaReturn.getProveedorDesc().replaceAll("null", ""));
              } catch (Exception e) {
                log.error("Error: " + e.toString());
              }

              entregaReturn.setItems(entregaReturn2.getItems());
              entregaReturn.setEmbalarEntrega("false");

              // Validar LGORT para descartar IM
              HashMap<?, ?> hashMapLgortsVbelnIM = recepcionEnvaseRepository.getLgortsTabla();
              Iterator<?> iteratorVbelnIM = hashMapLgortsVbelnIM.values().iterator();

              boolean isVbelnIm = false;
              if (iteratorVbelnIM != null) {
                while (iteratorVbelnIM.hasNext()) {
                  String lgortIm = (String) iteratorVbelnIM.next();

                  if (hashMap.get(lgortIm) != null) {
                    isVbelnIm = true;
                    break;
                  }
                }
              }

              if (isVbelnIm) {
                entregaReturn.setLgort("AlmacenIM");
              } else {
                entregaReturn.setLgort("AlmacenWM");
                CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
                List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

                for (int x = 0; x < entregaReturn2.getItems().getItem().size(); x++) {
                  EntregaDetalleDTO materialItem = entregaReturn2.getItems().getItem().get(x);
                  log.error("Antes de: " + materialItem.getEmbalar());

                  if (materialItem.getEmbalar().equals("false")) {
                    if (materialItem.getHus().equals("0")) {
                      materialItem.setEmbalar("true");
                    }
                  }

                  String matnr = materialItem.getMaterial();
                  // quitar 0s
                  materialItem.setMaterial(Utils.zeroClean(materialItem.getMaterial()));
                  materialItem.setPosicion(Utils.zeroClean(materialItem.getPosicion()));

                  CarrilesUbicacionDTO carrilesDAO = new CarrilesUbicacionDTO();
                  CarrilesUbicacionDTO carrilesDAOLV02 = new CarrilesUbicacionDTO();

                  log.error(entrega.getWerks() + " " + matnr + " "
                      + Utils.zeroFill(entrega.getEntrega(), 10));

                  String idZProcesoContingenciaBCPS = "ID_01";
                  String idZPickingEntrega = "1";
                  String idLgort = "LV01";

                  if (Utils.zeroClean(matnr).startsWith("3")) {
                    idZProcesoContingenciaBCPS = "ID_03";
                    idZPickingEntrega = "3";
                  }

                  if (entrega.getWerks().equals("EMZ1") || entrega.getWerks().equals("TMZ1")) {
                    idLgort = "PT01";
                  }

                  // Obtiene carriles bloqueados por otro proceso
                  HashMap<String, String> carrilesBloqueados = recepcionEnvaseRepository
                      .getCarrilesBloqueados(idZPickingEntrega, entrega.getWerks());

                  carrilesDAO = recepcionEnvaseRepository.getCarriles(entrega.getWerks(), matnr,
                      idZProcesoContingenciaBCPS, idZPickingEntrega, idLgort,
                      Utils.zeroFill(entrega.getEntrega(), 10), carrilesBloqueados);

                  for (int y = 0; y < carrilesDAO.getItem().size(); y++) {
                    carrilesDAO.getItem().get(y).setMaterial(materialItem.getMaterial());
                    carrilesDAO.getItem().get(y).setIdCarril(
                        materialItem.getPosicion() + carrilesDAO.getItem().get(y).getLgnum()
                            + carrilesDAO.getItem().get(y).getLgtyp()
                            + carrilesDAO.getItem().get(y).getLgpla()
                            + carrilesDAO.getItem().get(y).getMe()
                            + carrilesDAO.getItem().get(y).getCajas());
                    carriles.add(carrilesDAO.getItem().get(y));
                  }

                  if (entrega.getWerks().equals("PC01")) {
                    carrilesDAOLV02 = recepcionEnvaseRepository.getCarriles(entrega.getWerks(), matnr,
                        idZProcesoContingenciaBCPS, idZPickingEntrega, "LV02",
                        Utils.zeroFill(entrega.getEntrega(), 10), carrilesBloqueados);

                    for (int y = 0; y < carrilesDAOLV02.getItem().size(); y++) {
                      carrilesDAOLV02.getItem().get(y).setMaterial(materialItem.getMaterial());
                      carrilesDAOLV02.getItem().get(y)
                          .setIdCarril(materialItem.getPosicion()
                              + carrilesDAOLV02.getItem().get(y).getLgnum()
                              + carrilesDAOLV02.getItem().get(y).getLgtyp()
                              + carrilesDAOLV02.getItem().get(y).getLgpla()
                              + carrilesDAOLV02.getItem().get(y).getMe()
                              + carrilesDAOLV02.getItem().get(y).getCajas());
                      carriles.add(carrilesDAOLV02.getItem().get(y));
                    }
                  }

                  log.error(
                      "embalar posicion: " + entregaReturn2.getItems().getItem().get(x).getEmbalar());

                  if (entregaReturn2.getItems().getItem().get(x).getEmbalar().equals("true")) {
                    entregaReturn.setEmbalarEntrega("true");
                    log.error("Se seteo a true para que sea visible la columna");
                  }
                }
                carrilesList.setItem(carriles);
                entregaReturn.setCarriles(carrilesList);
              }
            } else {
              resultDT.setId(2);
              resultDT.setMsg(entregaReturn2.getResultDT().getMsg());
            }

            // valida si se puede pickear
            int cont = 0;
            int x = 0;
            for (x = 0; x < entregaReturn.getItems().getItem().size(); x++) {
              EntregaDetalleDTO item = entregaReturn.getItems().getItem().get(x);

              try {
                if (new BigDecimal(item.getHusPendientes()).setScale(3)
                    .equals(new BigDecimal(item.getHus()).setScale(3))) {
                  if (new BigDecimal(item.getHus()).setScale(3).intValue() > 0) {
                    cont++;
                  }
                }
              } catch (Exception e) {
                log.error("Error:", e);
              }
            }

            log.error("Cont: " + cont + " X: " + x);

            if ((cont == x && x > 0) || entregaReturn.getLgort() == "AlmacenIM") {
              // totalmente pickeado no permitir hacer
              // modificaciones, si se trata de COPR BPE1 RTE1
              ResultDTO resultDT2 = new ResultDTO();
              log.error("Pickeado totalmente");
              resultDT2 = recepcionEnvaseRepository.contabilizadoOK(entrega.getEntrega());
              entregaReturn.setPicking("false");

              if (resultDT2.getId() == 1) {
                log.error("contabilizada");
                entregaReturn.setContabilizar("false");
                entregaReturn.setContabilizada("true");
                entregaReturn.setPicking("false");
              } else {
                log.error("sin contabilizar");
                entregaReturn.setContabilizar("true");
                entregaReturn.setContabilizada("false");
              }
            } else {
              log.error("Pickeado parcialmente");
              EntregaInputDTO entregaInput = new EntregaInputDTO();
              entregaInput.setEntrega(entregaReturn.getEntrega());
              entregaInput.setWerks(entregaReturn.getWerks());
              log.error("Error: entro a validar el ZPICKING:");
              EntregaInputDTO resultDT1 = recepcionEnvaseRepository.validarEntregaPickinCompleto(entregaInput);
              log.error("Error: entro a validar el ZPICKING: " + resultDT1.getResultDT().getId());

              if (resultDT1.getResultDT().getId() == 1) {
                log.error("en picking");
                entregaReturn.setPicking("true");
                entregaReturn.setContabilizar("false");
                entregaReturn.setContabilizada("false");
              } else {
                log.error("sin picking, nueva o recargada");
                entregaReturn.setPicking("false");
                entregaReturn.setContabilizar("false");
                entregaReturn.setContabilizada("false");
              }
            }

            log.error("Banderas: ");
            log.error("Picking: " + entregaReturn.getPicking());
            log.error("Contabilizaar: " + entregaReturn.getContabilizar());
            log.error("Contabilizada: " + entregaReturn.getContabilizada());
          }
          break;

        default:
          resultDT.setId(2);
          resultDT.setMsg("Entrega con error, case default en validaEntrega");
          break;
      }

    } else {
      resultDT.setId(Integer.parseInt(hashMap.get("resultDT.id").toString()));
      resultDT.setMsg(hashMap.get("resultDT.msg").toString());
    }
    entregaReturn.setResultDT(resultDT);
    log.error("Entrega: -> " + entregaReturn.toString());

    return entregaReturn;
  }

  @Override
  public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks) {
    return recepcionEnvaseRepository.ingresaDetalleEnvase(Utils.zeroFill(carriles.getItem().get(0).getEntrega(), 10),
        carriles,
        user, werks);

  }

  @Override
  public ResultDTO contabilizarEntregaEntrante(EntregaDTO entregaDTO, String user) {
    log.error(entregaDTO.toString());

    if (entregaDTO.getLgort().equals("AlmacenIM")) {
      entregaDTO.setLfart("32");
    } else if (entregaDTO.getLfart().equals("EL") || entregaDTO.getLfart().equals("YD06")) {
      entregaDTO.setLfart("4");
    } else {
      entregaDTO.setLfart("14");
    }

    return recepcionEnvaseRepository.insertProcesoContingencia_4_14_32(entregaDTO.getWerks(),
        Utils.zeroFill(entregaDTO.getEntrega(), 10), entregaDTO.getLfart(), user);

  }
}
