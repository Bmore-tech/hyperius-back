package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.CreacionEntregasDTO;

public class CreacionEntregasResponse extends DefaultResponse {

	private CreacionEntregasDTO data;

	public CreacionEntregasDTO getData() {
		return data;
	}

	public void setData(CreacionEntregasDTO data) {
		this.data = data;
	}
}
