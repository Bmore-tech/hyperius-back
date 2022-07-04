package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.TransportesDTO;

public class TransportesResponse extends DefaultResponse {

	private TransportesDTO data;

	public TransportesDTO getData() {
		return data;
	}

	public void setData(TransportesDTO data) {
		this.data = data;
	}
}
