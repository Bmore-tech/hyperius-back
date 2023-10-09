package com.bmore.hyperius.web.dto.SAP;

import java.util.List;

public class ZIACMF_I360_INV_MOV_2DTO {

	private List<E_Mard_SapEntityDTO> eMard_SapEntities;
	private List<E_Msku_SapEntityDTO> eMsku_SapEntities;
	private List<E_Lqua_SapEntityDTO> eLqua_SapEntities;
	private E_Error_SapEntityDTO eError_SapEntities;

	public List<E_Mard_SapEntityDTO> geteMard_SapEntities() {
		return eMard_SapEntities;
	}

	public void seteMard_SapEntities(List<E_Mard_SapEntityDTO> eMard_SapEntities) {
		this.eMard_SapEntities = eMard_SapEntities;
	}

	public List<E_Msku_SapEntityDTO> geteMsku_SapEntities() {
		return eMsku_SapEntities;
	}

	public void seteMsku_SapEntities(List<E_Msku_SapEntityDTO> eMsku_SapEntities) {
		this.eMsku_SapEntities = eMsku_SapEntities;
	}

	public List<E_Lqua_SapEntityDTO> geteLqua_SapEntities() {
		return eLqua_SapEntities;
	}

	public void seteLqua_SapEntities(List<E_Lqua_SapEntityDTO> eLqua_SapEntities) {
		this.eLqua_SapEntities = eLqua_SapEntities;
	}

	public E_Error_SapEntityDTO geteError_SapEntities() {
		return eError_SapEntities;
	}

	public void seteError_SapEntities(E_Error_SapEntityDTO eError_SapEntities) {
		this.eError_SapEntities = eError_SapEntities;
	}

	public ZIACMF_I360_INV_MOV_2DTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ZIACMF_I360_INV_MOV_2DTO(List<E_Mard_SapEntityDTO> eMard_SapEntities, List<E_Msku_SapEntityDTO> eMsku_SapEntities,
			List<E_Lqua_SapEntityDTO> eLqua_SapEntities, E_Error_SapEntityDTO eError_SapEntities) {
		super();
		this.eMard_SapEntities = eMard_SapEntities;
		this.eMsku_SapEntities = eMsku_SapEntities;
		this.eLqua_SapEntities = eLqua_SapEntities;
		this.eError_SapEntities = eError_SapEntities;
	}

	@Override
	public String toString() {
		return "ZIACMF_I360_INV_MOV_2 [eMard_SapEntities=" + eMard_SapEntities + ", eMsku_SapEntities="
				+ eMsku_SapEntities + ", eLqua_SapEntities=" + eLqua_SapEntities + ", eError_SapEntities="
				+ eError_SapEntities + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eError_SapEntities == null) ? 0 : eError_SapEntities.hashCode());
		result = prime * result + ((eLqua_SapEntities == null) ? 0 : eLqua_SapEntities.hashCode());
		result = prime * result + ((eMard_SapEntities == null) ? 0 : eMard_SapEntities.hashCode());
		result = prime * result + ((eMsku_SapEntities == null) ? 0 : eMsku_SapEntities.hashCode());
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
		ZIACMF_I360_INV_MOV_2DTO other = (ZIACMF_I360_INV_MOV_2DTO) obj;
		if (eError_SapEntities == null) {
			if (other.eError_SapEntities != null)
				return false;
		} else if (!eError_SapEntities.equals(other.eError_SapEntities))
			return false;
		if (eLqua_SapEntities == null) {
			if (other.eLqua_SapEntities != null)
				return false;
		} else if (!eLqua_SapEntities.equals(other.eLqua_SapEntities))
			return false;
		if (eMard_SapEntities == null) {
			if (other.eMard_SapEntities != null)
				return false;
		} else if (!eMard_SapEntities.equals(other.eMard_SapEntities))
			return false;
		if (eMsku_SapEntities == null) {
			if (other.eMsku_SapEntities != null)
				return false;
		} else if (!eMsku_SapEntities.equals(other.eMsku_SapEntities))
			return false;
		return true;
	}

}
