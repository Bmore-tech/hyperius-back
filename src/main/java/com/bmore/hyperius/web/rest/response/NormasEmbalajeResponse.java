package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.NormasEmbalajeDTO;

public class NormasEmbalajeResponse extends DefaultResponse {

	private NormasEmbalajeDTO data;

	public NormasEmbalajeDTO getData() {
		return data;
	}

	public void setData(NormasEmbalajeDTO data) {
		this.data = data;
	}
}
