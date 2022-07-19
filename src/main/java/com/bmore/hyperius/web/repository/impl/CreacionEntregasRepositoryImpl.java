package com.bmore.hyperius.web.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.CreacionEntregaItemDTO;
import com.bmore.hyperius.web.dto.CreacionEntregasDTO;
import com.bmore.hyperius.web.dto.CrecionEntregaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.CreacionEntregasRepository;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class CreacionEntregasRepositoryImpl implements CreacionEntregasRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public CreacionEntregasDTO obtieneMaterialDAO() {

    CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();
    ArrayList<CrecionEntregaDTO> listMaterialDTOs = new ArrayList<>();
    CreacionEntregaItemDTO materialItemDTO = new CreacionEntregaItemDTO();
    ResultDTO resultDT = new ResultDTO();

    String sql = "SELECT MATNR, MAKTX FROM VS_BCPS_ENTREGAS_MATERIALES WITH(NOLOCK)";

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      CrecionEntregaDTO materialDTO = new CrecionEntregaDTO();
      materialDTO.setMatnr((String) row.get("MATNR"));
      materialDTO.setMaktx((String) row.get("MAKTX"));
      listMaterialDTOs.add(materialDTO);
    }

    resultDT.setId(listMaterialDTOs.size() > 0 ? 1 : 3);
    materialItemDTO.setItem(listMaterialDTOs);
    materialesDTO.setItems(materialItemDTO);
    materialesDTO.setResultDT(resultDT);

    return materialesDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneTarimasDAO(CrecionEntregaDTO materialDTO) {
    CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();
    ResultDTO resultDT = new ResultDTO();
    CreacionEntregaItemDTO materialItemDTO = new CreacionEntregaItemDTO();
    ArrayList<CrecionEntregaDTO> listMaterialDTOs = new ArrayList<>();

    String sql = String.format(
        "SELECT TARIMA, MAKTX FROM VS_BCPS_ENTREGAS_NORMA_EMBALAJE WITH(NOLOCK) WHERE MATERIAL = '%s';",
        Utils.zeroFill(materialDTO.getMatnr(), 18));

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      CrecionEntregaDTO tarimasDTO = new CrecionEntregaDTO();
      tarimasDTO.setMatnr((String) row.get("MATNR"));
      tarimasDTO.setMaktx((String) row.get("MAKTX"));
      listMaterialDTOs.add(tarimasDTO);
    }

    resultDT.setId(listMaterialDTOs.size() > 0 ? 1 : 3);
    materialItemDTO.setItem(listMaterialDTOs);
    materialesDTO.setItems(materialItemDTO);
    materialesDTO.setResultDT(resultDT);

    return materialesDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneCentrosDAO() {
    CreacionEntregasDTO centrosDTO = new CreacionEntregasDTO();
    ResultDTO resultDT = new ResultDTO();
    CreacionEntregaItemDTO centrosItemDTO = new CreacionEntregaItemDTO();
    ArrayList<CrecionEntregaDTO> listaCentrosDTO = new ArrayList<>();

    String sql = "SELECT WERKS, DESCRIPCION FROM zCentrosBCPS WITH(NOLOCK) WHERE WERKS LIKE 'PC%' order by WERKS";

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      CrecionEntregaDTO tarimasDTO = new CrecionEntregaDTO();
      tarimasDTO.setWerks((String) row.get("WERKS"));
      tarimasDTO.setWerksDesc((String) row.get("DESCRIPCION"));
      listaCentrosDTO.add(tarimasDTO);
    }

    if (listaCentrosDTO.size() > 0) {
      CrecionEntregaDTO tarimasDTO = new CrecionEntregaDTO();
      tarimasDTO.setWerks("");
      tarimasDTO.setWerksDesc("");
      listaCentrosDTO.add(0, tarimasDTO);
      log.error("CreacionEntrega Centros Recuperados con exito");
      resultDT.setId(1);
      resultDT.setMsg("CreacionEntrega Centros Recuperados con exito");
    } else {
      log.error("CreacionEntrega Centros No Recuperados");
      resultDT.setId(3);
      resultDT.setMsg("CreacionEntrega Centros No Recuperados");
    }
    centrosItemDTO.setItem(listaCentrosDTO);
    centrosDTO.setItems(centrosItemDTO);
    centrosDTO.setResultDT(resultDT);

    return centrosDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneAgenciasDAO() {
    CreacionEntregasDTO agenciasDTO = new CreacionEntregasDTO();

    ResultDTO resultDT = new ResultDTO();
    CreacionEntregaItemDTO agenciasItemDTO = new CreacionEntregaItemDTO();
    ArrayList<CrecionEntregaDTO> listAgenciasItemDTO = new ArrayList<>();

    String sql = "SELECT T001W.WERKS, T001W.NAME1,LFA1.LIFNR,LFA1.NAME1 as NAME_LIFNR FROM T001W WITH(NOLOCK) inner join LFA1 LFA1 on dbo. T001W.WERKS=LFA1.WERKS WHERE T001W.WERKS NOT LIKE 'PC%' ";

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      CrecionEntregaDTO agenciaDTO = new CrecionEntregaDTO();
      agenciaDTO.setWerks((String) row.get("WERKS"));
      agenciaDTO.setWerksDesc((String) row.get("NAME1"));
      agenciaDTO.setLifnr((String) row.get("LIFNR"));
      agenciaDTO.setLifnrDesc((String) row.get("NAME_LIFNR"));
      listAgenciasItemDTO.add(agenciaDTO);
    }

    resultDT.setId(listAgenciasItemDTO.size() > 0 ? 1 : 3);

    agenciasItemDTO.setItem(listAgenciasItemDTO);
    agenciasDTO.setItems(agenciasItemDTO);

    agenciasDTO.setResultDT(resultDT);
    agenciasDTO.setItems(agenciasItemDTO);
    return agenciasDTO;
  }

  @Override
  public CreacionEntregasDTO obtieneTransportesDAO(CrecionEntregaDTO transporteDTO) {
    CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();
    ResultDTO resultDT = new ResultDTO();
    CreacionEntregaItemDTO materialItemDTO = new CreacionEntregaItemDTO();
    List<CrecionEntregaDTO> listMaterialDTOs = new ArrayList<>();

    String sql = String.format(
        "SELECT VBELN, ORIGEN, NOMBRE_ORIGEN, DESTINO, NOMBRE_DESTINO, TRANSPORTISTA FROM VS_BCPS_ENTREGAS_TRANSPORTES WHERE ORIGEN = '%s' AND DESTINO = '%s';",
        transporteDTO.getWerks(),
        transporteDTO.getWerksD());

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      CrecionEntregaDTO materialDTO = new CrecionEntregaDTO();
      materialDTO.setTknum((String) row.get("VBELN"));
      materialDTO.setTknumTransport((String) row.get("TRANSPORTISTA"));
      materialDTO.setWerks((String) row.get("ORIGEN"));
      materialDTO.setWerksDesc((String) row.get("NOMBRE_ORIGEN"));
      materialDTO.setWerksD((String) row.get("DESTINO"));
      materialDTO.setWerksDDesc((String) row.get("NOMBRE_DESTINO"));
      listMaterialDTOs.add(materialDTO);
    }

    resultDT.setId(listMaterialDTOs.size() > 0 ? 1 : 3);
    materialItemDTO.setItem(listMaterialDTOs);
    materialesDTO.setItems(materialItemDTO);
    materialesDTO.setResultDT(resultDT);

    return materialesDTO;
  }

  @Override
  public ResultDTO creacionEntregaBCPS(CrecionEntregaDTO item, String user) {
    ResultDTO result = new ResultDTO();
    result.setId(0);

    String sql = "exec sp_bcps_generate_entregas ?, ?, ?, ?, ?, ?;";
    Object[] parametros = { item.getWerks(), item.getWerksD(), user, item.getLifnr(), java.sql.Types.VARCHAR,
        java.sql.Types.INTEGER };

    jdbcTemplate.update(sql, parametros, (RowMapper<String>) (rs, rowNum) -> {
      int id = rs.getInt(6);
      log.info("id: " + id);
      result.setId(id);
      result.setMsg(rs.getString(5));
      return null;
    });

    return result;
  }

  @Override
  public ResultDTO creacionLipsZcontingenciaEntregaBCPS(CrecionEntregaDTO item, String user, String entrega) {

    String sql = "exec SP_BCPS_ENTREGA_POSICIONES ?, ?, ?, ?, ?, ?, ? ,? ,? , ?, ?, ?, ?, ?";
    Object[] parametros = {
        entrega,
        item.getPos(),
        user,
        item.getMatnr(),
        item.getWerks(),
        "LV01",
        item.getLfimg(),
        item.getUnidadMedida(),
        item.getMaktx(),
        item.getQytHus(),
        item.getTknum(),
        item.getLifnr(),
        item.getWerksD(),
        java.sql.Types.INTEGER
    };

    ResultDTO result = new ResultDTO();
    result.setId(0);

    jdbcTemplate.update(sql, parametros, (RowMapper<String>) (rs, rowNum) -> {
      int id = rs.getInt(6);
      log.info("id: " + id);
      result.setId(id);
      result.setMsg(rs.getString(5));
      return null;
    });

    return result;
  }

  @Override
  public ResultDTO updateVTTP(String transporte, String entrega, String werks, String user) {
    ResultDTO result = new ResultDTO();
    result.setId(0);

    String sql = "exec SP_BCPS_ALTER_TRANSPORTE ?, ?, ?, ?, ?, ?";
    Object[] parametros = {
        transporte,
        entrega,
        1,
        werks,
        user,
        java.sql.Types.INTEGER
    };

    jdbcTemplate.update(sql, parametros, (RowMapper<String>) (rs, rowNum) -> {
      int id = rs.getInt(6);
      log.info("id: " + id);
      result.setId(id);
      result.setMsg(rs.getString(5));
      return null;
    });

    return result;
  }

  @Override
  public CreacionEntregasDTO getEntregas() {
    ResultDTO result = new ResultDTO();
    CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();

    ArrayList<CrecionEntregaDTO> entregasDTO = new ArrayList<>();
    CreacionEntregaItemDTO items = new CreacionEntregaItemDTO();

    String sql = "SELECT ENTREGA,VTTP.TKNUM,LIKP.LIFNR,LFA1.WERKS,LFA1.NAME1,LFA1.NAME2,ZCENTROSBCPS.WERKS as WERKSD,ZCENTROSBCPS.DESCRIPCION FROM ZCONTINGENCIA ZCONTINGENCIA "
        + "INNER JOIN VTTP VTTP ON VTTP.VBELN= ZCONTINGENCIA.ENTREGA  "
        + "LEFT JOIN LIKP LIKP ON ZCONTINGENCIA.ENTREGA= LIKP.VBELN "
        + "LEFT JOIN LFA1 LFA1 ON LIKP.LIFNR= LFA1.LIFNR "
        + "LEFT JOIN ZCENTROSBCPS ZCENTROSBCPS  ON ZCENTROSBCPS.KUNNR = LIKP.KUNNR "
        + "WHERE IDPROC='31'";

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      CrecionEntregaDTO entrega = new CrecionEntregaDTO();
      entrega.setTknum((String) row.get("TKNUM"));
      entrega.setVbeln((String) row.get("ENTREGA"));
      entrega.setWerks((String) row.get("WERKS"));
      entrega.setLifnr((String) row.get("LIFNR"));
      entrega.setWerksDesc(row.get("NAME1") + " " + row.get("NAME2"));
      entrega.setWerksD((String) row.get("WERKSD"));
      entrega.setWerksDDesc((String) row.get("DESCRIPCION"));
      entregasDTO.add(entrega);
    }

    items.setItem(entregasDTO);
    materialesDTO.setItems(items);

    if (entregasDTO.size() > 0) {
      result.setId(1);
      result.setMsg("Entregas encontradas");
    } else {
      result.setId(2);
      result.setMsg("Aun no se han generado entregas por CSC Logística en el sistema BCPS");
    }

    materialesDTO.setResultDT(result);

    return materialesDTO;
  }

  @Override
  public CreacionEntregasDTO getEntrega(String vbeln) {
    ResultDTO result = new ResultDTO();
    CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();

    List<CrecionEntregaDTO> entregasDTO = new ArrayList<>();
    CreacionEntregaItemDTO items = new CreacionEntregaItemDTO();

    String get_data_pos_1 = String
        .format("select POSNR, MATNR, ARKTX , LFIMG  from LIPS with(nolock) where VBELN= '%s';", vbeln);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(get_data_pos_1);
    for (Map<String, Object> row : rows) {

      CrecionEntregaDTO entrega = new CrecionEntregaDTO();
      entrega.setPos((String) row.get("POSNR"));
      entrega.setMatnr((String) row.get("MATNR"));
      entrega.setMaktx((String) row.get("ARKTX"));
      entrega.setLfimg((String) row.get("LFIMG"));

      try {
        String get_data_pos_2 = String.format("select top 1 VEKP.VHILM,MAKT.MAKTX  from VEKP  VEKP "
            + "inner join VEPO VEPO on VEKP.VENUM = VEPO.VENUM "
            + "inner join MAKT MAKT on VEKP.VHILM=MAKT.MATNR "
            + "where VEPO.MATNR= '%s' and VEKP.VPOBJKEY= '%s';", entrega.getMatnr(), vbeln);
        Map<String, Object> row_query_2 = jdbcTemplate.queryForMap(get_data_pos_2);
        entrega.setPacknr((String) row_query_2.get("VHILM"));
        entrega.setPacknrTxt((String) row_query_2.get("MAKTX"));
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        String get_data_pos_tot = String.format("select count(*) as totalHU from VEPO VEPO "
            + "inner join VEKP VEKP on VEKP.VENUM=VEPO.VENUM "
            + "where MATNR= '%s' and VEKP.VPOBJKEY= '%s';", entrega.getMatnr(), vbeln);
        Map<String, Object> row_query_3 = jdbcTemplate.queryForMap(get_data_pos_tot);
        entrega.setQytHus((String) row_query_3.get("totalHU"));
      } catch (Exception e) {
        e.printStackTrace();
      }

      entregasDTO.add(entrega);
    }

    items.setItem(entregasDTO);
    materialesDTO.setItems(items);

    if (entregasDTO.size() > 0) {
      result.setId(1);
      result.setMsg("Entrega encontrada");
    } else {
      result.setId(2);
      result.setMsg("No se encontro el detalle de la entrega solicitada: " + vbeln);
    }

    materialesDTO.setResultDT(result);
    return materialesDTO;
  }

  @Override
  public ResultDTO eliminarEntrega(String vbeln, String transporte, String werks, String usuario) {
    ResultDTO result = new ResultDTO();
    result.setId(0);

    String sql = "exec SP_BCPS_ALTER_TRANSPORTE ?, ?, ?, ?,?,?";
    Object[] parametros = {
        transporte,
        vbeln,
        2,
        werks,
        usuario,
        java.sql.Types.INTEGER
    };

    jdbcTemplate.update(sql, parametros, (RowMapper<String>) (rs, rowNum) -> {
      int id = rs.getInt(6);
      log.info("id: " + id);
      result.setId(id);
      result.setMsg(id == 1 ? "Entrega eliminada" : "Error al eliminar la entrega");
      return null;
    });

    return result;
  }
}
