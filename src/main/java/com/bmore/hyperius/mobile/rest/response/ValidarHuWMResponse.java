package com.bmore.hyperius.mobile.rest.response;

import com.bmore.hyperius.mobile.dto.HuDTO;

public class ValidarHuWMResponse extends DefaultResponse {

	private HuDTO data;

	public HuDTO getData() {
		return data;
	}

	public void setData(HuDTO data) {
		this.data = data;
	}
}
