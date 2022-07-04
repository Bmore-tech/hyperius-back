package com.bmore.prueba.mobile.rest.response;

import com.bmore.prueba.mobile.dto.HusDTO;

public class ValidarHuVidrioResponse extends DefaultResponse {

	private HusDTO data;

	public HusDTO getData() {
		return data;
	}

	public void setData(HusDTO data) {
		this.data = data;
	}
}
