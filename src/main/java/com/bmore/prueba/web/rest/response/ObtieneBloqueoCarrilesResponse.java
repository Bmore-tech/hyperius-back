package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.CarrilesBloqueadosDTO;

public class ObtieneBloqueoCarrilesResponse extends DefaultResponse {

	private CarrilesBloqueadosDTO data;

	public CarrilesBloqueadosDTO getData() {
		return data;
	}

	public void setData(CarrilesBloqueadosDTO data) {
		this.data = data;
	}
}
