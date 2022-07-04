package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.ReporteAvanceDTO;

public class ReporteAvanceResponse extends DefaultResponse {

	private ReporteAvanceDTO data;

	public ReporteAvanceDTO getData() {
		return data;
	}

	public void setData(ReporteAvanceDTO data) {
		this.data = data;
	}
}
