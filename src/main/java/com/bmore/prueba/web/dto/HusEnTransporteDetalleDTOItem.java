package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class HusEnTransporteDetalleDTOItem {

	private List<HUsEnTransporteDetalleDTO> item;

	public void setItem(List<HUsEnTransporteDetalleDTO> item) {
		this.item = item;
	}

	public List<HUsEnTransporteDetalleDTO> getItem() {
		return item;
	}

	
	
	
}
