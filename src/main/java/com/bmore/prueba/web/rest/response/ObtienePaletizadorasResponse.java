package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.PaletizadorasDTO;

public class ObtienePaletizadorasResponse extends DefaultResponse {

	private PaletizadorasDTO data;

	public PaletizadorasDTO getData() {
		return data;
	}

	public void setData(PaletizadorasDTO data) {
		this.data = data;
	}
}
