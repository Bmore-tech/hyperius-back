package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CarrilesBloqueadosDTO {

	
	private CarrilesBloqueadosDetalleDTOItem items;
	private ResultDTO resultDT;

	
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public CarrilesBloqueadosDetalleDTOItem getItems() {
		return items;
	}

	public void setItems(CarrilesBloqueadosDetalleDTOItem items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "CarrilesBloqueadosDTO [items=" + items + ", resultDT="
				+ resultDT + "]";
	}

	public CarrilesBloqueadosDTO(CarrilesBloqueadosDetalleDTOItem items,
			ResultDTO resultDT) {
		super();
		this.items = items;
		this.resultDT = resultDT;
	}

	public CarrilesBloqueadosDTO() {
		super();
		// TODO Auto-generated constructor stub
	}


}
