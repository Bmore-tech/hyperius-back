package com.bmore.hyperius.mobile.rest.request;

public class PickHURecepcionRequest {

	private Integer hu1OHu2;
	private String idRed;
	private String hu;
	private String entrega;
	private String material;
	private String dest1;
	private String dest2;
	private String werks;

	public Integer getHu1OHu2() {
		return hu1OHu2;
	}

	public void setHu1OHu2(Integer hu1oHu2) {
		hu1OHu2 = hu1oHu2;
	}

	public String getIdRed() {
		return idRed;
	}

	public void setIdRed(String idRed) {
		this.idRed = idRed;
	}

	public String getHu() {
		return hu;
	}

	public void setHu(String hu) {
		this.hu = hu;
	}

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getDest1() {
		return dest1;
	}

	public void setDest1(String dest1) {
		this.dest1 = dest1;
	}

	public String getDest2() {
		return dest2;
	}

	public void setDest2(String dest2) {
		this.dest2 = dest2;
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}
}
