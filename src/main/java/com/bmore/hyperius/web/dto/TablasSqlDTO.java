package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TablasSqlDTO {

	private TablasSqlItemDTO items;
	
	
	private ResultDTO resultDT;


	public void setItems(TablasSqlItemDTO items) {
		this.items = items;
	}


	public TablasSqlItemDTO getItems() {
		return items;
	}


	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}


	public ResultDTO getResultDT() {
		return resultDT;
	}

	
}
