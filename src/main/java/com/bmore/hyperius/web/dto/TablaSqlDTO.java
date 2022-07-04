package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TablaSqlDTO {

	private String idTablaSQL;
	private String msg;
	private String logDeleteSFTP;
	private int id;
	private String img;

	public void setIdTablaSQL(String idTablaSQL) {
		this.idTablaSQL = idTablaSQL;
	}

	public String getIdTablaSQL() {
		return idTablaSQL;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getImg() {
		return img;
	}

	public void setLogDeleteSFTP(String logDeleteSFTP) {
		this.logDeleteSFTP = logDeleteSFTP;
	}

	public String getLogDeleteSFTP() {
		return logDeleteSFTP;
	}

}