package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmbarqueDetalleDTOItem {
	private List<EmbarqueDetalleDTO>item;

	public List<EmbarqueDetalleDTO> getItem() {
		return item;
	}

	public void setItem(List<EmbarqueDetalleDTO> item) {
		this.item = item;
	}

	
	
}
