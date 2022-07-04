package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AlmacenesDTO {

	private AlmacenItemDTO items;

	private ResultDTO resultDT;

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setItems(AlmacenItemDTO items) {
		this.items = items;
	}

	public AlmacenItemDTO getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "AlmacenesDTO [items=" + items + ", resultDT=" + resultDT + "]";
	}

}
