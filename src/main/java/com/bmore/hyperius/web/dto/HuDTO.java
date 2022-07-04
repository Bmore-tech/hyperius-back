package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HuDTO {

	private String hu;
	private String status;
	private String statusVEKP;	
	private String vblen;
	private String id;
	private String lgort;
	private String lgnum;
	private String lgtyp;
	private String lgpla;
	private String matnr;	
	private String lfart;
	private String werks;
	private String verme;
	private String meins;
	private String skzua;
	private String bestq;	
	
	public void setHu(String hu) {
		this.hu = hu;
	}

	public String getHu() {
		return hu;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setVblen(String vblen) {
		this.vblen = vblen;
	}

	public String getVblen() {
		return vblen;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setLgnum(String lgnum) {
		this.lgnum = lgnum;
	}

	public String getLgnum() {
		return lgnum;
	}

	public void setLgtyp(String lgtyp) {
		this.lgtyp = lgtyp;
	}

	public String getLgtyp() {
		return lgtyp;
	}

	public void setLgpla(String lgpla) {
		this.lgpla = lgpla;
	}

	public String getLgpla() {
		return lgpla;
	}

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setStatusVEKP(String statusVEKP) {
		this.statusVEKP = statusVEKP;
	}

	public String getStatusVEKP() {
		return statusVEKP;
	}

	public void setLfart(String lfart) {
		this.lfart = lfart;
	}

	public String getLfart() {
		return lfart;
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLgort() {
		return lgort;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
	}

	public void setVerme(String verme) {
		this.verme = verme;
	}

	public String getVerme() {
		return verme;
	}

	public void setMeins(String meins) {
		this.meins = meins;
	}

	public String getMeins() {
		return meins;
	}

	public void setSkzua(String skzua) {
		this.skzua = skzua;
	}

	public String getSkzua() {
		return skzua;
	}

	public void setBestq(String bestq) {
		this.bestq = bestq;
	}

	public String getBestq() {
		return bestq;
	}

}
