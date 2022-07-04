package com.bmore.hyperius.mobile.rest.response;

import com.bmore.hyperius.mobile.dto.EntregaInput;

public class ValidaEntregaEmbarqueResponse extends DefaultResponse {

	private EntregaInput data;

	public EntregaInput getData() {
		return data;
	}

	public void setData(EntregaInput data) {
		this.data = data;
	}
}
