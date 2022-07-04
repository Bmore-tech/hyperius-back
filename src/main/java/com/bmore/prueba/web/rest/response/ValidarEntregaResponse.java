package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.EntregaDTO;

public class ValidarEntregaResponse extends DefaultResponse {

	private EntregaDTO data;

	public EntregaDTO getData() {
		return data;
	}

	public void setData(EntregaDTO data) {
		this.data = data;
	}
}
