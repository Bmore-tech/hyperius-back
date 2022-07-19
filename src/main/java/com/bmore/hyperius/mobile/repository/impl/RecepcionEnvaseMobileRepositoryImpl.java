package com.bmore.hyperius.mobile.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.mobile.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.mobile.dto.EntregaDetalleDTO;
import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.repository.RecepcionEnvaseMobileRepository;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@Repository
public class RecepcionEnvaseMobileRepositoryImpl implements RecepcionEnvaseMobileRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String getWerks(String Hu) {
    ResultSet rs = null;
    String Werks = "";
    String query = "SELECT werks FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV= ?";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, Hu);
      rs = callableStatement.executeQuery();
      while (rs.next()) {
        Werks = rs.getString("werks");
      }

    } catch (SQLException e) {
      log.error(e.toString());
    }
    return Werks;
  }

  @Override
  public EntregaInput validarEntregaPickin(String entrega) {
    String query = "SELECT DISTINCT(MATNR) FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ?";
    EntregaInput entregaInput = new EntregaInput();
    ResultDT result = new ResultDT();
    HashMap<String, String> map = new HashMap<String, String>();
    ResultSet rs = null;
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entrega);
      rs = callableStatement.executeQuery();
      int cont = 0;
      while (rs.next()) {
        cont++;
        log.info(rs.getString("MATNR"));
        // map.put("MATNR",rs.getString("MATNR"));
        map.put(rs.getString("MATNR"), "MATNR");
        result.setId(1);
        result.setMsg("Entrega en picking");
      }
      if (cont == 0) {
        result.setId(2);
        result.setMsg("No disponible para pickign, acuda con supervisor.");
      }

    } catch (SQLException e) {
      result.setId(2);
      result.setMsg(e.getMessage());
    }

    log.info("validarEntregaPickin id: " + result.getId());
    entregaInput.setMateriales(map);
    entregaInput.setResultDT(result);
    return entregaInput;
  }

  @Override
  public int getFaltantes(String entrega) {
    int x = 999999;
    ResultSet rs = null;
    String query = "SELECT count(*) FROM "
        + "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ? AND Status is null AND EXIDV is null";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entrega);
      rs = callableStatement.executeQuery();
      if (rs.next()) {
        if (rs.getInt(1) > 0) {
          x = rs.getInt(1);
        } else {
          x = 0;
        }
      } else {
        x = 999999;
      }

    } catch (SQLException e) {
      x = 999999;
    }
    return x;
  }

  @Override
  public ResultDT getVBELNFromHuSAP(String hu, String werks) {
    String query = "SELECT VEPO.VBELN, LIKP.LFART FROM HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
        + "on VEPO.VENUM = VEKP.VENUM AND VEKP.EXIDV=? AND VEPO.VELIN='1' "
        + "inner join LIKP LIKP on VEPO.VBELN= LIKP.VBELN";

    ResultDT resultDT = new ResultDT();
    ResultSet rs = null;
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {
        resultDT.setTypeS(rs.getString("VBELN"));
        resultDT.setId(1);
        resultDT.setMsg(rs.getString("LFART"));
      } else {
        resultDT.setId(0);
        resultDT.setMsg("Entrega no encontrada vía HU y con centro de montacarguista: " + werks);
      }

    } catch (SQLException e) {
      resultDT.setId(200);
      resultDT.setMsg(e.getMessage());
    }
    log.info("getVBELNFromHuSAP id: " + resultDT.getId());
    return resultDT;
  }

  @Override
  public ResultDT getVBELNFromHuBCPS(String hu, String werks) {
    ResultDT resultDT = new ResultDT();
    ResultSet rs = null;
    String query = "SELECT ENTREGA as VBELN FROM zContingencia zCon WITH(NOLOCK) "
        + " inner join HCMDB.dbo.LIKP LIKP WITH(NOLOCK) "
        + "on zCon.ENTREGA = LIKP.VBELN AND LIKP.KUNNR = (SELECT KUNNR FROM zCentrosBCPS WITH(NOLOCK)"
        + "WHERE werks = ? ) AND zCon.HU= ? AND (zCon.IDPROC=28 or zCon.IDPROC=8)";
    log.info("entra");
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, werks);
      callableStatement.setString(2, hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {

        resultDT.setTypeS(rs.getString("VBELN"));

        resultDT.setId(1);
        resultDT.setMsg("Entrega encontrada");

      } else {
        resultDT.setId(2);
        resultDT.setMsg("Entrega no encontrada vÃ­a HU y con centro de montacarguista: " + werks);
      }

    } catch (SQLException e) {
      resultDT.setId(200);
      resultDT.setMsg(e.getMessage());
    }

    log.info("getVBELNFromHuBCPS id: " + resultDT.getId());
    return resultDT;
  }

  @Override
  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas) {
    String query = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
        + "set EXIDV = ?, usuarioMontacarga = ? WHERE VBELN = ? AND MATNR = ?  AND  Status is null AND EXIDV is null AND idProceso=1";
    ResultDT result = new ResultDT();
    Object[] args = { hu, usuarioMontacargas, entrega, matnr };

    if (jdbcTemplate.update(query, args) > 0) {
      result.setId(1);
      result.setMsg("HU reservado");
    } else {
      result.setId(0);
      result.setMsg("No fue posible reservar HU");
    }
    log.info("reservarCarrilHU id: " + result.getId());
    return result;

  }

  @Override
  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas, String lgtyp,
      String lgpla) {
    String query = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
        + "set EXIDV = ?, usuarioMontacarga = ? WHERE VBELN = ? AND MATNR = ? AND LGTYP = ? AND LGPLA = ?  AND  Status is null AND EXIDV is null AND idProceso=1";

    ResultDT result = new ResultDT();

    Object[] args = { hu, usuarioMontacargas, entrega, matnr, lgtyp, lgpla };

    if (jdbcTemplate.update(query, args) > 0) {
      result.setId(1);
      result.setMsg("HU reservado");
    } else {
      result.setId(0);
      result.setMsg("No fue posible reservar HU");
    }

    log.info("reservarCarrilHU id: " + result.getId());
    return result;

  }

  @Override
  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) {
    String query = "SELECT LGNUM, LGTYP, LGPLA, STATUS FROM "
        + " HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN=? AND EXIDV=? AND idProceso=1";
    ResultDT resultDT = new ResultDT();
    ResultSet rs = null;
    CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, vbeln);
      callableStatement.setString(2, hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {

        carrilUbicacionDTO.setLGNUM(rs.getString("LGNUM"));
        carrilUbicacionDTO.setLGTYP(rs.getString("LGTYP"));
        carrilUbicacionDTO.setLGPLA(rs.getString("LGPLA"));

        resultDT.setId(1);
        resultDT.setMsg("HU1 encontrada");
        resultDT.setTypeS(rs.getString("STATUS"));

      } else {
        resultDT.setId(0);
        resultDT.setMsg("El HU no esta diponible para pickear");
      }

    } catch (SQLException e) {
      resultDT.setId(200);
      resultDT.setMsg(e.getMessage());
    }
    carrilUbicacionDTO.setResultDT(resultDT);

    log.info("consultReservaCarrilHu id: " + resultDT.getId());
    return carrilUbicacionDTO;
  }

  @Override
  public EntregaDetalleDTO getDataHU(String hu) {
    String query = "SELECT vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH FROM "
        + "HCMDB.dbo.MAKT MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)"
        + " on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
        + "VEKP.venum = VEPO.venum WHERE VEKP.EXIDV=? AND VEPO.VELIN ='1'";
    ResultDT resultDT = new ResultDT();
    ResultSet resultado = null;
    EntregaDetalleDTO entrega = new EntregaDetalleDTO();
    log.info("getDataHU");
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      resultado = callableStatement.executeQuery();

      if (resultado.next()) {

        resultDT.setId(1);
        resultDT.setMsg("Material encontrado");

        entrega.setMaterial(resultado.getString("matnr"));
        entrega.setDescripcion(resultado.getString("maktx"));
        entrega.setCajas(resultado.getString("vemng"));
        entrega.setMe(resultado.getString("VEMEH"));
      } else {
        resultDT.setId(2);
        resultDT.setMsg("Material no encontrado en la entrega entrante.");
      }

    } catch (SQLException e) {
      resultDT.setId(200);
      resultDT.setMsg(e.getMessage());
    }

    entrega.setResultDT(resultDT);
    log.info("getDataHU id: " + resultDT.getId());
    return entrega;
  }

  @Override
  public EntregaDetalleDTO getDataHU_LQUA(String hu) {
    String query = "SELECT LQUA.MATNR AS MATNR, MAKT.MAKTX AS MAKTX, LQUA.VERME AS VEMNG, MEINS AS VEMEH FROM LQUA LQUA "
        + "INNER JOIN MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR " + "WHERE LENUM = ?";

    ResultDT result = new ResultDT();
    EntregaDetalleDTO entrega = new EntregaDetalleDTO();
    ResultSet rs = null;

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      log.error("HU: " + hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {

        result.setId(1);
        result.setMsg("Material encontrado");

        entrega.setMaterial(rs.getString("matnr"));
        entrega.setDescripcion(rs.getString("maktx"));
        entrega.setCajas(rs.getString("vemng"));
        entrega.setMe(rs.getString("VEMEH"));
      } else {
        result.setId(2);
        result.setMsg("Material no encontrado en la entrega entrante.");
      }

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }

    entrega.setResultDT(result);

    log.info("getDataHU_LQUA id: " + result.getId());
    return entrega;
  }

  @Override
  public ResultDT confirmaHusEnCarrill(EntregaInput entregaEntranteInput) {
    ResultDT result = new ResultDT();
    result.setId(0);
    String query = "exec sp_bcps_wm_consume_hus_recepcion ?,?,?,?,?,?,?,?,?,?,?,?";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      log.info(entregaEntranteInput.getHu1() + "-" + entregaEntranteInput.getHu2() + "-"
          + entregaEntranteInput.getUsuarioMontacarga() + "-" + entregaEntranteInput.getEntrega() + "-"
          + entregaEntranteInput.getMatnr() + "-" + entregaEntranteInput.getWerks() + "-"
          + entregaEntranteInput.getLgort() + "-" + entregaEntranteInput.getuDestino0() + "-"
          + entregaEntranteInput.getuDestino1() + "-" + entregaEntranteInput.getuDestino2() + "-"
          + entregaEntranteInput.getLfart());
      callableStatement.setString(1, entregaEntranteInput.getHu1());
      callableStatement.setString(2, entregaEntranteInput.getHu2());
      callableStatement.setString(3, entregaEntranteInput.getUsuarioMontacarga());
      callableStatement.setString(4, Utils.zeroFill(entregaEntranteInput.getEntrega(), 10));
      callableStatement.setString(5, entregaEntranteInput.getMatnr());
      callableStatement.setString(6, entregaEntranteInput.getWerks());
      callableStatement.setString(7, entregaEntranteInput.getLgort());
      callableStatement.setString(8, entregaEntranteInput.getuDestino0());
      callableStatement.setString(9, entregaEntranteInput.getuDestino1());
      callableStatement.setString(10, entregaEntranteInput.getuDestino2());
      callableStatement.setString(11, entregaEntranteInput.getLfart());
      callableStatement.registerOutParameter(12, java.sql.Types.INTEGER);
      callableStatement.execute();

      int id = 0;
      log.info("AFTER EXECUTE: " + id);
      id = callableStatement.getInt(12);
      result.setId(id);
      log.info("AFTER EXECUTE2: " + id);

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }
    log.info("confirmaHusEnCarrill id: " + result.getId());
    return result;
  }

  @Override
  public ResultDT limpiaPendientesXUsuario(String vbeln, String user) {
    ResultDT result = new ResultDT();
    String query = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
        + "WHERE VBELN = ? AND Status is null AND usuarioMontacarga = ? AND idProceso='1'";
    Object[] args = { vbeln, user };

    if (jdbcTemplate.update(query, args) > 0) {
      result.setId(1);
      result.setMsg("Limpieza ejecutada con exito");
      log.info(" > 0 Limpieza ejecutada con exito");
    } else {
      result.setId(1);
      result.setMsg("Limpieza ejecutada con exito");
      log.info(" <= 0 Limpieza ejecutada con exito");
    }
    log.info("limpiaPendientesXUsuario id: " + result.getId());
    return result;

  }

  @Override
  public ResultDT validaPickeoPrevioHU(EntregaInput entregaInput, String hu) {
    ResultDT resultDT = new ResultDT();
    ResultSet resultado = null;
    String query = "SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV = ? AND idProceso='1' AND Status='X'";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      resultado = callableStatement.executeQuery();

      if (resultado.next()) {
        resultDT.setId(2);
        resultDT.setMsg("HU consumido previamente");
      } else {
        resultDT.setId(1);
        resultDT.setMsg("HU con libre utilización");
      }

    } catch (SQLException e) {
      resultDT.setId(200);
      resultDT.setMsg(e.getMessage());
    }
    log.info("validaPickeoPrevioHU id: " + resultDT.getId());
    return resultDT;
  }

}
