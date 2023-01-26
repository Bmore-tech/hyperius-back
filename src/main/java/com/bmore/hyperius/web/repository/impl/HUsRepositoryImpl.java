package com.bmore.hyperius.web.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.HuDTOItem;
import com.bmore.hyperius.web.dto.HusDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.HUsRepository;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.print.Etiqueta;
import com.bmore.hyperius.web.utils.print.Etiquetas;

@Repository
public class HUsRepositoryImpl implements HUsRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public Etiquetas obtieneDatosHusLqua(String hus) {
    String query = "SELECT LQUA.LENUM as HU, LQUA.MATNR as MATERIAL, LQUA.WERKS as CENTRO, MAKT.MAKTX, "
        + "CASE WHEN LFA1.NAME1+' '+LFA1.NAME2 IS NULL THEN '' ELSE LFA1.NAME1+' '+LFA1.NAME2 END as DescPlanta, LQUA.VERME as VEMNG, LQUA.MEINS as VEMEH, SUBSTRING(LQUA.CHARG,len(LQUA.CHARG) - 3 ,len(LQUA.CHARG)) as CHARG, "
        + " CONVERT(VARCHAR(10),CONVERT(date,GETDATE()),103) as FECHA, REPLACE(CONVERT(varchar(10), GETDATE(),102),'.','') as FECHA2 "
        + "FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK)  "
        + "INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR  "
        + "LEFT JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on LQUA.WERKS = ZCBC.werks "
        + "LEFT JOIN HCMDB.dbo.LFA1 LFA1 WITH(NOLOCK) on ZCBC.LIFNR = LFA1.LIFNR " + "WHERE LQUA.LENUM in("
        + hus + ")";

    ResultDTO resultDT = new ResultDTO();
    Etiquetas etiquetas = new Etiquetas();

    List<Etiqueta> listaEtiquetas = jdbcTemplate.query(query, new RowMapper<Etiqueta>() {

      @Override
      public Etiqueta mapRow(ResultSet rs, int rowNum) throws SQLException {
        Etiqueta e = new Etiqueta();
        e.setEXIDV_HU(rs.getString("HU"));
        e.setFechaImpr(rs.getString("FECHA"));
        e.setBarCode(e.getEXIDV_HU() + "," + Utils.zeroClean(rs.getString("MATERIAL")) + ","
            + rs.getString("VEMNG") + "," + rs.getString("VEMEH") + "," + rs.getString("CHARG") + ","
            + rs.getString("FECHA2"));
        e.setCHARG4(rs.getString("CHARG"));
        e.setMATNR(Utils.zeroClean(rs.getString("MATERIAL")));
        e.setMAKTX_desc(rs.getString("MAKTX"));
        e.setVEMEH(rs.getString("VEMEH"));
        e.setVEMNG(rs.getString("VEMNG"));
        e.setWERKS(rs.getString("CENTRO"));
        e.setNAME1(rs.getString("DescPlanta"));

        return e;
      }
    });

    if (listaEtiquetas.size() > 0) {
      resultDT.setId(1);
      resultDT.setMsg("HU's recuperadas");
    } else {
      resultDT.setId(2);
      resultDT.setMsg("No fue posible recuperar HU's");
    }

    etiquetas.setItems(listaEtiquetas);
    etiquetas.setResultDT(resultDT);

