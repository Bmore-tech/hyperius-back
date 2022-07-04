package com.bmore.prueba.web.rest.resquest;

import java.util.List;

import com.bmore.prueba.web.dto.HuDTO;

public class CreateEtiquetaReportRequest {

	private List<HuDTO> item;

	public List<HuDTO> getItem() {
		return item;
	}

	public void setItem(List<HuDTO> item) {
		this.item = item;
	}
}
