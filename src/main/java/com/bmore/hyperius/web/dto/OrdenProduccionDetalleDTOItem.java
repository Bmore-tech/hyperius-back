package com.bmore.hyperius.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrdenProduccionDetalleDTOItem {

	private List<OrdenProduccionDetalleDTO> item;

	public List<OrdenProduccionDetalleDTO> getItem() {
		return item;
	}

	public void setItem(List<OrdenProduccionDetalleDTO> item) {
		this.item = item;
	}
	
	
	
}
