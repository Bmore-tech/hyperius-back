package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class InventarioDTO {

	private InventarioDetalleDTOItem items;
	private ResultDTO resultDT;

	
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setItems(InventarioDetalleDTOItem items) {
		this.items = items;
	}

	public InventarioDetalleDTOItem getItems() {
		return items;
	}

	
}
