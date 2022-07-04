package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.AlmacenesDTO;

public class LgortPermitidosResponse extends DefaultResponse {

	private AlmacenesDTO data;

	public AlmacenesDTO getData() {
		return data;
	}

	public void setData(AlmacenesDTO data) {
		this.data = data;
	}
}
