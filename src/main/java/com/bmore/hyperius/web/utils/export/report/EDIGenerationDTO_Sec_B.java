package com.bmore.hyperius.web.utils.export.report;

public class EDIGenerationDTO_Sec_B {

	String name1;
	String street1;
	String city1;
	String cve_edo_ori;
	String edo_ori;
	String sort2;
	String number;
	String addr;
	String kname;
	String telf1;
	String remark;
	String city2;
	String ferr;
	String tramo_fer;
	String material;
	
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getTramo_fer() {
		return tramo_fer;
	}

	public void setTramo_fer(String tramo_fer) {
		this.tramo_fer = tramo_fer;
	}

	public String getFerr() {
		return ferr;
	}

	public void setFerr(String ferr) {
		this.ferr = ferr;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getCity1() {
		return city1;
	}

	public void setCity1(String city1) {
		this.city1 = city1;
	}

	public String getCve_edo_ori() {
		return cve_edo_ori;
	}

	public void setCve_edo_ori(String cve_edo_ori) {
		this.cve_edo_ori = cve_edo_ori;
	}

	public String getEdo_ori() {
		return edo_ori;
	}

	public void setEdo_ori(String edo_ori) {
		this.edo_ori = edo_ori;
	}

	public String getSort2() {
		return sort2;
	}

	public void setSort2(String sort2) {
		this.sort2 = sort2;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getKname() {
		return kname;
	}

	public void setKname(String kname) {
		this.kname = kname;
	}

	public String getTelf1() {
		return telf1;
	}

	public void setTelf1(String telf1) {
		this.telf1 = telf1;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCity2() {
		return city2;
	}

	public void setCity2(String city2) {
		this.city2 = city2;
	}
	
	
	@Override
	public String toString() {
		return "EDIGenerationDTO_Sec_B [name1=" + name1 + ", street1="
				+ street1 + ", city1=" + city1 + ", cve_edo_ori=" + cve_edo_ori
				+ ", edo_ori=" + edo_ori + ", sort2=" + sort2 + ", number="
				+ number + ", addr=" + addr + ", kname=" + kname + ", telf1="
				+ telf1 + ", remark=" + remark + ", city2=" + city2 + ", ferr="
				+ ferr + ", tramo_fer=" + tramo_fer + "]";
	}

	public EDIGenerationDTO_Sec_B(String name1, String street1, String city1,
			String cve_edo_ori, String edo_ori, String sort2, String number,
			String addr, String kname, String telf1, String remark,
			String city2, String ferr, String tramo_fer) {
		super();
		this.name1 = name1;
		this.street1 = street1;
		this.city1 = city1;
		this.cve_edo_ori = cve_edo_ori;
		this.edo_ori = edo_ori;
		this.sort2 = sort2;
		this.number = number;
		this.addr = addr;
		this.kname = kname;
		this.telf1 = telf1;
		this.remark = remark;
		this.city2 = city2;
		this.ferr = ferr;
		this.tramo_fer = tramo_fer;
	}

	public EDIGenerationDTO_Sec_B() {
		super();
		// TODO Auto-generated constructor stub
	}

}
