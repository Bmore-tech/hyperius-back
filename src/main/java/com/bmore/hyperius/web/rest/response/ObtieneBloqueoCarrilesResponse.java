package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDTO;

public class ObtieneBloqueoCarrilesResponse extends DefaultResponse {

	private CarrilesBloqueadosDTO data;

	public CarrilesBloqueadosDTO getData() {
		return data;
	}

	public void setData(CarrilesBloqueadosDTO data) {
		this.data = data;
	}
}
