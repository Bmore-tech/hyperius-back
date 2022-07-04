package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class EntregasTransportesDTO {

	private EntregasTransportesDetalleItemDTO items;
	private ResultDTO resultDT;

	
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setItems(EntregasTransportesDetalleItemDTO items) {
		this.items = items;
	}

	public EntregasTransportesDetalleItemDTO getItems() {
		return items;
	}

}
