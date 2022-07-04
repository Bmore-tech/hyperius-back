package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.UsuarioItemDTO;

public class BuscarUsuarioResponse extends DefaultResponse {

	private UsuarioItemDTO data;

	public UsuarioItemDTO getData() {
		return data;
	}

	public void setData(UsuarioItemDTO data) {
		this.data = data;
	}
}
