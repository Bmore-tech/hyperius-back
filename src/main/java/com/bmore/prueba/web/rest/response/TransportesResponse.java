package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.TransportesDTO;

public class TransportesResponse extends DefaultResponse {

	private TransportesDTO data;

	public TransportesDTO getData() {
		return data;
	}

	public void setData(TransportesDTO data) {
		this.data = data;
	}
}
