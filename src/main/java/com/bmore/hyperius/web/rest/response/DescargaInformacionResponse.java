package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.DescargaInformacionDTO;

public class DescargaInformacionResponse extends DefaultResponse {

	private DescargaInformacionDTO data;

	public DescargaInformacionDTO getData() {
		return data;
	}

	public void setData(DescargaInformacionDTO data) {
		this.data = data;
	}
}
