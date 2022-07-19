package com.bmore.hyperius.web.repository.impl;

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

import com.bmore.hyperius.web.dto.AlmacenDTO;
import com.bmore.hyperius.web.dto.AlmacenItemDTO;
import com.bmore.hyperius.web.dto.AlmacenesDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.SupervisorUtilsTraspasosRepository;

public class SupervisorUtilsTraspasosRepositoryImpl implements SupervisorUtilsTraspasosRepository {

  private String LGORT = "select distinct(almacen) from centrosAlmacenesPermitidos where centro = ?";

  private String LGNUM = "select distinct (noAlmacen) from centrosAlmacenesPermitidos where centro= ? and almacen= ?";

  private String LGTYP = "select distinct (tipoAlmacen) from centrosAlmacenesPermitidos where centro = ? and almacen = ? and noAlmacen= ? order by tipoAlmacen";

  private String LGPLA = "select distinct(lgpla)  from LAGP where LGNUM = ? and LGTYP = ?";

  private String LQUA = "select LGNUM,LGTYP,LGPLA,MATNR,CHARG, VERME, LENUM from LQUA where LGNUM = ? and LGTYP = ? and LGPLA = ? and WERKS =?";

  private String LQUA_BY_CHARG = "select LGNUM,LGTYP,LGPLA,MATNR,CHARG, VERME, LENUM from LQUA where CHARG = ? and WERKS =?";

  private String TRASPASO = "SP_BCPS_WM_TRASPASOS ?, ?, ?, ?, ?, ? ";

  private final Logger LOCATION = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public AlmacenesDTO lgortPermitidos(String werks) {
    Object[] args = { werks };
    ResultDTO resultDT = new ResultDTO();

    AlmacenesDTO almacenesDTO = new AlmacenesDTO();
    AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
    List<AlmacenDTO> items = new ArrayList<AlmacenDTO>();

    almacenesDTO.setResultDT(resultDT);
    almacenItemDTO.setItem(items);
    almacenesDTO.setItems(almacenItemDTO);

    // AlmacenDTO almacen = new AlmacenDTO(); // aquí guarda la info
    List<AlmacenDTO> almacen = jdbcTemplate.query(LGORT, args, new RowMapper<AlmacenDTO>() {
      @Override
      public AlmacenDTO mapRow(ResultSet rs, int i) throws SQLException {
        AlmacenDTO almacen = new AlmacenDTO();
        almacen.setLgort(rs.getString("almacen"));

        return almacen;

      }

    });

    if (almacen.size() > 0) {
      resultDT.setId(1);
      resultDT.setMsg("Almacén encontrado");
    } else {
      resultDT.setId(2);
      resultDT.setMsg("Almacén no encontrado");
    }
    return almacenesDTO;

  }

  @Override
  public AlmacenesDTO lgnumPermitidos(AlmacenDTO almacenDTO) {

    Object[] args = { almacenDTO.getWerks(), almacenDTO.getLgort() };
    ResultDTO resultDT = new ResultDTO();

    AlmacenesDTO almacenesDTO = new AlmacenesDTO();
    AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
    List<AlmacenDTO> items = jdbcTemplate.query(LGNUM, args, new RowMapper<AlmacenDTO>() {
      @Override
      public AlmacenDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        AlmacenDTO almacen = new AlmacenDTO();
        almacen.setLgnum(rs.getString("noAlmacen"));
        return almacen;
      }

    });

    almacenesDTO.setResultDT(resultDT);
    almacenItemDTO.setItem(items);
    almacenesDTO.setItems(almacenItemDTO);

    if (items.size() > 0) {
      LOCATION.error(">0");
      resultDT.setId(1);
      resultDT.setMsg("Numero de Almacén encontrado");

    } else {
      LOCATION.error("< 0 o = 0");
      resultDT.setId(2);
      resultDT.setMsg("Numero de Almacén no encontrado");
    }

