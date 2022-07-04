package com.bmore.hyperius.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntregaDetalleDTOItem {
	
	private List<EntregaDetalleDTO> item;

	public void setItem(List<EntregaDetalleDTO> item) {
		this.item = item;
	}

	public List<EntregaDetalleDTO> getItem() {
		return item;
	}
	
}