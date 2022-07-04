package com.bmore.hyperius.mobile.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.bmore.hyperius.mobile.utils.ResultDT;

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
