package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.InventarioDTO;

public class ObtieneInventarioResponse extends DefaultResponse {

	private InventarioDTO data;

	public InventarioDTO getData() {
		return data;
	}

	public void setData(InventarioDTO data) {
		this.data = data;
	}
}
