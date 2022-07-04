package com.bmore.prueba.mobile.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResultDTByte {
	private String msg;
	private int id;
	private byte[] file;
	public ResultDTByte() {
		msg = null;
		id = 0;
		setFile(null);
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public byte[] getFile() {
		return file;
	}
}
