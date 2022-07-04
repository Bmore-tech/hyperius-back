package com.bmore.prueba.web.rest.response;

import com.bmore.prueba.mobile.rest.response.DefaultResponse;
import com.bmore.prueba.web.dto.TablasSqlDTO;

public class CargaBcpsResponse extends DefaultResponse {

	private TablasSqlDTO data;

	public TablasSqlDTO getData() {
		return data;
	}

	public void setData(TablasSqlDTO data) {
		this.data = data;
	}
}
