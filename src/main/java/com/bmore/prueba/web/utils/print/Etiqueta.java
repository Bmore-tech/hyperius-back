package com.bmore.prueba.web.utils.print;

public class Etiqueta {

    /**
     * @param args the command line arguments
     */
    private String EXIDV_HU;
    private String ZHUEX_HU;
    private String fechaImpr;
    private String barCode;
    private String CHARG4;
    private String MATNR;
    private String MAKTX_desc;
    private String VEMEH;
    private String VEMNG;
    private String WERKS;
    private String NAME1;
    private String entrega;
    
    public Etiqueta(){
        
    }

	public Etiqueta(String EXIDVHU_HU, String fechaImpr, String barCode,
			String CHARG4, String MATNR, String MAKTXDesc, String VEMEH,
			String VEMNG, String WERKS, String NAME1) {
		super();
		this.EXIDV_HU = EXIDVHU_HU;
		this.fechaImpr = fechaImpr;
		this.barCode = barCode;
		this.CHARG4 = CHARG4;
		this.MATNR = MATNR;
		this.MAKTX_desc = MAKTXDesc;
		this.VEMEH = VEMEH;
		this.VEMNG = VEMNG;
		this.WERKS = WERKS;
		this.NAME1 = NAME1;
	}
	
	public Etiqueta(String EXIDVHU_HU, String fechaImpr, String barCode,
            String CHARG4, String MATNR, String MAKTXDesc, String VEMEH,
            String VEMNG, String WERKS, String NAME1, String entrega) {
        super();
        this.EXIDV_HU = EXIDVHU_HU;
        this.fechaImpr = fechaImpr;
        this.barCode = barCode;
        this.CHARG4 = CHARG4;
        this.MATNR = MATNR;
        this.MAKTX_desc = MAKTXDesc;
        this.VEMEH = VEMEH;
        this.VEMNG = VEMNG;
        this.WERKS = WERKS;
        this.NAME1 = NAME1;
        this.entrega = entrega;
    }

	public String getEXIDV_HU() {
		return EXIDV_HU;
	}

	public void setEXIDV_HU(String eXIDVHU) {
		EXIDV_HU = eXIDVHU;
	}

	public String getFechaImpr() {
		return fechaImpr;
	}

	public void setFechaImpr(String fechaImpr) {
		this.fechaImpr = fechaImpr;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getCHARG4() {
		return CHARG4;
	}

	public void setCHARG4(String cHARG4) {
		CHARG4 = cHARG4;
	}

	public String getMATNR() {
		return MATNR;
	}

	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}

	public String getMAKTX_desc() {
		return MAKTX_desc;
	}

	public void setMAKTX_desc(String mAKTXDesc) {
		MAKTX_desc = mAKTXDesc;
	}

	public String getVEMEH() {
		return VEMEH;
	}

	public void setVEMEH(String vEMEH) {
		VEMEH = vEMEH;
	}

	public String getVEMNG() {
		return VEMNG;
	}

	public void setVEMNG(String vEMNG) {
		VEMNG = vEMNG;
	}

	public String getWERKS() {
		return WERKS;
	}

	public void setWERKS(String wERKS) {
		WERKS = wERKS;
	}

	public String getNAME1() {
		return NAME1;
	}

	public void setNAME1(String nAME1) {
		NAME1 = nAME1;
	}
    
	public String getEntrega() {
        return entrega;
    }

    public void setEntrega(String entrega) {
        this.entrega = entrega;
    }
   
    public String getZHUEX_HU() {
		return ZHUEX_HU;
	}

	public void setZHUEX_HU(String ZHUEX_HU) {
		this.ZHUEX_HU = ZHUEX_HU;
	}

	
    
}