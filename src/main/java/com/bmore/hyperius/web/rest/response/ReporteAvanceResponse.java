package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.ReporteAvanceDTO;

public class ReporteAvanceResponse extends DefaultResponse {

	private ReporteAvanceDTO data;

	public ReporteAvanceDTO getData() {
		return data;
	}

	public void setData(ReporteAvanceDTO data) {
		this.data = data;
	}
}
