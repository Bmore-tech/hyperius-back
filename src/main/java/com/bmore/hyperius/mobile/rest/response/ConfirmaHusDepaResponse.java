package com.bmore.hyperius.mobile.rest.response;

import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;

public class ConfirmaHusDepaResponse extends DefaultResponse {

	private OrdenProduccionInput data;

	public OrdenProduccionInput getData() {
		return data;
	}

	public void setData(OrdenProduccionInput data) {
		this.data = data;
	}
}
