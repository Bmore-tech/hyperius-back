package com.bmore.hyperius.mobile.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.mobile.dto.HuDTO;
import com.bmore.hyperius.mobile.dto.HuDTOItem;
import com.bmore.hyperius.mobile.dto.HusDTO;
import com.bmore.hyperius.mobile.repository.HUsMobileRepository;
import com.bmore.hyperius.mobile.utils.ResultDT;

@Repository
public class HUsMobileRepositoryImpl implements HUsMobileRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public HuDTO validarHU(String hu) {
    ResultDT result = new ResultDT();
    HuDTO huDTO = new HuDTO();
    ResultSet rs = null;
    String query = "select WERKS,BESTQ,LGORT,LGNUM,LGTYP,LGPLA,MATNR,VERME,MEINS,SKZUA  from HCMDB.dbo.lqua WITH(NOLOCK) where lenum=?";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {
        huDTO.setWerks(rs.getString("werks"));
        huDTO.setBestq(rs.getString("bestq"));
        huDTO.setLgort(rs.getString("lgort"));
        huDTO.setLgnum(rs.getString("lgnum"));
        huDTO.setLgtyp(rs.getString("lgtyp"));
        huDTO.setLgpla(rs.getString("lgpla"));
        huDTO.setVerme(rs.getString("verme"));
        huDTO.setMeins(rs.getString("meins"));
        huDTO.setSkzua(rs.getString("skzua"));
        huDTO.setMatnr(rs.getString("matnr"));
        result.setId(1);
        result.setMsg("HU existe");
      } else {
        result.setId(2);
        result.setMsg("No existe el HU");
      }
    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }
    huDTO.setResultDT(result);
    return huDTO;
  }

  @Override
  public HusDTO validarHUVidrio(String exidv, String werks) {
    String query = "select VEKP.werks, VEPO.BESTQ, VEPO.matnr, VEPO.vemng, VEPO.vemeh, VEKP.hu_lgort from HCMDB.dbo.VEPO with(nolock) "
        + "inner join HCMDB.dbo.VEKP with(nolock) on VEPO.VENUM = VEKP.VENUM where VEKP.WERKS = ? and VEKP.EXIDV = ? and VEPO.VELIN = '1'";
    HusDTO husDTO = new HusDTO();
    ResultDT result = new ResultDT();
    HuDTOItem items = new HuDTOItem();
    List<HuDTO> itemList = new ArrayList<HuDTO>();
    items.setItem(itemList);
    husDTO.setItems(items);
    ResultSet rs = null;

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, werks);
      callableStatement.setString(2, exidv);
      rs = callableStatement.executeQuery();

      if (rs.next()) {
        HuDTO huDTO = new HuDTO();
        huDTO.setWerks(rs.getString("werks"));
        huDTO.setBestq(rs.getString("bestq"));
        huDTO.setVerme(rs.getString("vemng"));
        huDTO.setMeins(rs.getString("vemeh"));
        huDTO.setSkzua(rs.getString("hu_lgort"));
        huDTO.setMatnr(rs.getString("matnr"));
        itemList.add(huDTO);
        result.setId(1);
        result.setMsg("HU existe");
      } else {
        result.setId(2);
        result.setMsg("No existe el HU para el centro:" + werks);
      }
    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }
    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);
    return husDTO;
  }

}
