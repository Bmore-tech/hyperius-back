package com.bmore.hyperius.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class EntregasTransportesDetalleItemDTO {

	private List<EntregasTransportesDetalleDTO> item;

	public void setItem(List<EntregasTransportesDetalleDTO> item) {
		this.item = item;
	}

	public List<EntregasTransportesDetalleDTO> getItem() {
		return item;
	}

	
}
