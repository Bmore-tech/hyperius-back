package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.CargaInformacionDTO;

public class CargaInformacionResponse extends DefaultResponse {

	private CargaInformacionDTO data;

	public CargaInformacionDTO getData() {
		return data;
	}

	public void setData(CargaInformacionDTO data) {
		this.data = data;
	}
}
