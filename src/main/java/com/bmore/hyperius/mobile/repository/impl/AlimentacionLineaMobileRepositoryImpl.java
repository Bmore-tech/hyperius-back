package com.bmore.hyperius.mobile.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.repository.AlimentacionLineaMobileRepository;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@Repository
public class AlimentacionLineaMobileRepositoryImpl implements AlimentacionLineaMobileRepository {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  // No testeado
  @Override
  public String getLGPLA(String hu) {

    String query = "SELECT LGPLA FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) WHERE LENUM = ?";

    Object[] agrs = { hu };

    return jdbcTemplate.queryForObject(query, agrs, new RowMapper<String>() {

      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {

        return rs.getString("LGPLA");

      }

    });

  }

  @Override
  public ResultDT validaOrden(OrdenProduccionInput ordenProduccionInput) {
    String query = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
        + "where VBELN = ? and WERKS = ? and idProceso='2'";
    Object[] args = { ordenProduccionInput.getOrdeProduccion(), ordenProduccionInput.getWerks() };
    ResultDT result = new ResultDT();

    logger.info("Orden:" + ordenProduccionInput.getOrdeProduccion());
    logger.info("Werks:" + ordenProduccionInput.getWerks());

    result.setId(2);
    result.setMsg("Orden de producción no disponible para picking, contacte al supervisor");

    jdbcTemplate.query(query, args, new RowMapper<ResultDT>() {

      @Override
      public ResultDT mapRow(ResultSet rs, int rowNum) throws SQLException {
        // if(rs.next())
        // {
        result.setTypeS(rs.getString("MATNR"));
        result.setId(1);
        result.setMsg("Orden de producción en picking");
        // }
        return result;
      }
    });

    return result;
  }

  @Override
  public OrdenProduccionDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {
    String query1 = "select LQUA.MATNR as matnr from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
    String query2 = "select LQUA.MATNR as matnr, LQUA.VERME as vemng, LQUA.MEINS as meins, MAKT.MAKTX as maktx,BESTQ as BESTQ "
        + " from HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR "
        + " where LENUM =  ? and WERKS = ? and LGTYP = ? and LGPLA = ?";
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultDT result = new ResultDT();
    OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();

    logger.info("entra GetDataHU");

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      CallableStatement callableStatement2 = null;
      callableStatement = con.prepareCall(query1);
      callableStatement.setString(1, hu);
      callableStatement.setString(2, werks);
      callableStatement.setString(3, lgtyp);
      callableStatement.setString(4, lgpla);
      rs = callableStatement.executeQuery();
      logger.info("HU:" + hu + "\nWerks:" + werks + "\nlgtyp:" + lgtyp + "\nlgpla:" + lgpla);

      if (rs.next()) {
        logger.info("Primer Query");
        callableStatement2 = con.prepareCall(query2);
        callableStatement2.setString(1, hu);
        callableStatement2.setString(2, werks);
        callableStatement2.setString(3, lgtyp);
        callableStatement2.setString(4, lgpla);
        rs2 = callableStatement2.executeQuery();
        if (rs2.next()) {
          logger.info("Segundo Query");
          result.setId(1);
          result.setMsg("Material encontrado");
          orden.setMaterial(rs2.getString("matnr"));
          orden.setDescripcion(rs2.getString("maktx"));
          orden.setCajas(rs2.getString("vemng"));
          orden.setMe(rs2.getString("meins"));
          orden.setBestq(rs2.getString("bestq"));
        } else {
          result.setId(2);
          result.setMsg("Material no encontrado.");
        }
      } else {
        result.setId(2);
        result.setMsg("El HU no existe o no pertenece a la ubicación.");
      }
    } catch (SQLException e) {
      result.setId(2);
      result.setMsg(e.getMessage());
    }

