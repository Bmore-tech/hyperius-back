package com.bmore.hyperius.web.rest.resquest;

import java.util.List;

import com.bmore.hyperius.web.dto.HuDTO;

public class CreateEtiquetaReportRequest {

	private List<HuDTO> item;

	public List<HuDTO> getItem() {
		return item;
	}

	public void setItem(List<HuDTO> item) {
		this.item = item;
	}
}
