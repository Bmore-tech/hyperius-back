package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class TablasSqlItemDTO {

	private List<TablaSqlDTO> item;

	public void setItem(List<TablaSqlDTO> item) {
		this.item = item;
	}

	public List<TablaSqlDTO> getItem() {
		return item;
	}

	
	
	

	
}
