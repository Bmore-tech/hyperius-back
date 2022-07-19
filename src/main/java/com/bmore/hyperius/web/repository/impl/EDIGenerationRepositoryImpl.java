package com.bmore.hyperius.web.repository.impl;

import java.sql.Date;
import java.sql.Time;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.repository.EDIGenerationRepository;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.export.report.EDIGenerationDTO_Sec_A;
import com.bmore.hyperius.web.utils.export.report.EDIGenerationDTO_Sec_B;

@Repository
public class EDIGenerationRepositoryImpl implements EDIGenerationRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public EDIGenerationDTO_Sec_A Exportacion_EDI_A(String VBELN) {
    EDIGenerationDTO_Sec_A ediDto = new EDIGenerationDTO_Sec_A();

    try {
      String sql = String.format("SELECT TKNUM, SORT1, FERR_ORIGEN, FERR_DESTINO, FECHA, HORA, NOCAJA, NTGEW, " +
          "SELLO, SELLO_IMPORTADOR, CVE_EST_ORIGEN, EDO_ORIGEN, CVE_EST_DEST, EDO_DESTINO " +
          "FROM VS_BCPS_EXPORTACION_EDI_A WITH(NOLOCK) WHERE VBELN = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setTknum((String) row.get("TKNUM"));
      ediDto.setSort1((String) row.get("SORT1"));
      ediDto.setFer_origen((String) row.get("FERR_ORIGEN"));
      ediDto.setFer_destino((String) row.get("FERR_DESTINO"));
      ediDto.setFechZCont((Date) row.get("FECHA"));
      ediDto.setHoraZCont((Time) row.get("HORA"));
      ediDto.setCaja((String) row.get("NOCAJA"));
      ediDto.setNtgew((String) row.get("NTGEW"));
      ediDto.setSello((String) row.get("SELLO"));
      ediDto.setSello_imp((String) row.get("SELLO_IMPORTADOR"));
      ediDto.setEdo_origen((String) row.get("EDO_ORIGEN"));
      ediDto.setCve_origen((String) row.get("CVE_EST_ORIGEN"));
      ediDto.setCve_desitno((String) row.get("CVE_EST_DEST"));
      ediDto.setEdo_destino((String) row.get("EDO_DESTINO"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_B(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT NAME1, STREET, HOUSE_NUM1, CITY1, FERR, SORT2, TEL_NUMBER, " +
          "SMTP_ADDR FROM VS_BCPS_EXPORTACION_EDI_B WHERE VBELN = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setName1((String) row.get("NAME1"));
      ediDto.setStreet1(row.get("STREET") + " " + row.get("HOUSE_NUM1"));
      ediDto.setCity1(row.get("CITY1") + " " + row.get("FERR"));
      ediDto.setSort2(Utils.isNull((String) row.get("SORT2")));
      ediDto.setNumber(Utils.isNull((String) row.get("TEL_NUMBER")));
      ediDto.setAddr(Utils.isNull((String) row.get("SMTP_ADDR")));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_C(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT  NAME1, STREET, HOUSE_NUM1, CITY1, FERR, KNAME, TELF1, " +
          "SMTP_ADDR FROM VS_BCPS_EXPORTACION_EDI_C WITH(NOLOCK) WHERE VBELN = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setName1((String) row.get("NAME1"));
      ediDto.setStreet1(row.get("STREET") + " " + row.get("HOUSE_NUM1"));
      ediDto.setCity1(row.get("CITY1") + " " + row.get("FERR"));
      ediDto.setKname((String) row.get("KNAME"));
      ediDto.setNumber((String) row.get("TELF1"));
      ediDto.setAddr((String) row.get("SMTP_ADDR"));
      ediDto.setSort2((String) row.get("FERR"));
      ediDto.setCity2((String) row.get("CITY1"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_D(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT  NAME1, STREET, HOUSE_NUM1, CITY1, FERR, SCACD FROM " +
          "VS_BCPS_EXPORTACION_EDI_D WHERE VBELN = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setName1(row.get("SCACD") + " " + row.get("NAME1"));
      ediDto.setStreet1(row.get("STREET") + " " + row.get("HOUSE_NUM1"));
      ediDto.setCity1(row.get("CITY1") + " " + row.get("FERR"));
      ediDto.setSort2((String) row.get("FERR"));
      ediDto.setEdo_ori((String) row.get("CITY1"));
      ediDto.setKname((String) row.get("SCACD"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_E(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT  NAME1, STREET, HOUSE_NUM1, CITY1, FERR, SCACD FROM " +
          "VS_BCPS_EXPORTACION_EDI_D WHERE VBELN = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setName1((String) row.get("NAME1"));
      ediDto.setStreet1(row.get("STREET") + " " + row.get("HOUSE_NUM1"));
      ediDto.setCity1(row.get("CITY1") + " " + row.get("FERR"));
      ediDto.setSort2((String) row.get("FERR"));
      ediDto.setKname((String) row.get("CITY1"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_F(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT SORT2, STREET, HOUSE_NUM1, CITY1, FERR, TEL_NUMBER, REMARK, " +
          "SMTP_ADDR FROM VS_BCPS_EXPORTACION_EDI_F WITH (NOLOCK) WHERE VBELN = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setSort2((String) row.get("SORT2"));
      ediDto.setStreet1(row.get("STREET") + " " + row.get("HOUSE_NUM1"));
      ediDto.setCity1((String) row.get("CITY1"));
      ediDto.setFerr((String) row.get("FERR"));
      ediDto.setRemark((String) row.get("REMARK"));
      ediDto.setAddr((String) row.get("SMTP_ADDR"));
      ediDto.setNumber((String) row.get("TEL_NUMBER"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_G(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT TRAMO_FER FROM VS_BCPS_EXPORTACION_EDI_G WITH(NOLOCK) WHERE VBELN = '%s';",
          VBELN);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      ediDto.setTramo_fer((String) row.get("TRAMO_FER"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }

  @Override
  public EDIGenerationDTO_Sec_B Exportacion_EDI_H(String VBELN) {
    EDIGenerationDTO_Sec_B ediDto = new EDIGenerationDTO_Sec_B();

    try {
      String sql = String.format("SELECT ARKTX, FERTH, LFIMG, NTGEW, CVE_TARIFA FROM " +
          "VS_BCPS_EXPORTACION_EDI_H WITH(NOLOCK) WHERE VBELN  = '%s';", VBELN);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ediDto.setMaterial((String) row.get("ARKTX"));
      ediDto.setNumber((String) row.get("FERTH"));
      ediDto.setFerr((String) row.get("LFIMG"));
      ediDto.setKname((String) row.get("NTGEW"));
      ediDto.setTramo_fer((String) row.get("CVE_TARIFA"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ediDto;
  }
}
