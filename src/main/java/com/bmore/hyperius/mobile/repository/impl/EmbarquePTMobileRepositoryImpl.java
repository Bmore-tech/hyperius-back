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

import com.bmore.hyperius.mobile.dto.EntregaDetalleDTO;
import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.repository.EmbarquePTMobileRepository;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@Repository
public class EmbarquePTMobileRepositoryImpl implements EmbarquePTMobileRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public EntregaInput validarEntregaPickin(EntregaInput entregaInput) {

    EntregaInput entregaInputReturn = new EntregaInput();
    ResultDT result = new ResultDT();
    ResultSet rs = null;
    HashMap<String, String> hashhMap = new HashMap<String, String>();
    String query = "SELECT DISTINCT (VBELN),MATNR FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE VBELN = ? AND WERKS = ?";
    log.info("Werks: " + entregaInput.getWerks() + "\n" + "entrega: " + entregaInput.getEntrega());

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
        log.info("Query valida_pick matnr: " + rs.getString("MATNR"));
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
  public EntregaInput reservaUbicaciones(EntregaInput entregaInput) {
    String query = "exec sp_bcps_wm_reserva_espacio_hu ?,?,?,?,?,?,?,?,?";
    ResultDT result = new ResultDT();
    result.setId(0);

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entregaInput.getWerks());
      callableStatement.setString(2, entregaInput.getUsuarioMontacarga());
      callableStatement.setString(3, entregaInput.getEntrega());
      callableStatement.setString(4, "4");
      callableStatement.registerOutParameter(5, java.sql.Types.INTEGER);
      callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
      callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
      callableStatement.execute();
      int id = 0;
      id = callableStatement.getInt(5);
      result.setId(id);
      log.info("id" + id);
      entregaInput.setMatnr(Utils.zeroClean(callableStatement.getString(6)));
      entregaInput.setuOrigen0(callableStatement.getString(7));
      entregaInput.setuOrigen1(callableStatement.getString(8));
      entregaInput.setuOrigen2(callableStatement.getString(9));

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }
    entregaInput.setResultDT(result);
    log.info("reservaUbicaciones id: " + result.getId());
    return entregaInput;
  }

  @Override
  public ResultDT validaPickeoPrevioHU(EntregaInput entregaInput, String hu) {
    ResultDT result = new ResultDT();
    ResultSet rs = null;
    String query = "SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV = ? AND idProceso='4'";
    log.info("Werks: " + entregaInput.getWerks() + "\n" + "entrega: " + entregaInput.getEntrega());

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      rs = callableStatement.executeQuery();

      if (rs.next()) {
        result.setId(2);
        result.setMsg("El HU ya fue consumido");
      } else {
        result.setId(1);
        result.setMsg("HU sin confirmar");
      }

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }

    log.info("validaPickeoPrevioHU id: " + result.getId());
    return result;
  }

  @Override
  public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {
    ResultDT result = new ResultDT();
    EntregaDetalleDTO entrega = new EntregaDetalleDTO();
    String query = "SELECT LQUA.MATNR AS matnr FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) WHERE LENUM =  ? AND WERKS = ? AND LGTYP = ? AND LGPLA = ?";
    String query2 = "SELECT LQUA.MATNR AS matnr, LQUA.VERME AS vemng, LQUA.MEINS AS meins, MAKT.MAKTX AS maktx, BESTQ AS BESTQ"
        + " FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR "
        + " WHERE LENUM =  ? AND WERKS = ? AND LGTYP = ? AND LGPLA = ?";
    ResultSet rs = null;
    ResultSet rs2 = null;
    log.info("hu: " + hu + "\n" + "werks: " + werks + "\n" + "lgtyp: " + lgtyp + "\n" + "lgpla: " + lgpla + "\n");

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      callableStatement.setString(2, werks);
      callableStatement.setString(3, lgtyp);
      callableStatement.setString(4, lgpla);
      rs = callableStatement.executeQuery();

      if (rs.next()) {
        log.info("Entro al primer query");
        CallableStatement callableStatement2 = null;
        callableStatement2 = con.prepareCall(query2);
        callableStatement2.setString(1, hu);
        callableStatement2.setString(2, werks);
        callableStatement2.setString(3, lgtyp);
        callableStatement2.setString(4, lgpla);
        rs2 = callableStatement2.executeQuery();
        if (rs2.next()) {
          log.info("Entro al segundo query");
          result.setId(1);
          result.setMsg("Material encontrado");
          entrega.setMaterial(rs2.getString("matnr"));
          entrega.setDescripcion(rs2.getString("maktx"));
          entrega.setCajas(rs2.getString("vemng"));
          entrega.setMe(rs2.getString("meins"));
          entrega.setBestq(rs2.getString("bestq"));
          log.info("bestq: " + rs2.getString("bestq"));
        } else {
          result.setId(2);
          result.setMsg("Material no encontrado.");
        }
      } else {
        result.setId(2);
        result.setMsg("El HU no existe o no pertenece a la ubicación.");
      }

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }

    entrega.setResultDT(result);
    log.info("getDataHU id: " + result.getId());
    return entrega;

  }

  // Error
  @Override
  public ResultDT confirmaHusEnCamionFurgon(EntregaInput entregaInput) {
    ResultDT result = new ResultDT();
    result.setId(0);
    String query = "exec sp_bcps_wm_consume_hus_embarque ?,?,?,?,?,?,?,?,?,?,?";
    log.info(entregaInput.getHu1() + "-" + entregaInput.getHu2() + "-" + entregaInput.getUsuarioMontacarga() + "-"
        + entregaInput.getEntrega() + "-" + entregaInput.getMatnr() + "-" + entregaInput.getWerks() + "-"
        + entregaInput.getLgort() + "-" + entregaInput.getuOrigen0() + "-" + entregaInput.getuOrigen1() + "-"
        + entregaInput.getuOrigen2() + "-" + entregaInput.getLfart());

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);

      callableStatement.setString(1, entregaInput.getHu1());
      callableStatement.setString(2, entregaInput.getHu2());
      callableStatement.setString(3, entregaInput.getUsuarioMontacarga());
      callableStatement.setString(4, Utils.zeroFill(entregaInput.getEntrega(), 10));
      callableStatement.setString(5, Utils.zeroFill(entregaInput.getMatnr(), 18));
      callableStatement.setString(6, entregaInput.getWerks());
      callableStatement.setString(7, "");
      callableStatement.setString(8, entregaInput.getuOrigen0());
      callableStatement.setString(9, entregaInput.getuOrigen1());
      callableStatement.setString(10, entregaInput.getuOrigen2());
      callableStatement.registerOutParameter(11, java.sql.Types.INTEGER);

      callableStatement.execute();
      int id = 0;
      log.error("AFTER EXECUTE: " + id);
      id = callableStatement.getInt(11);
      result.setId(id);
      log.error("AFTER EXECUTE2: " + id);

    } catch (SQLException e) {
      result.setId(200);
      result.setMsg(e.getMessage());
    }

    log.info("confirmaHusEnCamionFurgon id: " + result.getId());
    return result;
  }

  @Override
  public ResultDT limpiaPendientesXUsuario(String vbeln, String user) {
    ResultDT result = new ResultDT();
    String query = "UPDATE HCMDB.dbo.ZPickingEntregaEntrante SET status = null, usuarioMontacarga = null  WHERE VBELN = ? AND Status = '1' AND usuarioMontacarga = ? ";
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

  @Override
  public String getWerks(String entrega) {
    // TODO Auto-generated method stub
    return null;
  }

}
