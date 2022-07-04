package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;

public class ValidaOrdenProduccionResponse extends DefaultResponse {

	private OrdenProduccionDTO data;

	public OrdenProduccionDTO getData() {
		return data;
	}

	public void setData(OrdenProduccionDTO data) {
		this.data = data;
	}
}
