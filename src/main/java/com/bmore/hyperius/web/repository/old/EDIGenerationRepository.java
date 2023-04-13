package com.bmore.hyperius.web.repository.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.export.report.EDIGenerationDTO_Sec_A;
import com.bmore.hyperius.web.utils.export.report.EDIGenerationDTO_Sec_B;

@Repository
public class EDIGenerationRepository {
  
  @Autowired
  private DBConnection dbConnection;

	public static final String VS_BCPS_EXPORTACION_EDI_A = "SELECT TKNUM, SORT1, FERR_ORIGEN, FERR_DESTINO, FECHA, HORA, NOCAJA, NTGEW, SELLO, SELLO_IMPORTADOR,"
			+ " CVE_EST_ORIGEN, EDO_ORIGEN, CVE_EST_DEST, EDO_DESTINO FROM VS_BCPS_EXPORTACION_EDI_A WITH(NOLOCK) WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_A Exportacion_EDI_A(String VBELN) {
		EDIGenerationDTO_Sec_A ediDto = new EDIGenerationDTO_Sec_A();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_A);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setTknum(rs.getString("TKNUM"));
					ediDto.setSort1(rs.getString("SORT1"));
					ediDto.setFer_origen(rs.getString("FERR_ORIGEN"));
					ediDto.setFer_destino(rs.getString("FERR_DESTINO"));
					ediDto.setFechZCont(rs.getDate("FECHA"));
					ediDto.setHoraZCont(rs.getTime("HORA"));
					ediDto.setCaja(rs.getString("NOCAJA"));
					ediDto.setNtgew(rs.getString("NTGEW"));
					ediDto.setSello(rs.getString("SELLO"));
					ediDto.setSello_imp(rs.getString("SELLO_IMPORTADOR"));
					ediDto.setEdo_origen(rs.getString("EDO_ORIGEN"));
					ediDto.setCve_origen(rs.getString("CVE_EST_ORIGEN"));
					ediDto.setCve_desitno(rs.getString("CVE_EST_DEST"));
					ediDto.setEdo_destino(rs.getString("EDO_DESTINO"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_B = "SELECT NAME1, STREET, HOUSE_NUM1, CITY1, FERR, SORT2, TEL_NUMBER, SMTP_ADDR FROM VS_BCPS_EXPORTACION_EDI_B WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_B(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_B);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setName1(Utils.isNull(rs.getString("NAME1")));
					ediDto.setStreet1(Utils.isNull(rs.getString("STREET"))
							+ " " + Utils.isNull(rs.getString("HOUSE_NUM1")));
					ediDto.setCity1(Utils.isNull(rs.getString("CITY1")) + " "
							+ Utils.isNull(rs.getString("FERR")));
					ediDto.setSort2(Utils.isNull(rs.getString("SORT2")));
					ediDto.setNumber(Utils.isNull(rs.getString("TEL_NUMBER")));
					ediDto.setAddr(Utils.isNull(rs.getString("SMTP_ADDR")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_C = "SELECT  NAME1, STREET, HOUSE_NUM1, CITY1, FERR, KNAME, TELF1, SMTP_ADDR FROM VS_BCPS_EXPORTACION_EDI_C WITH(NOLOCK) WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_C(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_C);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setName1(Utils.isNull(rs.getString("NAME1")));
					ediDto.setStreet1(Utils.isNull(rs.getString("STREET"))
							+ " " + Utils.isNull(rs.getString("HOUSE_NUM1")));
					ediDto.setCity1(Utils.isNull(rs.getString("CITY1")) + " "
							+ Utils.isNull(rs.getString("FERR")));
					ediDto.setKname(Utils.isNull(rs.getString("KNAME")));
					ediDto.setNumber(Utils.isNull(rs.getString("TELF1")));
					ediDto.setAddr(Utils.isNull(rs.getString("SMTP_ADDR")));
					ediDto.setSort2(Utils.isNull(rs.getString("FERR")));
					ediDto.setCity2(Utils.isNull(rs.getString("CITY1")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_D = "SELECT  NAME1, STREET, HOUSE_NUM1, CITY1, FERR, SCACD FROM VS_BCPS_EXPORTACION_EDI_D WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_D(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_D);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setName1(Utils.isNull(rs.getString("SCACD")) + " "
							+ Utils.isNull(rs.getString("NAME1")));
					ediDto.setStreet1(Utils.isNull(rs.getString("STREET"))
							+ " " + Utils.isNull(rs.getString("HOUSE_NUM1")));
					ediDto.setCity1(Utils.isNull(rs.getString("CITY1")) + " "
							+ Utils.isNull(rs.getString("FERR")));
					ediDto.setSort2(Utils.isNull(rs.getString("FERR")));
					ediDto.setEdo_ori(Utils.isNull(rs.getString("CITY1")));
					ediDto.setKname(Utils.isNull(rs.getString("SCACD")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_E = "SELECT NAME1, STREET, HOUSE_NUM1, CITY1, FERR FROM VS_BCPS_EXPORTACION_EDI_E WITH(NOLOCK) WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_E(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_E);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setName1(Utils.isNull(rs.getString("NAME1")));
					ediDto.setStreet1(Utils.isNull(rs.getString("STREET"))
							+ " " + Utils.isNull(rs.getString("HOUSE_NUM1")));
					ediDto.setCity1(Utils.isNull(rs.getString("CITY1")) + " "
							+ Utils.isNull(rs.getString("FERR")));
					ediDto.setSort2(Utils.isNull(rs.getString("FERR")));
					ediDto.setKname(Utils.isNull(rs.getString("CITY1")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_F = "SELECT SORT2, STREET, HOUSE_NUM1, CITY1, FERR, TEL_NUMBER, REMARK, SMTP_ADDR FROM VS_BCPS_EXPORTACION_EDI_F WITH (NOLOCK) WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_F(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_F);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setSort2(Utils.isNull(rs.getString("SORT2")));
					ediDto.setStreet1(Utils.isNull(rs.getString("STREET"))
							+ " " + Utils.isNull(rs.getString("HOUSE_NUM1")));
					ediDto.setCity1(Utils.isNull(rs.getString("CITY1")));
					ediDto.setFerr(Utils.isNull(rs.getString("FERR")));
					ediDto.setRemark(Utils.isNull(rs.getString("REMARK")));
					ediDto.setAddr(Utils.isNull(rs.getString("SMTP_ADDR")));
					ediDto.setNumber(Utils.isNull(rs.getString("TEL_NUMBER")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_G = "SELECT TRAMO_FER FROM VS_BCPS_EXPORTACION_EDI_G WITH(NOLOCK) WHERE VBELN = ?";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_G(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_G);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setTramo_fer(Utils.isNull(rs.getString("TRAMO_FER")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

	public static final String VS_BCPS_EXPORTACION_EDI_H = "SELECT ARKTX, FERTH, LFIMG, NTGEW, CVE_TARIFA FROM VS_BCPS_EXPORTACION_EDI_H WITH(NOLOCK) WHERE VBELN  = ? ";

	public EDIGenerationDTO_Sec_B Exportacion_EDI_H(String VBELN) {
		EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(VS_BCPS_EXPORTACION_EDI_H);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					ediDto.setMaterial(Utils.isNull(rs.getString("ARKTX")));
					ediDto.setNumber(Utils.isNull(rs.getString("FERTH")));
					ediDto.setFerr(Utils.isNull(rs.getString("LFIMG")));
					ediDto.setKname(Utils.isNull(rs.getString("NTGEW")));
					ediDto.setTramo_fer(Utils.isNull(rs.getString("CVE_TARIFA")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ediDto;
	}

}
