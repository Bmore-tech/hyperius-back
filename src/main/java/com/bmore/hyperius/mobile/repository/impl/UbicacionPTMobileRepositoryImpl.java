package com.bmore.hyperius.mobile.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.mobile.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.repository.UbicacionPTMobileRepository;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@Repository
public class UbicacionPTMobileRepositoryImpl implements UbicacionPTMobileRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public OrdenProduccionDetalleDTO getDataHU(String hu) {
    ResultSet rs = null;
    ResultDT result = new ResultDT();
    OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();
    String query = "select vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH,VHILM from "
        + "HCMDB.dbo.MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
        + "VEKP.venum = VEPO.venum where VEKP.EXIDV=? and VEPO.VELIN ='1'";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      log.info("HU: " + hu);
      rs = callableStatement.executeQuery();
      if (rs.next()) {
        result.setId(1);
        result.setMsg("Material encontrado");
        orden.setMaterial(rs.getString("matnr"));
        orden.setDescripcion(rs.getString("maktx"));
        orden.setCajas(rs.getString("vemng"));
        orden.setMe(rs.getString("VEMEH"));
        orden.setTarima(rs.getString("VHILM"));
      } else {
        result.setId(2);
        result.setMsg("Material no encontrado en la orden de producción");
      }

    } catch (SQLException e) {
      log.error(e.toString());
    }
    log.info("GetDataHU id: " + result.getId());
    orden.setResultDT(result);
    return orden;
  }

  @Override
  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) {
    CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();
    ResultDT resultDT = new ResultDT();
    ResultSet rs = null;
    String query = "SELECT LGNUM, LGTYP, LGPLA, STATUS from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
        + "where VBELN=? and EXIDV=? and idProceso='3'";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      log.info("Consulta reserva carril");
      log.info("hu: " + hu);
      log.info("Vblen: " + vbeln);
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
        log.info("status: " + resultDT.getTypeS() + "\n" + "LGNUM: " + carrilUbicacionDTO.getLGNUM() + "\n"
            + "LGTYP: " + carrilUbicacionDTO.getLGTYP() + "\n" + "LGPLA: " + carrilUbicacionDTO.getLGPLA()
            + "\n");
      } else {
        resultDT.setId(0);
        resultDT.setMsg("El HU no esta diponible para pickear");
      }

    } catch (SQLException e) {
      resultDT.setId(2);
      resultDT.setMsg("Error SQL: " + e.toString());
    }
    log.info("consultReservaCarrilHu id: " + resultDT.getId());
    carrilUbicacionDTO.setResultDT(resultDT);
    return carrilUbicacionDTO;

  }

  @Override
  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacarga) {
    ResultDT result = new ResultDT();
    String query = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
        + "set EXIDV = ?,usuarioMontacarga = ? where VBELN = ? and MATNR = ? and  Status is null and EXIDV is null and idProceso='3'";
    Object[] args = { hu, usuarioMontacarga, entrega, matnr };

    if (jdbcTemplate.update(query, args) > 0) {
      result.setId(1);
      result.setMsg("LUGAR RESERVADO PARA HU");
    } else {
      result.setId(0);
      result.setMsg("No fue posible reservar HU");
    }
    log.info("reservarCarrilHU id: " + result.getId());
    return result;
  }

  @Override
  public ResultDT reservarCarrilHU(String entrega, String hu, String matnr, String lgtyp, String lgpla,
      String usuarioMontacarga) {
    ResultDT result = new ResultDT();
    String query = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
        + "set EXIDV = ?,usuarioMontacarga = ? where VBELN = ? and MATNR = ? and LGTYP = ? and LGPLA = ? and  Status is null and EXIDV is null and idProceso='3'";
    Object[] args = { hu, usuarioMontacarga, entrega, matnr, lgtyp, lgpla };

    if (jdbcTemplate.update(query, args) > 0) {
      result.setId(1);
      result.setMsg("LUGAR RESERVADO PARA HU");
    } else {
      result.setId(0);
      result.setMsg("No fue posible reservar HU");
    }
    log.info("reservarCarrilHU id: " + result.getId());
    return result;
  }

  @Override
  public int getFaltantes(String entry) {
    int x = 999999;
    ResultSet rs = null;
    String query = "select count(*) from "
        + "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and Status is null and idProceso='3'";

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entry);
      rs = callableStatement.executeQuery();
      if (rs.next()) {
        if (rs.getInt(1) >= 0) {
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
  public ResultDT getAUFNRFromHu(String hu, String werks) {
    String query = "SELECT VPOBJKEY from HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
        + "on VEPO.VENUM = VEKP.VENUM and VEKP.EXIDV= ? and VEPO.WERKS = ? and VEPO.VELIN='1'";
    ResultDT resultDT = new ResultDT();
    ResultSet rs = null;
    resultDT.setTypeS("");

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, hu);
      callableStatement.setString(2, werks);
      log.info("Werks daoUbicacionPT: " + werks + " hu: " + hu);
      rs = callableStatement.executeQuery();
      if (rs.next()) {
        resultDT.setTypeS(rs.getString("VPOBJKEY"));
        resultDT.setId(1);
        resultDT.setMsg("Orden de producción encontrada");
      } else {
        resultDT.setId(0);
        resultDT.setMsg("Orden de producción no encontrada ví­a HU y con centro de montacarguista: " + werks);
      }

    } catch (SQLException e) {
      resultDT.setId(2);
      resultDT.setMsg("Error SQL: " + e.toString());
    }
    log.info("getAUFNRFromHu id: " + resultDT.getId());
    return resultDT;

  }

  @Override
  public OrdenProduccionInput validarOrdenEnPickin(String entry) {
    log.info("entry: " + entry);
    OrdenProduccionInput orden = new OrdenProduccionInput();
    ResultDT result = new ResultDT();
    PreparedStatement stmn = null;
    ResultSet rs = null;
    HashMap<String, String> map = new HashMap<String, String>();
    String query = "select distinct(MATNR) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and idProceso='3'";

    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, entry);
      rs = callableStatement.executeQuery();
      int cont = 0;
      while (rs.next()) {
        cont++;
        map.put(rs.getString("MATNR"), "MATNR");
        result.setId(1);
        result.setMsg("Orden de producción ya se encuentra en picking");
      }
      if (cont == 0) {
        result.setId(2);
        result.setMsg("Orden de producción no disponible para picking");
      }

    } catch (SQLException e) {
      result.setId(2);
      result.setMsg("Error SQL: " + e.toString());
    }
    orden.setMateriales(map);
    orden.setResultDT(result);
    log.info("validarOrdenEnPickin id: " + result.getId());
    return orden;
  }

  @Override
  public ResultDT confirmaHusEnCarrill(OrdenProduccionInput ordenProduccionInput) {
    ResultDT result = new ResultDT();
    result.setId(0);
    String query = "exec sp_bcps_wm_consume_hus_ubicacion ?,?,?,?,?,?,?,?,?,?,?";
    log.error("CONSUME HUS");
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
      callableStatement.setString(7, "LV01");
      callableStatement.setString(8, ordenProduccionInput.getuDestino0());
      callableStatement.setString(9, ordenProduccionInput.getuDestino1());
      callableStatement.setString(10, ordenProduccionInput.getuDestino2());
      callableStatement.registerOutParameter(11, java.sql.Types.INTEGER);
      callableStatement.execute();
      int id = 0;
      log.error("AFTER EXECUTE: " + id);
      id = callableStatement.getInt(11);
      result.setId(id);
      log.error("AFTER EXECUTE2: " + id);
    } catch (SQLException e) {
      result.setId(2);
      result.setMsg("Error SQL: " + e.toString());
    }
    log.info("confirmaHusEnCarrill id: " + result.getId());
    return result;

  }

  @Override
  public ResultDT limpiaPendientesXUsuario(String vbeln, String user) {
    ResultDT result = new ResultDT();
    String query = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null,EXIDV = null, usuarioMontacarga = null  "
        + "where VBELN = ? and Status is null and usuarioMontacarga = ? and idProceso='3'";

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
  public String getWerks(String Hu) {
    ResultSet rs = null;
    String werks = "";
    log.info("Antes del try\nHu: " + Hu);
    String query = "SELECT WERKS FROM VEKP WHERE EXIDV= ?";
    Connection con;
    try {
      con = jdbcTemplate.getDataSource().getConnection();
      CallableStatement callableStatement = null;
      callableStatement = con.prepareCall(query);
      callableStatement.setString(1, Hu);
      rs = callableStatement.executeQuery();
      if (rs.next()) {
        log.info("Dentro if");
        werks = rs.getString("WERKS");
      }
    } catch (SQLException e) {
      log.error(e.toString());
    }
    log.info("Centro encontrado: " + werks);
    return werks;

  }

}
