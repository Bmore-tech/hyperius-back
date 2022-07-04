package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.DescargaInformacionDTO;

public class DescargaInformacionResponse extends DefaultResponse {

	private DescargaInformacionDTO data;

	public DescargaInformacionDTO getData() {
		return data;
	}

	public void setData(DescargaInformacionDTO data) {
		this.data = data;
	}
}
