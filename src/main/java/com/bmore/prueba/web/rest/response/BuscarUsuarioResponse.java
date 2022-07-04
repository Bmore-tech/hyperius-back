package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.UsuarioItemDTO;

public class BuscarUsuarioResponse extends DefaultResponse {

	private UsuarioItemDTO data;

	public UsuarioItemDTO getData() {
		return data;
	}

	public void setData(UsuarioItemDTO data) {
		this.data = data;
	}
}
