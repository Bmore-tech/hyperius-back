package com.bmore.hyperius.mobile.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.mobile.dto.HuDTO;
import com.bmore.hyperius.mobile.dto.HusDTO;
import com.bmore.hyperius.mobile.repository.impl.HUsDAO;
import com.bmore.hyperius.mobile.utils.Utils;

public class ValidarHUBO {
	private static final Logger LOCATION = LoggerFactory.getLogger(ValidarHUBO.class);	
	public static HuDTO validarHU(String hu) throws ClassNotFoundException{
		HuDTO huDTO = new HuDTO();
		HUsDAO husDAO= new HUsDAO();
		huDTO = husDAO.validarHU(hu);
		if (huDTO.getResultDT().getId() == 1) {
			if (huDTO.getBestq() == null) {
				huDTO.setBestq("Libre utilización");
			} else if (huDTO.getBestq().equals("Q")) {
				huDTO.setBestq("En calidad");
			} else if (huDTO.getBestq().equals("S")) {
				huDTO.setBestq("Bloqueado");
			}
			if (huDTO.getSkzua() == null) {
				huDTO.setSkzua("Si");
			} else if (huDTO.getSkzua().equals("X")) {
				huDTO.setSkzua("No, HU previamente consumido");
			}			
			if (huDTO.getBestq().equals("En calidad")){
				huDTO.setSkzua("No, HU En Calidad");
			}			
			if (huDTO.getBestq().equals("Bloqueado")){
				huDTO.setSkzua("No, HU Bloqueado");
			}
			try {
				huDTO.setMatnr(Utils.zeroClean(huDTO.getMatnr()));
			} catch (Exception e) {
				LOCATION.error(e.toString());
			}
		}
		return huDTO;
	}	
	public static HusDTO validarHUVidrio(String exidv, String werks) throws ClassNotFoundException{
		HusDTO husDTO = new HusDTO();
		HUsDAO husDAO = new HUsDAO();
		husDTO = husDAO.validarHUVidrio(exidv, werks);
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
			}else if(husDTO.getItems().getItem().get(0).getSkzua().equals("X")){
				husDTO.getItems().getItem().get(0).setSkzua("Si, HU disponible");
			}else if(husDTO.getItems().getItem().get(0).getSkzua().equals("A")){
				husDTO.getItems().getItem().get(0).setSkzua("No, HU no disponible para consumo");
			}			
			if (husDTO.getItems().getItem().get(0).getBestq().equals("Bloqueado")){
				husDTO.getItems().getItem().get(0).setSkzua("No, HU Bloqueado");
			}
			try {
				husDTO.getItems().getItem().get(0).setMatnr(Utils.zeroClean(husDTO.getItems().getItem().get(0).getMatnr()));
			} catch (Exception e) {
				LOCATION.error(e.toString());
			}
		}
		return husDTO;
	}
}