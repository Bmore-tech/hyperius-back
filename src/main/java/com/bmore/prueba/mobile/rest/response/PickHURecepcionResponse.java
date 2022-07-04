package com.bmore.prueba.mobile.rest.response;

import com.bmore.prueba.mobile.dto.EntregaInput;

public class PickHURecepcionResponse extends DefaultResponse {

	private EntregaInput data;

	public EntregaInput getData() {
		return data;
	}

	public void setData(EntregaInput data) {
		this.data = data;
	}
	
	
}
