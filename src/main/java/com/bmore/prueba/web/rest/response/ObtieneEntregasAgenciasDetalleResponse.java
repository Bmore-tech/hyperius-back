package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.EmbarqueDTO;

public class ObtieneEntregasAgenciasDetalleResponse extends DefaultResponse {

	private EmbarqueDTO data;

	public EmbarqueDTO getData() {
		return data;
	}

	public void setData(EmbarqueDTO data) {
		this.data = data;
	}
}
