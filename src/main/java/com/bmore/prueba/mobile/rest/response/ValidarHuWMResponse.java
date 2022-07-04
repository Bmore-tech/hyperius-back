package com.bmore.prueba.mobile.rest.response;

import com.bmore.prueba.mobile.dto.HuDTO;

public class ValidarHuWMResponse extends DefaultResponse {

	private HuDTO data;

	public HuDTO getData() {
		return data;
	}

	public void setData(HuDTO data) {
		this.data = data;
	}
}
