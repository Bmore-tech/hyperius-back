package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.NormasEmbalajeDTO;

public class NormasEmbalajeResponse extends DefaultResponse {

	private NormasEmbalajeDTO data;

	public NormasEmbalajeDTO getData() {
		return data;
	}

	public void setData(NormasEmbalajeDTO data) {
		this.data = data;
	}
}
