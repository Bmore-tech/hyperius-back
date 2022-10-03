package com.bmore.hyperius.web.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.EntregaDTO;
import com.bmore.hyperius.web.dto.NormaEmbalajeDTO;
import com.bmore.hyperius.web.dto.NormaEmbalajeItemsDTO;
import com.bmore.hyperius.web.dto.NormasEmbalajeDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.PaletizadoraDTO;
import com.bmore.hyperius.web.dto.PaletizadorasDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.ControlPaletizadoraRepository;
import com.bmore.hyperius.web.repository.old.RecepcionEnvaseRepository;
import com.bmore.hyperius.web.repository.old.UbicacionPTRepository;
import com.bmore.hyperius.web.service.ControlPaletizadoraService;
import com.bmore.hyperius.web.utils.Utils;

@Service
public class ControlPaletizadoraServiceImpl implements ControlPaletizadoraService {

  private final Logger LOCATION = LoggerFactory.getLogger(getClass());

  @Autowired
  private ControlPaletizadoraRepository controlPaletizadoraRepository;

  @Autowired
  private RecepcionEnvaseRepository recepcionEnvaseRepository;

  @Autowired
  private UbicacionPTRepository ubicacionPTRepository;

  // private ControlPaletizadoraRepositoryOld controlPaletizadoraDAO = new
  // ControlPaletizadoraRepositoryOld();

  @Override
  public PaletizadorasDTO obtienePaletizadoras(String werks) {

    PaletizadorasDTO paletizadorasDTO = new PaletizadorasDTO();
    paletizadorasDTO = controlPaletizadoraRepository.obtienePaletizadoras(werks);

    if (paletizadorasDTO.getResultDT().getId() == 1) {

      for (int x = 0; x < paletizadorasDTO.getPaletizadoras().getItem().size(); x++) {

        PaletizadoraDTO item = paletizadorasDTO.getPaletizadoras().getItem().get(x);

        item.setAufnr(Utils.zeroClean(item.getAufnr()));
        item.setTarima(Utils.zeroClean(item.getTarima()));

      }

    }

    return paletizadorasDTO;

  }

  @Override
  public ResultDTO actualizaOrdenEnPaletizadora(PaletizadoraDTO paletizadora) {
    ResultDTO result = new ResultDTO();
    OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();

    paletizadora.setAufnr(Utils.zeroFill(paletizadora.getAufnr(), 12));

    ordenProduccionDTO.setOrdenProduccion(paletizadora.getAufnr());
    ordenProduccionDTO.setWerks(paletizadora.getWerks());

    ordenProduccionDTO = ubicacionPTRepository.getOrden(ordenProduccionDTO);

    result = ordenProduccionDTO.getResultDT();

    if (result.getId() == 1) {
      controlPaletizadoraRepository.guardaPaletizadora(paletizadora);
    }

    return result;
  }

  @Override
  public ResultDTO obtieneCantidadHUS(String aufnr) {

    ResultDTO result = new ResultDTO();

    result = controlPaletizadoraRepository.obtieneCantidadHUS(Utils.zeroFill(aufnr, 12));

    return result;

  }

  @Override
  public ResultDTO marcarHusParaImprimir(PaletizadoraDTO paletizadora) {

    ResultDTO result = new ResultDTO();

    // ControlPaletizadoraDAO controlPaletizadoraDAO = new
    // ControlPaletizadoraDAO();
    paletizadora.setAufnr(Utils.zeroFill(paletizadora.getAufnr(), 12));

    // if (paletizadora.getWerks().indexOf("PC") >= 0) {

    result = generaHusBCPS(paletizadora);

    // } else {
    //
    // result = controlPaletizadoraDAO.marcarHusParaImprimir(paletizadora);
    // }

    return result;

  }

  @Override
  public ResultDTO generaHusBCPS(PaletizadoraDTO paletizadora) {
    // Hus generadas por el legado

    ResultDTO result = new ResultDTO();

    paletizadora.setAufnr(Utils.zeroFill(paletizadora.getAufnr(), 12));

    String keyTimeStamp = Utils.getKeyTimeStamp();

    BigDecimal bigDecimal = new BigDecimal(paletizadora.getCantidadEtiqueasAImprimir());

    int generarHus = Integer.parseInt(bigDecimal.toBigInteger() + "");

    LOCATION.error("Numero generar hus: " + generarHus);
    int contHus = 0;
    for (int x = 0; x < generarHus; x++) {

      result = controlPaletizadoraRepository.generaHusBCPS(paletizadora, keyTimeStamp);

      switch (result.getId()) {

        case 1:// Todo Ok

          break;
        case 10:
          result.setMsg("No se encontro la orden de producción en la ZPAITT_PALLETOBR");
          break;

        default:
          result.setMsg("No fue posible generar nueva hu: " + result.getId() + result.getMsg());
          break;

      }

      if (result.getId() != 1) {
        contHus++;
        result.setMsg("Se crearon " + contHus + " hus ->" + result.getMsg());

        break;
      } else {
        result.setMsg(paletizadora.getAufnr());
      }
    }

    result.setTypeS(keyTimeStamp);
    return result;

  }

