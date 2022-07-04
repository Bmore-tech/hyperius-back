package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HuDTOItem {

	private List<HuDTO> item;

	public void setItem(List<HuDTO> item) {
		this.item = item;
	}

	public List<HuDTO> getItem() {
		return item;
	}

}
