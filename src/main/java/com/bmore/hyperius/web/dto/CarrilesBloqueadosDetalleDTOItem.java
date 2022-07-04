package com.bmore.hyperius.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CarrilesBloqueadosDetalleDTOItem {

	private List<CarrilesBloqueadosDetalleDTO> item;

	public List<CarrilesBloqueadosDetalleDTO> getItem() {
		return item;
	}

	public void setItem(List<CarrilesBloqueadosDetalleDTO> item) {
		this.item = item;
	}
	
	
}
