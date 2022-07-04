package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreacionEntregasDTO {

	private CreacionEntregaItemDTO items;
	private ResultDTO resultDT;

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public CreacionEntregasDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setItems(CreacionEntregaItemDTO items) {
		this.items = items;
	}

	public CreacionEntregaItemDTO getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "CreacionEntregasDTO [items=" + items + ", resultDT=" + resultDT
				+ "]";
	}

}
