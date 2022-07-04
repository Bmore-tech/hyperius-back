package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HusEnTransporteDTO {

	private HusEnTransporteDetalleDTOItem items;
	private ResultDTO resultDT;

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setItems(HusEnTransporteDetalleDTOItem items) {
		this.items = items;
	}

	public HusEnTransporteDetalleDTOItem getItems() {
		return items;
	}

}
