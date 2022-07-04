package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.EntregasTransportesDTO;

public class ObtieneEntregasResponse extends DefaultResponse {

	private EntregasTransportesDTO data;

	public EntregasTransportesDTO getData() {
		return data;
	}

	public void setData(EntregasTransportesDTO data) {
		this.data = data;
	}
}
