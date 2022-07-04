package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.HusEnTransporteDTO;

public class ObtieneHusBloqueadosResponse extends DefaultResponse {

	private HusEnTransporteDTO data;

	public HusEnTransporteDTO getData() {
		return data;
	}

	public void setData(HusEnTransporteDTO data) {
		this.data = data;
	}
}
