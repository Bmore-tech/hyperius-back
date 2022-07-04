package com.bmore.prueba.web.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AlmacenItemDTO {

	private List<AlmacenDTO> item;

	public void setItem(List<AlmacenDTO> item) {
		this.item = item;
	}

	public List<AlmacenDTO> getItem() {
		return item;
	}
	
}
