package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class InventarioDetalleDTOItem {

	private List<InventarioDetalleDTO> item;

	public void setItem(List<InventarioDetalleDTO> item) {
		this.item = item;
	}

	public List<InventarioDetalleDTO> getItem() {
		return item;
	}

	@Override
	public String toString() {
		return "InventarioDetalleDTOItem [item=" + item + "]";
	}

	
	
	
}
