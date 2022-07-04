package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class UsuariosDTO {

	private List<UsuarioDTO> item;

	public void setItem(List<UsuarioDTO> item) {
		this.item = item;
	}

	public List<UsuarioDTO> getItem() {
		return item;
	}

}
