package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class NormasEmbalajeDTO {

	private NormaEmbalajeItemsDTO items;
	private ResultDTO resultDT;

	
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setItems(NormaEmbalajeItemsDTO items) {
		this.items = items;
	}

	public NormaEmbalajeItemsDTO getItems() {
		return items;
	}

}