    return etiquetas;
  }

  @Override
  public Etiquetas obtieneDatosHusVekp(String hus) {
    String OBTENER_DATOS_HUS_VEKP = "SELECT VEKP.EXIDV as HU, VEPO.MATNR as MATERIAL, CASE WHEN VEPO.WERKS IS NULL THEN '' ELSE VEPO.WERKS END as CENTRO, "
        + "MAKT.MAKTX as DESCRIPCION, CASE WHEN LFA1.NAME1+' '+LFA1.NAME2 IS NULL THEN '' ELSE LFA1.NAME1+' '+LFA1.NAME2 END as DescPlanta, VEPO.VEMNG as VEMNG, VEPO.VEMEH as VEMEH, SUBSTRING(VEPO.CHARG,len(VEPO.CHARG) - 3 ,len(VEPO.CHARG)) as CHARG, "
        + "CONVERT(VARCHAR(10),CONVERT(date,GETDATE()),103) as FECHA, REPLACE(CONVERT(varchar(10), GETDATE(),102),'.','') as FECHA2, VEKP.VHILM as TARIMA "
        + "FROM HCMDB.dbo.VEKP VEKP WITH(NOLOCK)  "
        + "INNER JOIN HCMDB.dbo.VEPO VEPO WITH(NOLOCK) ON VEKP.VENUM = VEPO.VENUM AND VEPO.VELIN='1' "
        + "INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on VEPO.MATNR = MAKT.MATNR   "
        + "LEFT JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on VEPO.WERKS = ZCBC.werks "
        + "LEFT JOIN HCMDB.dbo.LFA1 LFA1 WITH(NOLOCK) on ZCBC.LIFNR = LFA1.LIFNR " + "WHERE VEKP.EXIDV in("
        + hus + ")";

    ResultDTO resultDT = new ResultDTO();
    Etiquetas etiquetas = new Etiquetas();

    List<Etiqueta> listaEtiquetas = jdbcTemplate.query(OBTENER_DATOS_HUS_VEKP, new RowMapper<Etiqueta>() {

      @Override
      public Etiqueta mapRow(ResultSet rs, int rowNum) throws SQLException {
        Etiqueta e = new Etiqueta();
        e.setEXIDV_HU(rs.getString("HU"));
        e.setFechaImpr(rs.getString("FECHA"));
        e.setBarCode(e.getEXIDV_HU() + "," + Utils.zeroClean(rs.getString("TARIMA")) + ","
            + Utils.zeroClean(rs.getString("MATERIAL")) + "," + rs.getString("VEMNG") + ","
            + rs.getString("VEMEH") + "," + rs.getString("CHARG") + "," + rs.getString("FECHA2"));
        e.setCHARG4(rs.getString("CHARG"));
        e.setMATNR(Utils.zeroClean(rs.getString("MATERIAL")));
        e.setMAKTX_desc(rs.getString("DESCRIPCION"));
        e.setVEMEH(rs.getString("VEMEH"));
        e.setVEMNG(rs.getString("VEMNG"));
        e.setWERKS(rs.getString("CENTRO"));
        e.setNAME1(rs.getString("DescPlanta"));

        return e;
      }
    });

    if (listaEtiquetas.size() > 0) {
      resultDT.setId(1);
      resultDT.setMsg("HU's recuperadas");
    } else {
      resultDT.setId(2);
      resultDT.setMsg("No fue posible recuperar HU's");
    }

    etiquetas.setItems(listaEtiquetas);
    etiquetas.setResultDT(resultDT);

    return etiquetas;
  }

  @Override
  public ResultDTO obtieneHuExterna(String hu) {
    String query = "select ZHUEX from HCMDB.dbo.ZPAITT_HU_EXT WITH(NOLOCK) where ZEXIDV = ? ";
    Object[] args = { hu };

    ResultDTO result = new ResultDTO();

    result.setId(2);
    result.setMsg("No fue posible recuperar HU externa para HU interna: " + hu);

    jdbcTemplate.query(query, new RowMapper<Integer>() {

      @Override
      public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        result.setId(1);
        result.setMsg(rs.getString("ZHUEX"));

        return 1;
      }
    }, args);

    return result;
  }

  @Override
  public HusDTO obtieneHusBCPS(HuDTO huDTO) {
    String OBTENER_STATUS_HUS_BCPS = "select hu,ZPE.Status from HCMDB.dbo.zContingencia WITH(NOLOCK) LEFT JOIN "
        + " HCMDB.dbo.ZPickingEntregaEntrante ZPE WITH(NOLOCK) on zContingencia.HU = ZPE.EXIDV and ZPE.idProceso='1' "
        + " where ENTREGA= ? and ((IDPROC=28) or (IDPROC = 8))  and hu is not null order by Status DESC";
    Object[] args = { huDTO.getVblen() };

    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();
    HusDTO husDTO = new HusDTO();

    List<HuDTO> itemList = jdbcTemplate.query(OBTENER_STATUS_HUS_BCPS, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTOItem = new HuDTO();

        huDTOItem.setHu(rs.getString("hu"));
        huDTOItem.setStatus(rs.getString("Status"));
        huDTOItem.setStatusVEKP(rs.getString("Status"));

        return huDTOItem;
      }
    }, args);

    if (itemList.size() > 0) {
      result.setId(1);
      result.setMsg("Se recuperaron HU´s de entrega entrante");
    } else {
      result.setId(0);
      result.setMsg("No fue posible recuperar HU´s de entrega entrante");
    }

    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }

  @Override
  public HusDTO obtieneHusCarrilPorMaterial(HuDTO huDTO) {
    String query = "select distinct(lenum),matnr from HCMDB.dbo.LQUA WITH(NOLOCK) where LGNUM = ? and LGTYP = ? and LGPLA = ? and matnr =? and SKZUA is null";
    Object[] args = { huDTO.getLgnum(), huDTO.getLgtyp(), huDTO.getLgpla(), huDTO.getMatnr() };

    HusDTO husDTO = new HusDTO();
    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();

    List<HuDTO> itemList = jdbcTemplate.query(query, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTOItem = new HuDTO();

        huDTOItem.setHu(rs.getString("lenum"));
        huDTOItem.setStatus("En Stock");

        return huDTOItem;
      }
    }, args);

    result.setId(1);
    result.setMsg("Se recuperaron HU´s de entrega entrante");
    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }

  @Override
  public HusDTO obtieneHusVEKP(HuDTO huDTO) {
    String query = "select DISTINCT VEKP.EXIDV,VEKP.LGNUM, ZPE.Status "
        + " from HCMDB.dbo.VEKP LEFT JOIN HCMDB.dbo.ZPickingEntregaEntrante ZPE  WITH(NOLOCK) on VEKP.EXIDV = ZPE.EXIDV "
        + " where VEKP.VPOBJKEY = ?  order by LGNUM,Status DESC";
    Object[] args = { huDTO.getVblen() };

    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();
    HusDTO husDTO = new HusDTO();

    List<HuDTO> itemList = jdbcTemplate.query(query, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTOItem = new HuDTO();

        huDTOItem.setHu(rs.getString("EXIDV"));
        huDTOItem.setStatus(rs.getString("Status"));
        huDTOItem.setStatusVEKP(rs.getString("LGNUM"));

        return huDTOItem;
      }
    }, args);

    if (itemList.size() > 0) {
      result.setId(1);
      result.setMsg("Se recuperaron HU´s de entrega entrante");
    } else {
      result.setId(0);
      result.setMsg("No fue posible recuperar HU´s de entrega entrante");
    }

    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }

  @Override
  public HusDTO obtieneHusZPicking(HuDTO huDTO) {
    String query = "select EXIDV, Status from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
        + "where VBELN = ? and EXIDV is not null and idProceso = ? order by Status desc";
    Object[] args = { huDTO.getVblen(), huDTO.getId() };

    HusDTO husDTO = new HusDTO();
    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();

    List<HuDTO> itemList = jdbcTemplate.query(query, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTOItem = new HuDTO();

        huDTOItem.setHu(rs.getString("EXIDV"));
        huDTOItem.setStatus(rs.getString("Status"));

        return huDTOItem;
      }
    }, args);

    result.setId(1);
    result.setMsg("Se recuperaron HU´s de Zpicking");
    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }

  @Override
  public HusDTO obtieneHusZPickingVidrio(HuDTO huDTO) {
    String query = "select EXIDV, Status from HCMDB.dbo.ZPickingVidrio WITH(NOLOCK) "
        + "where VBELN = ? and EXIDV is not null and idProceso = ? order by Status desc";
    Object[] args = { huDTO.getVblen(), huDTO.getId() };

    HusDTO husDTO = new HusDTO();
    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();

    List<HuDTO> itemList = jdbcTemplate.query(query, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTOItem = new HuDTO();

        huDTOItem.setHu(rs.getString("EXIDV"));
        huDTOItem.setStatus(rs.getString("Status"));

        return huDTOItem;
      }
    }, args);

    result.setId(1);
    result.setMsg("Se recuperaron HU´s de Zpicking");
    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }

  @Override
  public HusDTO validarHU(String hu, String werks) {
    String VALIDAR_HU_LQUA = "select WERKS,BESTQ,LGORT,LGNUM,LGTYP,LGPLA,MATNR,VERME,MEINS,SKZUA  from HCMDB.dbo.lqua WITH(NOLOCK) where lenum=? and werks = ?";
    Object[] args = { hu, werks };

    HusDTO husDTO = new HusDTO();
    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();
    List<HuDTO> itemList = new ArrayList<HuDTO>();

    result.setId(2);
    result.setMsg("No existe el HU para el centro:" + werks);

    HuDTO huDTO = jdbcTemplate.queryForObject(VALIDAR_HU_LQUA, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTO = new HuDTO();

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

        return huDTO;
      }
    }, args);

    itemList.add(huDTO);
    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }

  @Override
  public HusDTO validarHUVidrio(String exidv, String werks) {
    String VALIDAR_HU_VKP = "select VEKP.werks, VEPO.BESTQ, VEPO.matnr, VEPO.vemng, VEPO.vemeh, VEKP.hu_lgort from HCMDB.dbo.VEPO with(nolock) inner join HCMDB.dbo.VEKP with(nolock) on VEPO.VENUM = VEKP.VENUM where VEKP.WERKS = ? and VEKP.EXIDV = ? and VEPO.VELIN = '1'";
    Object[] args = { werks, exidv };

    HusDTO husDTO = new HusDTO();
    ResultDTO result = new ResultDTO();
    HuDTOItem items = new HuDTOItem();
    List<HuDTO> itemList = new ArrayList<HuDTO>();

    result.setId(2);
    result.setMsg("No existe el HU para el centro:" + werks);

    HuDTO huDTO = jdbcTemplate.queryForObject(VALIDAR_HU_VKP, new RowMapper<HuDTO>() {

      @Override
      public HuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        HuDTO huDTO = new HuDTO();

        huDTO.setWerks(rs.getString("werks"));
        huDTO.setBestq(rs.getString("bestq"));
        huDTO.setVerme(rs.getString("vemng"));
        huDTO.setMeins(rs.getString("vemeh"));
        huDTO.setSkzua(rs.getString("hu_lgort"));
        huDTO.setMatnr(rs.getString("matnr"));

        result.setId(1);
        result.setMsg("HU existe");

        return huDTO;
      }
    }, args);

    itemList.add(huDTO);
    items.setItem(itemList);
    husDTO.setItems(items);
    husDTO.setResultDT(result);

    return husDTO;
  }
}
