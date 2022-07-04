package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.PaletizadorasDTO;

public class ObtienePaletizadorasResponse extends DefaultResponse {

	private PaletizadorasDTO data;

	public PaletizadorasDTO getData() {
		return data;
	}

	public void setData(PaletizadorasDTO data) {
		this.data = data;
	}
}
