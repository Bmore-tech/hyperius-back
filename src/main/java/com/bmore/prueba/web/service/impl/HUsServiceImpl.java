package com.bmore.prueba.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.HuDTO;
import com.bmore.prueba.web.dto.HusDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.HUsRepository;
import com.bmore.prueba.web.repository.old.HUsRepositoryOld;
import com.bmore.prueba.web.service.HUsService;
import com.bmore.prueba.web.utils.Utils;

@Service
public class HUsServiceImpl implements HUsService {

	private static final Logger LOCATION = LoggerFactory.getLogger(HUsServiceImpl.class);

	@Autowired
	private HUsRepository hUsRepository;

	@Override
	public HusDTO obtieneHus(HuDTO huDTO) {

		HusDTO husDTO = new HusDTO();

		ResultDTO resultDT = new ResultDTO();
		husDTO.setResultDT(resultDT);

		if (huDTO.getId().equals("1") || huDTO.getId().equals("4") || huDTO.getId().equals("6")) {

			huDTO.setVblen(Utils.zeroFill(huDTO.getVblen(), 10));

		} else if (huDTO.getId().equals("2") || huDTO.getId().equals("3") || huDTO.getId().equals("5")) {

			huDTO.setVblen(Utils.zeroFill(huDTO.getVblen(), 12));

		}

		if (huDTO.getId().equals("1") || huDTO.getId().equals("3") || huDTO.getId().equals("5")) {
			LOCATION.error("Entre a 135");

			if (huDTO.getId().equals("1")) {

				if (huDTO.getLfart().equals("EL") || huDTO.getLfart().equals("YD15") || huDTO.getLfart().equals("YD06"))
					husDTO = hUsRepository.obtieneHusVEKP(huDTO);
				else if (huDTO.getLfart().equals("Y015"))
					husDTO = hUsRepository.obtieneHusBCPS(huDTO);

			} else
				husDTO = hUsRepository.obtieneHusVEKP(huDTO);

		} else if (huDTO.getId().equals("2") || huDTO.getId().equals("4") || huDTO.getId().equals("6")) {

			if (huDTO.getId().equals("2") || huDTO.getId().equals("4"))
				husDTO = hUsRepository.obtieneHusZPicking(huDTO);
			else {
				huDTO.setId("4");
				husDTO = hUsRepository.obtieneHusZPickingVidrio(huDTO);
			}

		} else if (huDTO.getId().equals("900")) {
			LOCATION.error("Entre a 900");
			huDTO.setMatnr(Utils.zeroFill(huDTO.getMatnr(), 18));
			husDTO = hUsRepository.obtieneHusCarrilPorMaterial(huDTO);

		}

		if (husDTO.getResultDT().getId() == 1) {

			for (int x = 0; x < husDTO.getItems().getItem().size(); x++) {

				HuDTO item = husDTO.getItems().getItem().get(x);

				if (huDTO.getId().equals("1") || huDTO.getId().equals("3") || huDTO.getId().equals("5")) {

					LOCATION.error("id: " + huDTO.getId() + " " + item.getStatus() + " vekp:" + item.getStatusVEKP());

					if (((huDTO.getId().equals("1") || huDTO.getId().equals("3")) && item.getStatus() == null)
							|| (huDTO.getId().equals("5") && item.getStatusVEKP() == null)) {

						item.setStatus("------Pendiente");
					} else {

						item.setStatus("Pickeado");
					}
				} else if (huDTO.getId().equals("2") || huDTO.getId().equals("4")) {

					if (item.getStatus() == null || !item.getStatus().equals("2")) {

						item.setStatus("------Pendiente");
					} else {

						item.setStatus("Pickeado");
					}
				}
			}

		}

		return husDTO;
	}
}
