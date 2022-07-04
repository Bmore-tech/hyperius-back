package com.bmore.prueba.mobile.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.bmore.prueba.mobile.utils.ResultDT;

@XmlRootElement
public class HusDTO {

	private HuDTOItem items;
	private ResultDT resultDT;

	public void setItems(HuDTOItem items) {
		this.items = items;
	}

	public HuDTOItem getItems() {
		return items;
	}

	public void setResultDT(ResultDT resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDT getResultDT() {
		return resultDT;
	}

}