  @Override
  public NormasEmbalajeDTO obtieneNormasEmbalaje(String aufnr, String werks, String unidadMedida, String cantidad,
      int opc, String material) {

    NormasEmbalajeDTO normasEmbalajeDTO = new NormasEmbalajeDTO();
    NormaEmbalajeItemsDTO normaEmbalajeItemsDTO = new NormaEmbalajeItemsDTO();
    List<NormaEmbalajeDTO> listNormaEmbalajeDTO = new ArrayList<NormaEmbalajeDTO>();

    normaEmbalajeItemsDTO.setItem(listNormaEmbalajeDTO);
    normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);

    ResultDTO resultDT = new ResultDTO();
    OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();

    EntregaDTO entregaDTO = new EntregaDTO();
    boolean equivalenciasOk = true;

    LOCATION.error("Doc: " + Utils.zeroFill(aufnr, 12) + " werks: " + werks);

    int listSize = 0;

    switch (opc) {

      case 1:

        ordenProduccionDTO = ubicacionPTRepository.detalleOrdenProduccionSoloCabecera(Utils.zeroFill(aufnr, 12), werks);

        listSize = ordenProduccionDTO.getItems().getItem().size();

        resultDT = ordenProduccionDTO.getResultDT();

        break;

      case 2:

        entregaDTO = recepcionEnvaseRepository.getEntregaDetalleSoloCabecera(Utils.zeroFill(aufnr, 10));

        resultDT = entregaDTO.getResultDT();

        listSize = entregaDTO.getItems().getItem().size();

        break;

      case 3:// Mostrar normas embalaje para creacion entregas cscLogistica
        listSize = 1;
        resultDT.setId(1);

        break;
      default:
        LOCATION.error("No se envió la OPC para recuperar datos del documento");
        return null;

    }

    if (resultDT.getId() == 1) {

      for (int x = 0; x < listSize; x++) {

        OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

        switch (opc) {

          case 1:
            item = ordenProduccionDTO.getItems().getItem().get(x);
            break;

          case 2:
            item.setMaterial(entregaDTO.getItems().getItem().get(x).getMaterial());
            item.setDescripcion(entregaDTO.getItems().getItem().get(x).getDescripcion());

            break;
          case 3:
            item.setMaterial(Utils.zeroFill(material, 18));

            break;
          default:
            LOCATION.error("No se envió la OPC para recuperar datos del documento");
            return null;
        }

        // // Obtener letys por material

        NormasEmbalajeDTO tarimas = controlPaletizadoraRepository.obtieneTarimas(item.getMaterial());

        if (tarimas.getResultDT().getId() == 1) {

          for (int y = 0; y < tarimas.getItems().getItem().size(); y++) {

            NormaEmbalajeDTO tarima = tarimas.getItems().getItem().get(y);

            tarima.setMatnr(Utils.zeroClean(item.getMaterial()));

            tarima.setMaktx(item.getDescripcion());

            tarima.setTarima(Utils.zeroClean(tarima.getTarima()));

            tarima.setCantidad2(tarima.getCantidad());
            tarima.setUnidadMedida2(tarima.getUnidadMedida());

            if (opc == 2 && !unidadMedida.trim().equalsIgnoreCase(tarima.getUnidadMedida().trim())) {

              /**
               * Se estan mostrando las normas de embalaje para embalar entregas y es
               * necesario realizar la conversion entre las unidades de medida
               */

              NormasEmbalajeDTO equivalencias = controlPaletizadoraRepository
                  .obtieneEquivalenciasUM(item.getMaterial(), unidadMedida);

              if (equivalencias.getResultDT().getId() == 1) {

                LOCATION.error("ResultDT Entro.....");

                /**
                 * Solo existe una equivalencia en el sistema
                 */

                NormaEmbalajeDTO equivalencia = equivalencias.getItems().getItem().get(0);

                BigDecimal cantidadBD = new BigDecimal(0);
                BigDecimal umrez = new BigDecimal(0);
                BigDecimal umren = new BigDecimal(0);
                BigDecimal res = new BigDecimal(0);
                LOCATION.error("ResultDT Entro 213.....");
                try {

                  LOCATION.error("cantidadDB " + cantidad.trim());
                  cantidadBD = new BigDecimal(cantidad.trim());

                  umrez = new BigDecimal(equivalencia.getUmrez().trim());
                  LOCATION.error("umrez " + equivalencia.getUmrez().trim());
                  umren = new BigDecimal(equivalencia.getUmren().trim());

                  LOCATION.error("umren " + equivalencia.getUmren().trim());
                  res = new BigDecimal(0);

                  LOCATION.error("res1 " + res.toString());

                  res = cantidadBD.multiply(umrez).divide(umren);

                  LOCATION.error("res2 " + res);

                  LOCATION.error("tarimaGetCatidad" + tarima.getCantidad());

                  res = res.divide(new BigDecimal(tarima.getCantidad().trim()), RoundingMode.HALF_UP)
                      .setScale(3);

                  LOCATION.error("res3 " + res);

                  res = cantidadBD.divide(res, RoundingMode.HALF_UP).setScale(3);

                  LOCATION.error("res4 " + res);

                  tarima.setCantidad(res.toString());
                  tarima.setUnidadMedida(unidadMedida);

                } catch (Exception e) {
                  resultDT.setId(2);
                  resultDT.setMsg("Error al realizar la conversión a BigDecimal");
                  LOCATION.error("Error: " + e.toString());
                  equivalenciasOk = false;

                }

              } else {

                resultDT = equivalencias.getResultDT();

                equivalenciasOk = false;
                break;
              }

            }

            normaEmbalajeItemsDTO.getItem().add(tarima);

          }

        }

        normaEmbalajeItemsDTO.setItem(tarimas.getItems().getItem());
        normasEmbalajeDTO.setItems(normaEmbalajeItemsDTO);
      }
    }

