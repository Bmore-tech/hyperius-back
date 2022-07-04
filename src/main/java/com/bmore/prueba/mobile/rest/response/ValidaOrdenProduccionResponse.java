package com.bmore.prueba.mobile.rest.response;

import com.bmore.prueba.mobile.dto.OrdenProduccionInput;

public class ValidaOrdenProduccionResponse extends DefaultResponse {

	private OrdenProduccionInput data;

	public OrdenProduccionInput getData() {
		return data;
	}

	public void setData(OrdenProduccionInput data) {
		this.data = data;
	}
}
