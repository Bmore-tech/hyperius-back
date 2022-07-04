package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.EntregaDTO;

public class ValidarEntregaResponse extends DefaultResponse {

	private EntregaDTO data;

	public EntregaDTO getData() {
		return data;
	}

	public void setData(EntregaDTO data) {
		this.data = data;
	}
}
