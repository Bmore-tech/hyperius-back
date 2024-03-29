package com.bmore.hyperius.mobile.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bmore.hyperius.config.DBConnectionMob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UtilsMob {

  @Autowired
  private DBConnectionMob connectionMob;

  static String JAVA_SCRITP_FILE = "script_1.0.31.js";
  static String LIMPIA_CARRIL = "exec sp_bcps_wm_actualizaInventario ?, ?, ?";

  public static String zeroFill(String entry, int tamaño) {
    String tChart = entry;
    try {
      for (int i = entry.length(); i < tamaño; i++) {
        tChart = "0" + tChart;
      }
    } catch (Exception e) {
      log.error(e.toString());
    }
    return tChart;
  }

  public static String zeroClean(String number) {
    try {
      number = new BigDecimal(number).intValue() + "";
    } catch (Exception e) {
      // NA
    }
    return number;
  }

  public ResultDT actualizarInventarioCarriles(String LGNUM, String LGTYP, String LGPLA) throws ClassNotFoundException {
    ResultDT result = new ResultDT();
    Connection con = connectionMob.createConnection();
    PreparedStatement stmn = null;
    try {
      stmn = con.prepareStatement(LIMPIA_CARRIL);
      stmn.setString(1, LGNUM);
      stmn.setString(2, LGTYP);
      stmn.setString(3, LGPLA);
      int star = stmn.executeUpdate();
      if (star > 0) {
        result.setId(1);
        result.setMsg("Ejecutado Correctamente");
      } else {
        result.setId(1);
        result.setMsg("Ejecutado Correctamente");
      }
    } catch (SQLException e) {
      result.setId(2);
      result.setMsg(e.getMessage());
      log.error("Error SQLException al ejecutar store procedure LIMPIA_CARRIL: " + e.toString());
    } catch (Exception en) {
      result.setId(2);
      result.setMsg(en.getMessage());
      log.error("Error Exception al ejecutar store procedure LIMPIA_CARRIL: " + en.toString());
    } finally {
      try {
        DBConnectionMob.closeConnection(con);
      } catch (Exception e) {
        result.setId(2);
        result.setMsg(e.getMessage());
        log.error("Error al ejecutar store procedure LIMPIA_CARRIL: " + e.toString());
      }
    }
    return result;
  }

  public static String returnJS() {
    return JAVA_SCRITP_FILE;
  }

  public static String getKeyTimeStamp() {
    java.util.Date date = new java.util.Date();
    String timeStamp = new Timestamp(date.getTime()) + "";
    return (timeStamp.replaceAll(":", "_").replaceAll(" ", "").replaceAll("-", "_"));
  }
}
