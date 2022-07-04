package com.bmore.hyperius.web.rest.response;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.dto.TablasSqlDTO;

public class CargaBcpsResponse extends DefaultResponse {

	private TablasSqlDTO data;

	public TablasSqlDTO getData() {
		return data;
	}

	public void setData(TablasSqlDTO data) {
		this.data = data;
	}
}
