package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class NormaEmbalajeItemsDTO {

	private List<NormaEmbalajeDTO> item;

	public void setItem(List<NormaEmbalajeDTO> item) {
		this.item = item;
	}

	public List<NormaEmbalajeDTO> getItem() {
		return item;
	}
	
}