    if (normasEmbalajeDTO.getItems().getItem().size() > 0 && equivalenciasOk) {
      resultDT.setId(1);
      resultDT.setMsg("Normas de embalaje recuperadas con exito");

    }

    normasEmbalajeDTO.setResultDT(resultDT);

    return normasEmbalajeDTO;

  }

  @Override
  public ResultDTO cambiarNormaEmbalaje(PaletizadoraDTO paletizadoraDTO) {

    ResultDTO resultDT = new ResultDTO();

    paletizadoraDTO.setAufnr(Utils.zeroFill(paletizadoraDTO.getAufnr(), 12));

    paletizadoraDTO.setTarima(Utils.zeroFill(paletizadoraDTO.getTarima(), 18));

    // if (paletizadoraDTO.getWerks().toUpperCase().indexOf("PC") >= 0) {//
    // Cerveza

    paletizadoraDTO.setMaterialPTTarima(Utils.zeroFill(paletizadoraDTO.getMaterialPTTarima(), 18));

    resultDT = controlPaletizadoraRepository.cambiarNormaEmbalajeBCPS(paletizadoraDTO);

    if (resultDT.getId() == 1)
      resultDT.setMsg("Orden actualizada con exito");

    //
    // } else {// envases y tapas
    //
    // paletizadoraDTO.setMaterialPTTarima(Utils.zeroFill(paletizadoraDTO
    // .getMaterialPTTarima(), 18));
    //
    // resultDT = controlPaletizadoraDAO
    // .cambiarNormaEmbalajeBCPS(paletizadoraDTO);
    //
    // if (resultDT.getId() == 1) {
    //
    // resultDT = controlPaletizadoraDAO
    // .cambiarNormaEmbalaje(paletizadoraDTO);
    //
    // }

    // }

    return resultDT;

  }

  @Override
  public ResultDTO embalarHus(PaletizadoraDTO paletizadora, String userId) {

    // Hus generadas por el legado

    ResultDTO result = new ResultDTO();

    paletizadora.setAufnr(Utils.zeroFill(paletizadora.getAufnr(), 10));

    String keyTimeStamp = Utils.getKeyTimeStamp();

    BigDecimal bigDecimal = new BigDecimal(paletizadora.getCantidadEtiqueasAImprimir());

    int generarHus = Integer.parseInt(bigDecimal.toBigInteger() + "");

    LOCATION.error("Numero generar hus: " + generarHus);

    int contHus = 0;
    for (int x = 0; x < generarHus; x++) {

      paletizadora.setAufnr(Utils.zeroFill(paletizadora.getAufnr(), 10));

      paletizadora.setMaterialPTTarima(Utils.zeroFill(paletizadora.getMaterialPTTarima(), 18));
      paletizadora.setTarima(Utils.zeroFill(paletizadora.getTarima(), 18));

      result = controlPaletizadoraRepository.embalarHus(paletizadora, keyTimeStamp, userId);

      switch (result.getId()) {

        case 1:// Todo Ok

          break;
        case 10:
          result.setMsg("No se encontro la orden de entrega en LIKP");
          break;

        default:
          result.setMsg("No fue posible generar nueva hu: " + result.getId() + result.getMsg());
          break;

      }

      if (result.getId() != 1) {
        contHus++;
        result.setMsg("Se crearon " + contHus + " hus ->" + result.getMsg());

        break;
      } else {
        result.setMsg(paletizadora.getAufnr());
      }
    }

    result.setTypeS(keyTimeStamp);
    return result;

  }
}