    return almacenesDTO;

  }

  @Override
  public AlmacenesDTO lgtypPermitidos(AlmacenDTO almacenDTO) {

    Object[] args = { almacenDTO.getWerks(), almacenDTO.getLgort(), almacenDTO.getLgnum() };

    ResultDTO resultDT = new ResultDTO();

    AlmacenesDTO almacenesDTO = new AlmacenesDTO();
    AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
    List<AlmacenDTO> items = jdbcTemplate.query(LGTYP, args, new RowMapper<AlmacenDTO>() {
      @Override
      public AlmacenDTO mapRow(ResultSet rs, int i) throws SQLException {
        AlmacenDTO almacen = new AlmacenDTO();
        almacen.setLgtyp(rs.getString("tipoAlmacen"));
        return almacen;
      }
    });

    almacenesDTO.setResultDT(resultDT);
    almacenItemDTO.setItem(items);
    almacenesDTO.setItems(almacenItemDTO);

    if (items.size() > 0) {

      resultDT.setId(1);
      resultDT.setMsg("Tipos de Almacén encontrados");

    } else {

      resultDT.setId(2);
      resultDT.setMsg("Tipos de Almacén no encontrados");

    }

    return almacenesDTO;

  }

  @Override
  public AlmacenesDTO lgplaPermitidos(AlmacenDTO almacenDTO) {

    Object[] args = { almacenDTO.getLgnum(), almacenDTO.getLgtyp() };
    ResultDTO resultDT = new ResultDTO();

    AlmacenesDTO almacenesDTO = new AlmacenesDTO();
    AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
    List<AlmacenDTO> items = jdbcTemplate.query(LGPLA, args, new RowMapper<AlmacenDTO>() {
      @Override
      public AlmacenDTO mapRow(ResultSet rs, int i) throws SQLException {

        AlmacenDTO almacen = new AlmacenDTO();
        almacen.setLgpla(rs.getString("lgpla"));

        return almacen;

      }

    });

    almacenesDTO.setResultDT(resultDT);
    almacenItemDTO.setItem(items);
    almacenesDTO.setItems(almacenItemDTO);
    if (items.size() > 0) {

      resultDT.setId(1);
      resultDT.setMsg("Ubicaciones encontradas");

    } else {

      resultDT.setId(2);
      resultDT.setMsg("No se encontraron ubicaciones");
    }
    return almacenesDTO;

  }

  @Override
  public InventarioDTO lquaBusquedaTraspasos(AlmacenDTO almacenDTO, int opc) {

    ResultDTO resultDT = new ResultDTO();

    InventarioDTO inventarioDTO = new InventarioDTO();
    InventarioDetalleDTOItem inventarioDetalleDTOItem = new InventarioDetalleDTOItem();

    inventarioDTO.setItems(inventarioDetalleDTOItem);
    inventarioDTO.setResultDT(resultDT);

    String query = "";

    if (opc == 1) {
      query = LQUA;
      Object[] args = { almacenDTO.getLgnum(), almacenDTO.getLgtyp(), almacenDTO.getLgpla(), almacenDTO.getWerks() };
    }
    if (opc == 2) {
      query = LQUA_BY_CHARG;
      Object[] args = { almacenDTO.getCharg(), almacenDTO.getWerks() };
    }

    List<InventarioDetalleDTO> items = jdbcTemplate.query(query, new RowMapper<InventarioDetalleDTO>() {
      @Override
      public InventarioDetalleDTO mapRow(ResultSet rs, int i) throws SQLException {
        InventarioDetalleDTO inventarioDetalleDTO = new InventarioDetalleDTO();
        inventarioDetalleDTO.setLgnum(rs.getString("lgnum"));
        inventarioDetalleDTO.setLgtyp(rs.getString("lgtyp"));
        inventarioDetalleDTO.setLgpla(rs.getString("lgpla"));
        inventarioDetalleDTO.setMatnr(rs.getString("matnr"));
        inventarioDetalleDTO.setCharg(rs.getString("charg"));
        inventarioDetalleDTO.setVerme(rs.getString("verme"));
        inventarioDetalleDTO.setLenum(rs.getString("lenum"));
        return inventarioDetalleDTO;

      }

    });

    if (items.size() > 0) {

      resultDT.setId(1);
      resultDT.setMsg("Existe inventario en la ubicación");

    } else {

      resultDT.setId(2);
      resultDT.setMsg("No Existe inventario en la ubicación");

    }

    return inventarioDTO;

  }

  @Override
  public ResultDTO traspaso(InventarioDetalleDTO inventarioDetalleDTO,
      String user) {

    ResultDTO result = new ResultDTO();
    jdbcTemplate.execute(
        new CallableStatementCreator() {
          @Override
          public CallableStatement createCallableStatement(Connection con) throws SQLException {
            CallableStatement callableStatement = con.prepareCall(TRASPASO);
            callableStatement.setString(1, inventarioDetalleDTO.getLenum());
            callableStatement.setString(2, inventarioDetalleDTO.getLgnum());
            callableStatement.setString(3, inventarioDetalleDTO.getLgtyp());
            callableStatement.setString(4, inventarioDetalleDTO.getLgpla());
            callableStatement.setString(5, user);
            callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
            return callableStatement;
          }

        }, new CallableStatementCallback() {
          @Override
          public Object doInCallableStatement(CallableStatement callableStatement)
              throws SQLException, DataAccessException {
            callableStatement.execute();
            int id = 0;
            id = callableStatement.getInt(6);
            result.setId(id);
            return result;
          }
        });
    return result;
  }
}
