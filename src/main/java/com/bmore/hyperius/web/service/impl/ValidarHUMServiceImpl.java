package com.bmore.hyperius.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.HusDTO;
import com.bmore.hyperius.web.repository.HUsRepository;
import com.bmore.hyperius.web.service.ValidarHUMService;
import com.bmore.hyperius.web.utils.Utils;

@Service
public class ValidarHUMServiceImpl implements ValidarHUMService {

  // private final Logger LOCATION = LoggerFactory.getLogger(getClass());

  @Autowired
  private HUsRepository hUsRepository;

  @Override
  public HusDTO validarHU(String hu, String werks) {

    HusDTO husDTO = new HusDTO();

    husDTO = hUsRepository.validarHU(hu, werks);

    if (husDTO.getResultDT().getId() == 1) {

      if (husDTO.getItems().getItem().get(0).getBestq() == null) {
        husDTO.getItems().getItem().get(0).setBestq("Libre utilización");
      } else if (husDTO.getItems().getItem().get(0).getBestq().equals("Q")) {
        husDTO.getItems().getItem().get(0).setBestq("En calidad");
      } else if (husDTO.getItems().getItem().get(0).getBestq().equals("S")) {
        husDTO.getItems().getItem().get(0).setBestq("Bloqueado");
      }

      if (husDTO.getItems().getItem().get(0).getSkzua() == null) {
        husDTO.getItems().getItem().get(0).setSkzua("Si");
      } else if (husDTO.getItems().getItem().get(0).getSkzua().equals("X")) {
        husDTO.getItems().getItem().get(0).setSkzua("No, HU previamente consumido");
      }

      if (husDTO.getItems().getItem().get(0).getBestq().equals("Bloqueado")) {
        husDTO.getItems().getItem().get(0).setSkzua("No, HU Bloqueado");
      }

      try {
        husDTO.getItems().getItem().get(0)
            .setMatnr(Utils.zeroClean(husDTO.getItems().getItem().get(0).getMatnr()));
      } catch (Exception e) {

      }
    }

    return husDTO;
  }

  @Override
  public HusDTO validarHUVidrio(String exidv, String werks) {

    HusDTO husDTO = new HusDTO();

    husDTO = hUsRepository.validarHUVidrio(exidv, werks);

    if (husDTO.getResultDT().getId() == 1) {

      if (husDTO.getItems().getItem().get(0).getBestq() == null) {
        husDTO.getItems().getItem().get(0).setBestq("Libre utilización");
      } else if (husDTO.getItems().getItem().get(0).getBestq().equals("Q")) {
        husDTO.getItems().getItem().get(0).setBestq("En calidad");
      } else if (husDTO.getItems().getItem().get(0).getBestq().equals("S")) {
        husDTO.getItems().getItem().get(0).setBestq("Bloqueado");
      }

      if (husDTO.getItems().getItem().get(0).getSkzua().equals("Z")) {
        husDTO.getItems().getItem().get(0).setSkzua("No, HU previamente consumido");
      } else if (husDTO.getItems().getItem().get(0).getSkzua().equals("X")) {
        husDTO.getItems().getItem().get(0).setSkzua("Si, HU disponible");
      } else if (husDTO.getItems().getItem().get(0).getSkzua().equals("A")) {
        husDTO.getItems().getItem().get(0).setSkzua("No, HU no disponible para consumo");
      }

      if (husDTO.getItems().getItem().get(0).getBestq().equals("Bloqueado")) {
        husDTO.getItems().getItem().get(0).setSkzua("No, HU Bloqueado");
      }

      try {
        husDTO.getItems().getItem().get(0)
            .setMatnr(Utils.zeroClean(husDTO.getItems().getItem().get(0).getMatnr()));
      } catch (Exception e) {

      }
    }

    return husDTO;
  }
}
