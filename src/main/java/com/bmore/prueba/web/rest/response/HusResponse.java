package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.HusDTO;

public class HusResponse extends DefaultResponse {

	private HusDTO data;

	public HusDTO getData() {
		return data;
	}

	public void setData(HusDTO data) {
		this.data = data;
	}
}
