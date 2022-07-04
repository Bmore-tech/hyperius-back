package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CrecionEntregaDTO {

	private String pos;
	private String matnr;
	private String lfimg;
	private String packnrTxt;
	private String vemng;
	private String qytHus;
	private String packnr;
	private String maktx;
	private String unidadMedida;
	private String letyp;
	private String materialPTTarima;
	private String werks;
	private String werksDesc;
	private String tknum;
	private String tknumTransport;
	private String werksD;
	private String werksDDesc;
	private String lifnr;
	private String lifnrDesc;
	private String vbeln;

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getLfimg() {
		return lfimg;
	}

	public void setLfimg(String lfimg) {
		this.lfimg = lfimg;
	}

	public String getPacknrTxt() {
		return packnrTxt;
	}

	public void setPacknrTxt(String packnrTxt) {
		this.packnrTxt = packnrTxt;
	}

	public String getVemng() {
		return vemng;
	}

	public void setVemng(String vemng) {
		this.vemng = vemng;
	}

	public String getQytHus() {
		return qytHus;
	}

	public void setQytHus(String qytHus) {
		this.qytHus = qytHus;
	}

	public String getPacknr() {
		return packnr;
	}

	public void setPacknr(String packnr) {
		this.packnr = packnr;
	}

	public String getMaktx() {
		return maktx;
	}

	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public String getLetyp() {
		return letyp;
	}

	public void setLetyp(String letyp) {
		this.letyp = letyp;
	}

	public String getMaterialPTTarima() {
		return materialPTTarima;
	}

	public void setMaterialPTTarima(String materialPTTarima) {
		this.materialPTTarima = materialPTTarima;
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerksDesc() {
		return werksDesc;
	}

	public void setWerksDesc(String werksDesc) {
		this.werksDesc = werksDesc;
	}

	public String getTknum() {
		return tknum;
	}

	public void setTknum(String tknum) {
		this.tknum = tknum;
	}

	public String getTknumTransport() {
		return tknumTransport;
	}

	public void setTknumTransport(String tknumTransport) {
		this.tknumTransport = tknumTransport;
	}

	public String getWerksD() {
		return werksD;
	}

	public void setWerksD(String werksD) {
		this.werksD = werksD;
	}

	public String getWerksDDesc() {
		return werksDDesc;
	}

	public void setWerksDDesc(String werksDDesc) {
		this.werksDDesc = werksDDesc;
	}


	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}

	public String getLifnr() {
		return lifnr;
	}

	public void setLifnrDesc(String lifnrDesc) {
		this.lifnrDesc = lifnrDesc;
	}

	public String getLifnrDesc() {
		return lifnrDesc;
	}

	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}

	public String getVbeln() {
		return vbeln;
	}

	@Override
	public String toString() {
		return "CrecionEntregaDTO [letyp=" + letyp + ", lfimg=" + lfimg
				+ ", lifnr=" + lifnr + ", lifnrDesc=" + lifnrDesc + ", maktx="
				+ maktx + ", materialPTTarima=" + materialPTTarima + ", matnr="
				+ matnr + ", packnr=" + packnr + ", packnrTxt=" + packnrTxt
				+ ", pos=" + pos + ", qytHus=" + qytHus + ", tknum=" + tknum
				+ ", tknumTransport=" + tknumTransport + ", unidadMedida="
				+ unidadMedida + ", vbeln=" + vbeln + ", vemng=" + vemng
				+ ", werks=" + werks + ", werksD=" + werksD + ", werksDDesc="
				+ werksDDesc + ", werksDesc=" + werksDesc + "]";
	}

	
	
}
