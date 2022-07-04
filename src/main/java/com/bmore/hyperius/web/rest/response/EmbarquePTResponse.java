package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.EmbarqueDTO;

public class EmbarquePTResponse extends DefaultResponse {

	private EmbarqueDTO data;

	public EmbarqueDTO getData() {
		return data;
	}

	public void setData(EmbarqueDTO data) {
		this.data = data;
	}
}
