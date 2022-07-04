package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.InventarioDTO;

public class ObtieneInventarioResponse extends DefaultResponse {

	private InventarioDTO data;

	public InventarioDTO getData() {
		return data;
	}

	public void setData(InventarioDTO data) {
		this.data = data;
	}
}
