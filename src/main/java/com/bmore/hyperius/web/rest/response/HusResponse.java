package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.HusDTO;

public class HusResponse extends DefaultResponse {

	private HusDTO data;

	public HusDTO getData() {
		return data;
	}

	public void setData(HusDTO data) {
		this.data = data;
	}
}
