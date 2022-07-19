package com.bmore.hyperius.web.repository.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTOItem;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.VidrioUbicacionPTRepository;
import com.bmore.hyperius.web.utils.Utils;

public class VidrioUbicacionPTRepositoryImpl implements VidrioUbicacionPTRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static String VALIDA_ORDEN_PRODUCCION = "select AUFK.AUFNR,  dbo.conFec(AUFK.ERDAT) as ERDAT, AUFK.WERKS, zCentrosBCPS.descripcion "
      + "from HCMDB.dbo.AUFK AUFK inner join HCMDB.dbo.zCentrosBCPS zCentrosBCPS on AUFK.WERKS = zCentrosBCPS.werks "
      + "and AUFK.AUFNR=? and AUFK.WERKS=?";

  private static String DATOS_ORDEN = "select POSNR,AFPO.MATNR,MAKT.MAKTX,PSMNG,WEMNG,MEINS from HCMDB.dbo.AFPO AFPO "
      + "left join HCMDB.dbo.MAKT MAKT on AFPO.MATNR = MAKT.MATNR where AFPO.AUFNR=?";

  private static String SUMA_ZPICKING_VEKP_ORDEN_PRODUCCION = "select sum(cast(VEMNG as decimal(9,3)))as cantidad from HCMDB.dbo.vepo where MATNR=? and VELIN=1 "
      + "and VENUM in(select VENUM  from HCMDB.dbo.VEKP where VPOBJKEY=? and LGNUM is not null)";

  private static String HUS_EN_ORDEN_PRODUCCION = "select count(*) as cantidad from VEPO where matnr=? and VENUM in "
      + "(select venum from VEKP where VPOBJKEY=(select VPOBJKEY from AUFK where AUFNR = ?))";

  private static String VIDRIO_UBICA_PT = "exec sp_bcps_im_ubicacionPT_ingresa_hu ?,?,?,?,?,?";

  private final Logger LOCATION = LoggerFactory.getLogger(getClass());

  @Override
  public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput) {

    ResultDTO result = new ResultDTO();
    result.setId(0);
    Object[] args = { ordenInput.getOrdenProduccion(), ordenInput.getWerks() };

    LOCATION.error("Dentro de DAO: " + ordenInput.getOrdenProduccion());
    LOCATION.error("Dentro de DAO: " + ordenInput.getWerks());

    OrdenProduccionDTO orden = new OrdenProduccionDTO();
    jdbcTemplate.query(VALIDA_ORDEN_PRODUCCION, args, new RowMapper<OrdenProduccionDTO>() {
      @Override
      public OrdenProduccionDTO mapRow(ResultSet rs, int i) throws SQLException {

        if (rs.next()) {
          orden.setOrdenProduccion(rs.getString("AUFNR"));
          orden.setFabrica(rs.getString("WERKS"));
          orden.setFechaDocumento(rs.getString("ERDAT"));
          orden.setFabricaDesc(rs.getString("descripcion"));
          result.setId(1);
          result.setMsg("Recuperacion de datos de cabecera correcta");
        } else {
          result.setId(2);
          result.setMsg("La orden de producción no existe");
        }
        return orden;
      }
    });
    orden.setResultDT(result);
    return orden;
  }

  @Override
  public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks) {

    OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();
    OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
    List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();

    ResultDTO result = new ResultDTO();

    Object[] args1 = { aufnr };

    jdbcTemplate.query(DATOS_ORDEN, args1, new RowMapper<OrdenProduccionDTO>() {
      @Override
      public OrdenProduccionDTO mapRow(ResultSet rs, int i) throws SQLException {
        int cant = 0;
        while (rs.next()) {
          cant++;
          OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();
          item.setPosicion(rs.getString("POSNR"));
          item.setMaterial(rs.getString("MATNR"));
          item.setDescripcion(rs.getString("MAKTX"));
          item.setCajas(rs.getString("PSMNG"));
          item.setCajasAsignadas(rs.getString("WEMNG"));
          item.setMe(rs.getString("MEINS"));

          Object[] args2 = { item.getMaterial(), aufnr };

          int resultado = jdbcTemplate.queryForObject(SUMA_ZPICKING_VEKP_ORDEN_PRODUCCION, args2, Integer.class);
          String cantidad = resultado + "";

          LOCATION.error("Cantidad :@  " + cantidad
              + "     WEMNG  " + rs.getString("WEMNG"));
          BigDecimal numero1 = new BigDecimal(cantidad.trim());
          BigDecimal numero2 = new BigDecimal(rs
              .getString("WEMNG").trim());
          LOCATION.error("antes de suma: " + numero1);
          item.setCajasAsignadas(numero1.add(numero2).setScale(3,
              RoundingMode.HALF_UP)
              + "");
          LOCATION.error("depsues de suma:");

          int result = jdbcTemplate.queryForObject(HUS_EN_ORDEN_PRODUCCION, args2, Integer.class);
          item.setHus(result + "");

          detalle.add(item);
        }

        if (cant != 0) {
          result.setId(1);
          result.setMsg("Detalle de Orden recuperado con exito");
        } else {
          result.setId(2);
          result.setMsg("No fue posible recuperar el detalle de la orden");
        }

        return null;
      }
    });

    detalleList.setItem(detalle);
    ordenProduccionDTO.setItems(detalleList);

    ordenProduccionDTO.setResultDT(result);
    return ordenProduccionDTO;
  }

  @Override
  public ResultDTO vidrioUbicaPT(OrdenProduccionInputDTO ordenProduccion) {

    String query = "exec sp_bcps_im_ubicacionPT_ingresa_hu ?,?,?,?,?,?";

    ResultDTO result = new ResultDTO();
    result.setId(0);
    jdbcTemplate.execute(
        new CallableStatementCreator() {
          @Override
          public CallableStatement createCallableStatement(Connection con) throws SQLException {
            CallableStatement callableStatement = con.prepareCall(query);
            callableStatement.setString(1, ordenProduccion.getHu1());
            callableStatement.setString(2, ordenProduccion.getWerks());
            callableStatement.setString(3, ordenProduccion.getOrdeProduccion());
            callableStatement.setString(4, ordenProduccion
                .getUsuarioMontacarga());
            callableStatement.setString(5, Utils.getKeyTimeStamp());

            callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
            return callableStatement;
          }
        }, new CallableStatementCallback() {
          @Override
          public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
            cs.execute();
            int id = 0;
            id = cs.getInt(6);
            result.setId(id);
            return result;
          }
        });

    return result;

  }
}
