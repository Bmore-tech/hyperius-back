package com.bmore.hyperius.mobile.repository.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.repository.EmbarquePTIMMobileRepository;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@Repository
public class EmbarquePTIMMobileRepositoryImpl implements EmbarquePTIMMobileRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public EntregaInput validarEntregaPickin(EntregaInput entregaInput) {

    EntregaInput entregaInputReturn = new EntregaInput();
    ResultDT result = new ResultDT();
    ResultSet rs = null;
    HashMap<String, String> hashhMap = new HashMap<String, String>();
    String query = "select distinct (VBELN),MATNR from HCMDB.dbo.ZPickingVidrio where VBELN = ? and WERKS = ?";
    log.info("validarEntregaPickin...");

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, Utils.zeroFill(entregaInput.getEntrega(), 10));
      callableStatement.setString(2, entregaInput.getWerks());
      rs = callableStatement.executeQuery();

      int cont = 0;

      while (rs.next()) {
        cont++;
        // Se caza a que solo es un material en la orden de produccion
        // de otra manera esto no funcionara

        hashhMap.put(rs.getString("MATNR"), rs.getString("MATNR"));
        result.setId(1);
        result.setMsg("Entrega saliente ya se encuentra en picking");
        log.info("Entrega saliente ya se encuentra en picking");
      }

      if (cont == 0) {
        result.setId(0);
        result.setMsg("Entrega saliente no disponible para picking");
        log.info("Entrega saliente no disponible para picking");
      }
    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }

    entregaInputReturn.setResultDT(result);
    entregaInputReturn.setMateriales(hashhMap);
    return entregaInputReturn;
  }

  @Override
  public ResultDT reservaHus(EntregaInput entregaInput) {
    ResultDT result = new ResultDT();
    result.setId(0);

    String query = "exec sp_bcps_im_embarquePT_reserva_hu ?, ?,?,?, ?, ?, ?,?,?,?";

    log.info("reservaHus: " + result.getId());
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entregaInput.getWerks());
      callableStatement.setString(2, entregaInput.getUsuarioMontacarga());
      callableStatement.setString(3, entregaInput.getEntrega());
      callableStatement.setString(4, "4");

      callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
      callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(10, java.sql.Types.VARCHAR);
      callableStatement.execute();

      int id = 0;

      String cantidad = "";
      String material = "";
      String cantidadTotal = "";
      String um = "";
      String cantidadPickeada = "";

      material = callableStatement.getString(5);
      id = callableStatement.getInt(6);
      cantidad = callableStatement.getString(7);
      cantidadTotal = callableStatement.getString(8);
      um = callableStatement.getString(9);
      cantidadPickeada = callableStatement.getString(10);

      log.error("id: " + id + " material:" + material + "  cantidadTotal: " + cantidadTotal
          + " cantidadPickeada: " + cantidadPickeada);
      result.setId(id);
      if (id == 1) {

        result.setTypeS(material);
        result.setTypeF(Float.parseFloat(cantidad));
        result.setMsg(um);
        BigDecimal cantidadTotalD = new BigDecimal("0.00");
        BigDecimal cantidadTotalZPickingD = new BigDecimal("0.00");

        try {

          cantidadTotalD = new BigDecimal(cantidadTotal).setScale(3, RoundingMode.HALF_UP);

          cantidadTotalZPickingD = new BigDecimal(cantidadPickeada).setScale(3, RoundingMode.HALF_UP);

          cantidadTotalD = cantidadTotalD.subtract(cantidadTotalZPickingD);

        } catch (Exception e) {

        }

        result.setTypeBD(cantidadTotalD);

      }
    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }
    return result;
  }

  @Override
  public ResultDT obtieneDescripcionMaterial(String matnr, String vblen) {

    String query = "select top (1) ARKTX from  HCMDB.dbo.LIPS where VBELN=? and MATNR=?";
    ResultDT result = new ResultDT();
    log.error("MATNR: " + matnr);
    log.error("Vblen: " + vblen);

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();

      ResultSet rs = null;
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);

      callableStatement.setString(1, vblen);
      callableStatement.setString(2, matnr);

      rs = callableStatement.executeQuery();

      if (rs.next()) {

        result.setId(1);
        result.setMsg("Material encontrado");
        result.setTypeS(rs.getString("ARKTX"));
        log.info(rs.getString("ARKTX"));

      } else {
        result.setId(2);
        result.setMsg("Material no encontrado");
        log.info("Material no encontrado");
      }
    } catch (SQLException e) {
      log.error("SQLException: " + e.toString());
      result.setId(200);
      result.setMsg(e.getMessage());
    }

    return result;
  }

  @Override
  public ResultDT consumeHUs(EntregaInput entregaInput) {
    String query = "exec sp_bcps_im_embarquePT_consume_hu ?, ?, ?,?, ?, ?, ?, ?";
    ResultDT result = new ResultDT();
    result.setId(0);

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entregaInput.getHu1());
      callableStatement.setString(2, entregaInput.getWerks());
      callableStatement.setString(3, Utils.zeroFill(entregaInput.getMatnr(), 18));
      callableStatement.setString(4, entregaInput.getCant());
      callableStatement.setString(5, entregaInput.getHu2());
      callableStatement.setString(6, entregaInput.getUsuarioMontacarga());
      callableStatement.setString(7, entregaInput.getEntrega());

      callableStatement.registerOutParameter(8, java.sql.Types.INTEGER);
      callableStatement.execute();

      int id = 0;

      id = callableStatement.getInt(8);
      result.setId(id);
      log.error("ID DE CONSUMIR HUS: " + id);

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }
    return result;

  }

  @Override
  public ResultDT limpiaPendientesXUsuario(String vbeln, String user) {
    ResultDT result = new ResultDT();
    String query = " updateHCMDB.dbo.ZPickingVidrio set status = null, usuarioMontacarga = null  where VBELN = ? and Status = '1' and usuarioMontacarga = ? ";

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
    return result;

  }

}
