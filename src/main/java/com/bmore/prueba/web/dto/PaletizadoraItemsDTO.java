package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PaletizadoraItemsDTO {

	private List<PaletizadoraDTO> item;

	public void setItem(List<PaletizadoraDTO> item) {
		this.item = item;
	}

	public List<PaletizadoraDTO> getItem() {
		return item;
	}
}
