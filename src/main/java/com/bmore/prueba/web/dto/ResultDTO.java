/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmore.prueba.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author nesquivel
 */
public class ResultDTO {

	private String msg;
	private int id;
	private String typeS;
	private int typeI;
	private boolean typeB;
	private long typeL;
	private BigDecimal typeBD;
	private float typeF;

	public ResultDTO() {

		msg = null;
		id = 0;
		typeS = null;
		typeI = 0;
		typeB = false;
		typeL = 0;
		typeBD = null;
		typeF = 0;

	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTypeS() {
		return typeS;
	}

	public void setTypeS(String typeS) {
		this.typeS = typeS;
	}

	public int getTypeI() {
		return typeI;
	}

	public void setTypeI(int typeI) {
		this.typeI = typeI;
	}

	public boolean isTypeB() {
		return typeB;
	}

	public void setTypeB(boolean typeB) {
		this.typeB = typeB;
	}

	public long getTypeL() {
		return typeL;
	}

	public void setTypeL(long typeL) {
		this.typeL = typeL;
	}

	public BigDecimal getTypeBD() {
		return typeBD;
	}

	public void setTypeBD(BigDecimal typeBD) {
		this.typeBD = typeBD;
	}

	public float getTypeF() {
		return typeF;
	}

	public void setTypeF(float typeF) {
		this.typeF = typeF;
	}

	@Override
	public String toString() {
		return "ResultDT [id=" + id + ", msg=" + msg + ", typeB=" + typeB + ", typeBD=" + typeBD + ", typeF=" + typeF
				+ ", typeI=" + typeI + ", typeL=" + typeL + ", typeS=" + typeS + "]";
	}
}
