package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.AlmacenesDTO;

public class LgortPermitidosResponse extends DefaultResponse {

	private AlmacenesDTO data;

	public AlmacenesDTO getData() {
		return data;
	}

	public void setData(AlmacenesDTO data) {
		this.data = data;
	}
}
