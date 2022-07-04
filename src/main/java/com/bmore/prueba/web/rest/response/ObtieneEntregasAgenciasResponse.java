package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.EntregasTransportesDTO;

public class ObtieneEntregasAgenciasResponse extends DefaultResponse {

	private EntregasTransportesDTO data;

	public EntregasTransportesDTO getData() {
		return data;
	}

	public void setData(EntregasTransportesDTO data) {
		this.data = data;
	}
}
