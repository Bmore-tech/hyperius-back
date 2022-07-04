package com.bmore.hyperius.web.utils.export.report;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import com.bmore.hyperius.web.repository.old.EDIGenerationRepository;
import com.bmore.hyperius.web.utils.Utils;

public class EDIGenerationBO {

	public String generateEDI(String VBELN) {
		StringBuffer EDI = new StringBuffer();
		EDIGenerationDTO_Sec_A ediDto = new EDIGenerationDTO_Sec_A();
		EDIGenerationDTO_Sec_B ediDtoB = new EDIGenerationDTO_Sec_B();
		EDIGenerationDTO_Sec_B ediDtoC = new EDIGenerationDTO_Sec_B();
		EDIGenerationDTO_Sec_B ediDtoD = new EDIGenerationDTO_Sec_B();
		EDIGenerationDTO_Sec_B ediDtoE = new EDIGenerationDTO_Sec_B();
		EDIGenerationDTO_Sec_B ediDtoF = new EDIGenerationDTO_Sec_B();
		EDIGenerationDTO_Sec_B ediDtoG = new EDIGenerationDTO_Sec_B();
		EDIGenerationDTO_Sec_B ediDtoH = new EDIGenerationDTO_Sec_B();
		EDIGenerationRepository ediDao = new EDIGenerationRepository();
		ediDto = ediDao.Exportacion_EDI_A(VBELN);
		ediDtoB = ediDao.Exportacion_EDI_B(VBELN);
		ediDtoC = ediDao.Exportacion_EDI_C(VBELN);
		ediDtoD = ediDao.Exportacion_EDI_D(VBELN);
		ediDtoE = ediDao.Exportacion_EDI_E(VBELN);
		ediDtoF = ediDao.Exportacion_EDI_F(VBELN);
		ediDtoG = ediDao.Exportacion_EDI_G(VBELN);
		ediDtoH = ediDao.Exportacion_EDI_H(VBELN);

		EDI = ediAppend(EDI, "GS", 0);
		EDI = ediAppend(EDI, "SR", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getSort1()), 0); // ADRC-SORT1
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getFer_origen()), 0); // ZPAITT_RUTASFERR-FERR_ORIGEN
		EDI = ediAppend(EDI, dateForm(ediDto.getFechZCont(), "yyyyMMdd"), 0); // FECHA
		// ZCONT
		EDI = ediAppend(EDI, timeForm(ediDto.getHoraZCont(), "HHmm"), 0); // HORA
		// ZCONT
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getTknum()).substring(1,
				Utils.isNull(ediDto.getTknum()).length()), 0); // TRANSPORTE
		// SUBSTRING
		// A 9
		EDI = ediAppend(EDI, "X", 0);
		EDI = ediAppend(EDI, "005010", 1);

		EDI = ediAppend(EDI, "ST", 0);
		EDI = ediAppend(EDI, "404", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getTknum()).substring(1,
				Utils.isNull(ediDto.getTknum()).length()), 1);// TRANSPORTE

		EDI = ediAppend(EDI, "BX", 0);
		EDI = ediAppend(EDI, "00", 0);
		EDI = ediAppend(EDI, "R", 0);
		EDI = ediAppend(EDI, "11", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getFer_origen()), 0);
		EDI = ediAppend(EDI, "K", 0);
		EDI = ediAppend(EDI, "B", 1);

		EDI = ediAppend(EDI, "BNX", 0);
		EDI = ediAppend(EDI, "A", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "S", 1);

		EDI = ediAppend(EDI, "M3", 0);
		EDI = ediAppend(EDI, "R", 0);
		EDI = ediAppend(EDI, dateForm(ediDto.getFechZCont(), "yyyyMMdd"), 0); // FECHA
		// ZCONT
		EDI = ediAppend(EDI, timeForm(ediDto.getHoraZCont(), "HHmm") + "*", 1); // HORA
		// ZCONT

		EDI = ediAppend(EDI, "N9", 0);
		EDI = ediAppend(EDI, "BM", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getTknum()).substring(1,
				Utils.isNull(ediDto.getTknum()).length()), 0); // TRANSPORTE
		// SUBSTRING
		// A 9
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, dateForm(ediDto.getFechZCont(), "yyyyMMdd"), 0); // FECHA
		// ZCONT
		EDI = ediAppend(EDI, timeForm(ediDto.getHoraZCont(), "HHmm"), 0); // HORA
		// ZCONT
		EDI = ediAppend(EDI, "CT", 1);

		EDI = ediAppend(EDI, "N7", 0);
		EDI = ediAppend(EDI, Utils
				.regexSplit(Utils.isNull(ediDto.getCaja()), 1), 0); // PRIMEROS
																	// 4
		// DIGITOS
		// CAJA PROC
		// 12
		EDI = ediAppend(EDI, Utils
				.regexSplit(Utils.isNull(ediDto.getCaja()), 2), 0); // PRIMEROS
																	// 4
		// PROC 12

		EDI = ediAppend(EDI, Utils.isNull(ediDtoH.getKname()).split("\\.")[0],
				0); // LIKP-NTGEW (truncado a 5 digitos)
		EDI = ediAppend(EDI, "N", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "RR", 1);

		EDI = ediAppend(EDI, "M7", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getSello()), 0); // SELLO PROC
		// 12
		EDI = ediAppend(EDI, " " + Utils.isNull(ediDto.getSello_imp()), 1); // SELLO-IMP
		// PROC
		// 12

		EDI = ediAppend(EDI, "F9", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getCve_origen()), 0); // RUTAS
		// FERR
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getEdo_origen()), 1); // RUTAS
		// FERR

		EDI = ediAppend(EDI, "D9", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getCve_desitno()), 0); // RUTAS
		// FERR
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getEdo_destino()), 1); // RUTAS
		// FERR

		// -////-Section B-////-//-/////-//

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "SH", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoB.getName1()), 1); // VBFA

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoB.getStreet1()), 0); // Concatenar
		// ADRC-CITY1
		// y
		// ZPAITT_EDOSFERR-FERR
		EDI = ediAppend(EDI, Utils.isNull(ediDtoB.getCity1()), 1); // Concatenar
		// ADRC-CITY1
		// y
		// ZPAITT_EDOSFERR-FERR

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getCve_origen()), 0); // RUTAS
		// FERR
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getEdo_origen()), 1); // RUTAS
		// FERR

		// ADMINISTR....
		EDI = ediAppend(EDI, "PER", 0);
		EDI = ediAppend(EDI, "IC", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoB.getSort2()), 0); // ADRC-SORT2
		EDI = ediAppend(EDI, "TE", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoB.getNumber()), 0); // ADRC-TEL_NUMBER
		EDI = ediAppend(EDI, "EM", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoB.getAddr()), 1); // ADR6-SMTP_ADDR

		// PAGADOR
		// ----- SECTION C -----//

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "CN", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getName1()), 1); // ADRC-NAME1

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getStreet1()), 0); // Concatenar
		// ADRC-STREET
		// Y
		// ADRC-HOUSE_NUM1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getCity1()), 1); // Concatenar
		// ADRC-CITY1
		// y

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getCve_desitno()), 0); // RUTAS
		// FERR
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getEdo_destino()), 1); // RUTAS
		// FERR

		EDI = ediAppend(EDI, "PER", 0);
		EDI = ediAppend(EDI, "IC", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getKname()), 0); // KNVK-NAME1
		EDI = ediAppend(EDI, "TE", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getNumber()), 0); // KNVK-TELF1
		EDI = ediAppend(EDI, "EM", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getAddr()), 1); // ADR6 -
		// SMTP_ADDR

		// REPEAT

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "11", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getName1()), 1); // ADRC-NAME1

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getStreet1()), 0); // Concatenar
		// ADRC-STREET
		// Y
		// ADRC-HOUSE_NUM1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getCity1()), 1); // Concatenar
		// ADRC-CITY1
		// y //
		// ZPAITT_EDOSFERR-FERR

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getCity2()), 0);// ADRC-CITY1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getSort2()), 1);// ZPAITT_EDOSFERR-FERR

		// REPEAT

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "11", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getName1()), 1); // ADRC-NAME1

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getStreet1()), 0); // Concatenar
		// ADRC-STREET
		// Y
		// ADRC-HOUSE_NUM1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getCity1()), 1); // Concatenar
		// ADRC-CITY1
		// y //
		// ZPAITT_EDOSFERR-FERR

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getCity2()), 0);// ADRC-CITY1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getSort2()), 1);// ZPAITT_EDOSFERR-FERR

		// AGENTE ADUANAL MEXICO

		// ---------------------------- SECTION D ------------------//

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "XR", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoD.getName1()), 1); //

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoD.getStreet1()), 0); // Concatenar
		EDI = ediAppend(EDI, Utils.isNull(ediDtoD.getCity1()), 1); // Concatenar

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoD.getEdo_ori()), 0);// ADRC-CITY1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoD.getSort2()), 1);// ZPAITT_EDOSFERR-FERR

		// REPEAT
		// ------------SECTION E -----------------//

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "XU", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoE.getName1()), 1); // Concatenar
		// LFA1-SCACD
		// y
		// LFA1-NAME1

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoE.getStreet1()), 0); // Concatenar
		// ADRC-STREET
		// Y
		// ADRC-HOUSE_NUM1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoE.getCity1()), 1); // Concatenar
		// ADRC-CITY1
		// y
		// ZPAITT_EDOSFERR-FERR

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoE.getKname()), 0);// ZPAITT_EDOSFERR-FERR
		EDI = ediAppend(EDI, Utils.isNull(ediDtoE.getSort2()), 1);// ADRC-CITY1
		// ------------SECTION F-----------------//

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "C1", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getSort2()), 1); // ADRC-SORT2

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getStreet1()), 1);

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getCity1()), 0);// ADRC-CITY1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getFerr()), 1);// ZPAITT_EDOSFERR-FERR

		EDI = ediAppend(EDI, "PER", 0);
		EDI = ediAppend(EDI, "IC", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getRemark()), 0); // KNVK-NAME1
		EDI = ediAppend(EDI, "TE", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getNumber()), 0); // KNVK-TELF1
		EDI = ediAppend(EDI, "EM", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getAddr()), 1); // ADR6 -
		// SMTP_ADDR

		// Solicitante
		// SECTION ---------------C AGAIN - -------------------------------

		EDI = ediAppend(EDI, "N1", 0);
		EDI = ediAppend(EDI, "BN", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getName1()), 1); // ADRC-NAME1

		EDI = ediAppend(EDI, "N3", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getStreet1()), 0); // Concatenar
		// ADRC-STREET
		// Y
		// ADRC-HOUSE_NUM1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getCity1()), 1); // Concatenar
		// ADRC-CITY1
		// y

		EDI = ediAppend(EDI, "N4", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getCity1()), 0);// ADRC-CITY1
		EDI = ediAppend(EDI, Utils.isNull(ediDtoF.getFerr()), 1);// ZPAITT_EDOSFERR-FERR

		EDI = ediAppend(EDI, "PER", 0);
		EDI = ediAppend(EDI, "IC", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getKname()), 0); // KNVK-NAME1
		EDI = ediAppend(EDI, "TE", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getNumber()), 0); // KNVK-TELF1
		EDI = ediAppend(EDI, "EM", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoC.getAddr()), 1); // ADR6 -
		// SMTP_ADDR

		// /--SECTION G--///

		if (ediDtoG.getTramo_fer() != null) {
			if (ediDtoG.getTramo_fer().split("-")[0].equalsIgnoreCase("FXE")) {
				EDI = ediAppend(EDI, "R2", 0);
				EDI = ediAppend(EDI, ediDtoG.getTramo_fer().split("-")[0], 0);
				EDI = ediAppend(EDI, "R", 0);
				try {
					EDI = ediAppend(EDI,
							ediDtoG.getTramo_fer().split("-")[1] == null ? ""
									: ediDtoG.getTramo_fer().split("-")[1], 1);
				} catch (Exception e) {
					EDI = ediAppend(EDI, "", 1);
				}

				if (ediDtoG.getTramo_fer().split("-").length > 1) {
					int z = 1;
					for (int x = 2; x <= ediDtoG.getTramo_fer().split("-").length; x++) {
						EDI = ediAppend(EDI, "R2", 0);
						try {
							EDI = ediAppend(EDI, ediDtoG.getTramo_fer().split(
									"-")[x] == null ? "" : ediDtoG
									.getTramo_fer().split("-")[x], 0);
						} catch (Exception e) {
							EDI = ediAppend(EDI, "", 0);
						}
						x++;
						EDI = ediAppend(EDI, "" + z + "", 0);
						z++;
						try {
							EDI = ediAppend(EDI, ediDtoG.getTramo_fer().split(
									"-")[x] == null ? "" : ediDtoG
									.getTramo_fer().split("-")[x], 1);
						} catch (Exception e) {
							EDI = ediAppend(EDI, "", 1);
						}
					}
				}
			} else {
				EDI = ediAppend(EDI, "R2", 0);
				EDI = ediAppend(EDI, ediDtoG.getTramo_fer().split("-")[0], 0);
				EDI = ediAppend(EDI, "I", 0);
				try {
					EDI = ediAppend(EDI,
							ediDtoG.getTramo_fer().split("-")[1] == null ? ""
									: ediDtoG.getTramo_fer().split("-")[1], 1);
				} catch (Exception e) {
					EDI = ediAppend(EDI, "", 1);
				}

				if (ediDtoG.getTramo_fer().split("-").length > 1) {
					EDI = ediAppend(EDI, "R2", 0);
					EDI = ediAppend(EDI, ediDtoG.getTramo_fer().split("-")[2],
							0);
					EDI = ediAppend(EDI, "R", 0);
					try {
						EDI = ediAppend(
								EDI,
								ediDtoG.getTramo_fer().split("-")[3] == null ? ""
										: ediDtoG.getTramo_fer().split("-")[3],
								1);
					} catch (Exception e) {
						EDI = ediAppend(EDI, "", 1);
					}

					if (ediDtoG.getTramo_fer().split("-").length > 3) {
						int z = 1;
						for (int x = 2; x <= ediDtoG.getTramo_fer().split("-").length; x++) {
							EDI = ediAppend(EDI, "R2", 0);
							try {
								EDI = ediAppend(EDI, ediDtoG.getTramo_fer()
										.split("-")[x] == null ? "" : ediDtoG
										.getTramo_fer().split("-")[x], 0);
							} catch (Exception e) {
								EDI = ediAppend(EDI, "", 0);
							}
							x++;
							EDI = ediAppend(EDI, "" + z + "", 0);
							z++;
							try {
								EDI = ediAppend(EDI, ediDtoG.getTramo_fer()
										.split("-")[x] == null ? "" : ediDtoG
										.getTramo_fer().split("-")[x], 1);
							} catch (Exception e) {
								EDI = ediAppend(EDI, "", 1);
							}
						}
					}
				}

			}
		} else {
			EDI = ediAppend(EDI, "R2", 0);
			EDI = ediAppend(EDI, "", 0);
			EDI = ediAppend(EDI, "R", 0);
			EDI = ediAppend(EDI, "", 1);
		}

		// CLOSE VALIDATION SECTION G

		EDI = ediAppend(EDI, "H3", 0);
		EDI = ediAppend(EDI, "XP", 1);

		EDI = ediAppend(EDI, "LX", 0);
		EDI = ediAppend(EDI, "1", 1);

		// SECTION H

		EDI = ediAppend(EDI, "L5", 0);
		EDI = ediAppend(EDI, "1", 0);
		EDI = ediAppend(EDI, "BEER" + Utils.isNull(ediDtoH.getMaterial()), 0);// Concatenar
		// la
		// constante
		// "BEER"
		// y
		// LIPS-ARKTX
		EDI = ediAppend(EDI, ediDtoH.getNumber(), 0); // MARA-FERTH
		EDI = ediAppend(EDI, "T", 1);

		EDI = ediAppend(EDI, "L5", 0);
		EDI = ediAppend(EDI, "1", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoH.getFerr()).split("\\.")[0]
				+ " CTNS", 1); // Concatenar LIPS- LFIMG y "CTNS"

		EDI = ediAppend(EDI, "L0", 0);
		EDI = ediAppend(EDI, "1", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoH.getKname()).split("\\.")[0],
				0); // LIPS-NTGEW / 10 (entre diez)
		EDI = ediAppend(EDI, "N", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoH.getFerr()).split("\\.")[0], 0);// LIPS-
		// LFIMG
		EDI = ediAppend(EDI, "PCS", 1);

		EDI = ediAppend(EDI, "PI", 0);
		EDI = ediAppend(EDI, "CT", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDtoH.getTramo_fer()), 0);// ZPAITT_RUTASFERR
		// -
		// CVE_TARIFA
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "", 0);
		EDI = ediAppend(EDI, "FXE", 0);
		EDI = ediAppend(EDI, "FXEIND01", 1);

		EDI = ediAppend(EDI, "SE", 0);
		EDI = ediAppend(EDI, "" + EDI.toString().split("\n").length + "", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getTknum()).substring(1,
				Utils.isNull(ediDto.getTknum()).length()), 1);// TRANSPORTE

		EDI = ediAppend(EDI, "GE", 0);
		EDI = ediAppend(EDI, "1", 0);
		EDI = ediAppend(EDI, Utils.isNull(ediDto.getTknum()).substring(1,
				Utils.isNull(ediDto.getTknum()).length()), 1);// TRANSPORTE

		return EDI.toString();
	}

	public static StringBuffer ediAppend(StringBuffer strbf, String toAdd,
			Integer type) {
		switch (type) {
		case 1:
			strbf = strbf.append(toAdd).append("\n");
			break;

		case 2:
			strbf = strbf.append(toAdd).append("");
			break;

		default:
			strbf = strbf.append(toAdd).append("*");
			break;
		}
		return strbf;
	}

	public static String dateForm(Date toConvert, String dateformat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		return sdf.format(toConvert);
	}

	public static String timeForm(Time toConvert, String dateformat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		return sdf.format(toConvert);
	}

}
