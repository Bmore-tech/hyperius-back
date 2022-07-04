package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.CreacionEntregasDTO;

public class CreacionEntregasResponse extends DefaultResponse {

	private CreacionEntregasDTO data;

	public CreacionEntregasDTO getData() {
		return data;
	}

	public void setData(CreacionEntregasDTO data) {
		this.data = data;
	}
}
