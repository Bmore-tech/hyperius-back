package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.HusEnTransporteDTO;

public class ObtieneHusBloqueadosResponse extends DefaultResponse {

	private HusEnTransporteDTO data;

	public HusEnTransporteDTO getData() {
		return data;
	}

	public void setData(HusEnTransporteDTO data) {
		this.data = data;
	}
}
