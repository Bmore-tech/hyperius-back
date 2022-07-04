package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UsuarioDTO {

	private String idUsuario;
	private String name;
	private String lastName;
	private String werks;
	private String descWerks;
	private String password;

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDescWerks(String descWerks) {
		this.descWerks = descWerks;
	}

	public String getDescWerks() {
		return descWerks;
	}

}
