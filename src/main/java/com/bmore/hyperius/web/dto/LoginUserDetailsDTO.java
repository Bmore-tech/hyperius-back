package com.bmore.hyperius.web.dto;

import lombok.ToString;

@ToString
public class LoginUserDetailsDTO<E> {

	private ResultDTO resultdto;
	private E userDetails;

	public ResultDTO getResultdto() {
		return resultdto;
	}

	public void setResultdto(ResultDTO resultDto) {
		this.resultdto = resultDto;
	}

	public E getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(E userDetails) {
		this.userDetails = userDetails;
	}
}