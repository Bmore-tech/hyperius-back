package com.bmore.hyperius.web.dto.SAP;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import lombok.Data;

@Data
public class E_Lqua_SapEntityDTO {

	private int docInvId;
	private String lgnum;
	private String matnr;
	private String werks;
	private String lgort;
	private String lgtyp;
	private String lgpla;
	private String verme;
	private String meins;
	private String lenum;
	private Boolean marked;
	private String lgortD;
	private String maktx;

	public E_Lqua_SapEntityDTO() {
		super();
		this.marked = false;
	}

	public String E_Lqua_SapEntity_Key() {
		return this.lgnum + "" + this.lgtyp + "" + this.lgpla;
	}

	public E_Lqua_SapEntityDTO(JCoTable jcoTable) throws JCoException {
		super();
		this.lgnum = jcoTable.getString("LGNUM");
		this.matnr = jcoTable.getString("MATNR");
		this.werks = jcoTable.getString("WERKS");
		this.lgort = jcoTable.getString("LGORT");
		this.lgtyp = jcoTable.getString("LGTYP");
		this.lgpla = jcoTable.getString("LGPLA");
		this.verme = jcoTable.getString("VERME");
		this.meins = jcoTable.getString("MEINS");
		this.lenum = jcoTable.getString("LENUM");
	}

	public E_Lqua_SapEntityDTO(String lgnum, String matnr, String werks, String lgort, String lgtyp, String lgpla,
			String verme, String meins, String lenum) {
		super();
		this.lgnum = lgnum;
		this.matnr = matnr;
		this.werks = werks;
		this.lgort = lgort;
		this.lgtyp = lgtyp;
		this.lgpla = lgpla;
		this.verme = verme;
		this.meins = meins;
		this.lenum = lenum;
	}

	@Override
	public String toString() {
		return "E_Lqua_SapEntity [lgnum=" + lgnum + ", matnr=" + matnr + ", werks=" + werks + ", lgort=" + lgort
				+ ", lgtyp=" + lgtyp + ", lgpla=" + lgpla + ", verme=" + verme + ", meins=" + meins + ", lenum=" + lenum
				+ ", marked=" + marked + ", lgortD=" + lgortD + ", maktx=" + maktx + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lenum == null) ? 0 : lenum.hashCode());
		result = prime * result + ((lgnum == null) ? 0 : lgnum.hashCode());
		result = prime * result + ((lgort == null) ? 0 : lgort.hashCode());
		result = prime * result + ((lgortD == null) ? 0 : lgortD.hashCode());
		result = prime * result + ((lgpla == null) ? 0 : lgpla.hashCode());
		result = prime * result + ((lgtyp == null) ? 0 : lgtyp.hashCode());
		result = prime * result + ((maktx == null) ? 0 : maktx.hashCode());
		result = prime * result + ((marked == null) ? 0 : marked.hashCode());
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		result = prime * result + ((meins == null) ? 0 : meins.hashCode());
		result = prime * result + ((verme == null) ? 0 : verme.hashCode());
		result = prime * result + ((werks == null) ? 0 : werks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		E_Lqua_SapEntityDTO other = (E_Lqua_SapEntityDTO) obj;
		if (lenum == null) {
			if (other.lenum != null)
				return false;
		} else if (!lenum.equals(other.lenum))
			return false;
		if (lgnum == null) {
			if (other.lgnum != null)
				return false;
		} else if (!lgnum.equals(other.lgnum))
			return false;
		if (lgort == null) {
			if (other.lgort != null)
				return false;
		} else if (!lgort.equals(other.lgort))
			return false;
		if (lgortD == null) {
			if (other.lgortD != null)
				return false;
		} else if (!lgortD.equals(other.lgortD))
			return false;
		if (lgpla == null) {
			if (other.lgpla != null)
				return false;
		} else if (!lgpla.equals(other.lgpla))
			return false;
		if (lgtyp == null) {
			if (other.lgtyp != null)
				return false;
		} else if (!lgtyp.equals(other.lgtyp))
			return false;
		if (maktx == null) {
			if (other.maktx != null)
				return false;
		} else if (!maktx.equals(other.maktx))
			return false;
		if (marked == null) {
			if (other.marked != null)
				return false;
		} else if (!marked.equals(other.marked))
			return false;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		if (meins == null) {
			if (other.meins != null)
				return false;
		} else if (!meins.equals(other.meins))
			return false;
		if (verme == null) {
			if (other.verme != null)
				return false;
		} else if (!verme.equals(other.verme))
			return false;
		if (werks == null) {
			if (other.werks != null)
				return false;
		} else if (!werks.equals(other.werks))
			return false;
		return true;
	}

}
