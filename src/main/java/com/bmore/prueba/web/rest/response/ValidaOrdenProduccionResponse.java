package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.OrdenProduccionDTO;

public class ValidaOrdenProduccionResponse extends DefaultResponse {

	private OrdenProduccionDTO data;

	public OrdenProduccionDTO getData() {
		return data;
	}

	public void setData(OrdenProduccionDTO data) {
		this.data = data;
	}
}