    orden.setResultDT(result);
    return orden;

  }

  @Override
  public OrdenProduccionInput obtieneDepaletizadora(OrdenProduccionInput ordenProduccionInput) {
    String query = "select LGTYP,LGPLA  from HCMDB.dbo.RESB WITH(NOLOCK) where MATNR= ? and AUFNR = ?";
    ResultDT resultDT = new ResultDT();
    OrdenProduccionInput ordenProduccionInputReturn = new OrdenProduccionInput();
    CallableStatement stmn = null;
    ResultSet resultado = null;
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      stmn = con.prepareCall(query);
      stmn.setString(1, ordenProduccionInput.getMatnr());
      stmn.setString(2, ordenProduccionInput.getOrdeProduccion());
      resultado = stmn.executeQuery();
      if (resultado.next()) {
        ordenProduccionInputReturn.setuDestino2(resultado.getString("LGPLA"));
        resultDT.setId(1);
        resultDT.setMsg("DEPA encontrada");
      } else {
        resultDT.setId(2);
        resultDT.setMsg("Depaletizadora no encontrada, acuda con supervisor");
      }
    } catch (SQLException e) {
      resultDT.setId(2);
      resultDT.setMsg("Error SQL: " + e.toString());
    }
    ordenProduccionInputReturn.setResultDT(resultDT);
    return ordenProduccionInputReturn;
  }

  @Override
  public ResultDT confirmaHUsenDepa(OrdenProduccionInput ordenProduccionInput) {
    String query = "exec sp_bcps_wm_consume_hus_alimentacion ?,?,?,?,?,?,?,?,?,?,?";
    logger.info(
        "Parametros:" + ordenProduccionInput.getWerks() + " - " + ordenProduccionInput.getUsuarioMontacarga());
    ResultDT result = new ResultDT();
    result.setId(0);

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, ordenProduccionInput.getHu1());
      callableStatement.setString(2, ordenProduccionInput.getHu2());
      callableStatement.setString(3, ordenProduccionInput.getUsuarioMontacarga());
      callableStatement.setString(4, Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12));
      callableStatement.setString(5, Utils.zeroFill(ordenProduccionInput.getMatnr(), 18));
      callableStatement.setString(6, ordenProduccionInput.getWerks());
      callableStatement.setString(7, ordenProduccionInput.getLgort());
      callableStatement.setString(8, ordenProduccionInput.getuOrigen0());
      callableStatement.setString(9, ordenProduccionInput.getuOrigen1());
      callableStatement.setString(10, ordenProduccionInput.getuOrigen2());
      callableStatement.registerOutParameter(11, java.sql.Types.INTEGER);
      callableStatement.execute();

      int id = 0;
      logger.info("AFTER EXECUTE: " + id);
      id = callableStatement.getInt(11);
      result.setId(id);
      logger.info("AFTER EXECUTE2: " + id);
    } catch (SQLException e) {
      logger.error("error", e);
    }

    return result;
  }

  @Override
  public ResultDT validaPickeoPrevioHU(OrdenProduccionInput ordenProduccionInput, String hu) {

    logger.info("ValidaPickeoPrevio...");
    String query = "select EXIDV from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where EXIDV = ? and idProceso='2'";
    ResultDT result = new ResultDT();
    ResultSet rs = null;

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {
        result.setId(2);
        result.setMsg("HU consumido previamente");
        logger.info("HU consumido previamente");
      } else {
        result.setId(1);
        result.setMsg("HU con libre utilización");
        logger.info("HU con libre utilización");
      }

    } catch (SQLException e) {
      logger.error("error", e);
    }
    return result;
  }

  @Override
  public int limpiaPendientesXUsuario(String vbeln, String user) {

    String query = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
        + "where VBELN = ? and Status = '1' and usuarioMontacarga = ? and idProceso='2'";

    Object[] args = { vbeln, user };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public OrdenProduccionInput reservaUbicaciones(OrdenProduccionInput ordenProduccionInput) {
    String query = "exec sp_bcps_wm_reserva_espacio_hu ?,?,?,?,?,?,?,?,?";
    logger.info(
        "Parametros:" + ordenProduccionInput.getWerks() + " - " + ordenProduccionInput.getUsuarioMontacarga()
            + " - " + ordenProduccionInput.getOrdeProduccion() + " - " + 2);
    ResultDT result = new ResultDT();
    result.setId(0);

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, ordenProduccionInput.getWerks());
      callableStatement.setString(2, ordenProduccionInput.getUsuarioMontacarga());
      callableStatement.setString(3, ordenProduccionInput.getOrdeProduccion());
      callableStatement.setString(4, "2");
      callableStatement.registerOutParameter(5, java.sql.Types.INTEGER);
      callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
      callableStatement.execute();
      result.setId(callableStatement.getInt(5));

    } catch (SQLException e) {
      logger.error("error", e);

    }

    ordenProduccionInput.setResultDT(result);

    return ordenProduccionInput;
  }

  @Override
  public String getWerks(String ordProd) {

    String query = "SELECT DISTINCT(WERKS) FROM HCMDB.dbo.ZPickingEntregaEntrante where VBELN = ?";
    Object[] args = { ordProd };
    ResultDT result = new ResultDT();
    jdbcTemplate.query(query, args, new RowMapper<String>() {

      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {

        result.setMsg(rs.getString("WERKS"));
        return "";

      }
    });

    return result.getMsg();
  }

}
