package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HusDTO {

	private HuDTOItem items;
	private ResultDTO resultDT;

	public void setItems(HuDTOItem items) {
		this.items = items;
	}

	public HuDTOItem getItems() {
		return items;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

}
