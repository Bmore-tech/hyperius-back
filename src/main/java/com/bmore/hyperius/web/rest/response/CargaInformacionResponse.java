package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.CargaInformacionDTO;

public class CargaInformacionResponse extends DefaultResponse {

	private CargaInformacionDTO data;

	public CargaInformacionDTO getData() {
		return data;
	}

	public void setData(CargaInformacionDTO data) {
		this.data = data;
	}
}
