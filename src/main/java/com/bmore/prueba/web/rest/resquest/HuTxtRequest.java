package com.bmore.prueba.web.rest.resquest;

import java.util.List;

import com.bmore.prueba.web.dto.HuDTO;

public class HuTxtRequest {

	private List<HuDTO> item;

	public void setItem(List<HuDTO> item) {
		this.item = item;
	}

	public List<HuDTO> getItem() {
		return item;
	}
}
