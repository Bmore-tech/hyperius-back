package com.bmore.hyperius.web.repository.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTOItem;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.UbicacionPTRepository;
import com.bmore.hyperius.web.utils.Utils;

public class UbicacionPTRepositoryImpl implements UbicacionPTRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger LOCATION = LoggerFactory.getLogger(getClass());

  private static String VALIDA_ORDEN_PRODUCCION = "select AUFK.AUFNR,  dbo.conFec(AUFK.ERDAT) as ERDAT, AUFK.WERKS, zCentrosBCPS.descripcion "
      + "from HCMDB.dbo.AUFK AUFK WITH(NOLOCK) inner join HCMDB.dbo.zCentrosBCPS zCentrosBCPS WITH(NOLOCK) on AUFK.WERKS = zCentrosBCPS.werks "
      + "and AUFK.AUFNR=? and AUFK.WERKS=?";;

  private static String DATOS_ORDEN = "select POSNR,AFPO.MATNR,MAKT.MAKTX,PSMNG,WEMNG,MEINS from HCMDB.dbo.AFPO AFPO WITH(NOLOCK)"
      + "left join HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on AFPO.MATNR = MAKT.MATNR where AFPO.AUFNR=?";

  private static String GET_CARRILES_BLOQUEADOS = "select distinct(CARRIL) "
      + "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";

  private static String GET_CARRILES = "EXEC SP_BCPS_GET_CARRILES_POR_MATERIAL ?,?,?,?,?";

  private static String GET_CARRILES_VACIOS = "EXEC SP_GET_CARRILES_VACIOS ?,?,?,?,?";

  private static String GET_CARRILES_PROPUESTOS = "SELECT distinct LGNUM, LGTYP, LGPLA FROM ZPickingEntregaEntrante with(nolock) where VBELN = ? and werks = ? and idProceso = ? and matnr = ? and (Status  is null or Status ='1')";

  private static String SUMA_ZPICKING_ORDEN_PRODUCCION = "select sum(cast(VERME as float))as cantidad from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM "
      + "in(SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
      + "where vbeln =? and matnr=? and status='X' and EXIDV is not null  and idProceso='3')";

  private static String DETAIL_ENTRY = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
      + "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,?,?,?)";

  private static String PENDIENTES_POR_PICKEAR_POR_CARRIL = "select count(*) as cantidad from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
      + "where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='3' and Status is null";

  private static String PICKING_COMPLETO_POSICION = "select count (*) as cantidad from ZPickingEntregaEntrante WITH(NOLOCK) "
      + "where MATNR=? and VBELN = ? and idProceso='3' and Status ='X'";

  private static String HUS_EN_ORDEN_PRODUCCION = "select count(*) as cantidad from VEPO WITH(NOLOCK) where matnr=? and VENUM in "
      + "(select venum from VEKP WITH(NOLOCK) where VPOBJKEY=(select VPOBJKEY from AUFK WITH(NOLOCK) where AUFNR = ?))";

  private static String GET_DATA_HU = "select vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH,VHILM from "
      + "HCMDB.dbo.MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK)on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
      + "VEKP.venum = VEPO.venum where VEKP.EXIDV=? and VEPO.VELIN ='1'";

  private static String CONSULTA_RESERVA_CARRIL_HU = "SELECT LGNUM, LGTYP, LGPLA, STATUS from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
      + "where VBELN=? and EXIDV=? and idProceso='3'";

  private static String CONFIRMA_PICKING_HU = "update HCMDB.dbo.ZPickingEntregaEntrante set Status = 'X' where VBELN = ? and EXIDV=? and idProceso='3'";

  private static String RESERVAR_CARRIL_HU = "UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante "
      + "set EXIDV = ? where VBELN = ? and MATNR = ? and  Status is null and EXIDV is null and idProceso='3'";

  private static String UPDATE_LQUA = "insert into HCMDB.dbo.LQUA (LGNUM,MATNR,WERKS,BESTQ,LGTYP,LGPLA,MEINS,GESME,VERME,LENUM,LGORT,SKZUE,SKZUA,LETYP) VALUES "
      + "(?,?,?,'',?,?,?,?,?,?,?,NULL,NULL,"
      + "(select top (1) LETYP from ZPAITT_TTW WITH(NOLOCK) where WERKS= ? and MATNR=(select top (1) VHILM FROM VEKP WITH(NOLOCK) where EXIDV= ? and WERKS= ?)))";

  private static String INSERT_PROCESO_ZCONTINGENCIA_7 = "insert into HCMDB.dbo.zContingencia(IDPROC,FECHA,HORA,CENTRO,HU,ORDEN_PRODUCCION,CANTIDAD,USUARIO,TARIMA,TIPO_ALMACEN,DESTINO) "
      + "select IDPROC=7, convert(date,getdate()), convert(time, getdate()), WERKS = ?, HU=?,ORDEN_PRODUCCION=?,CANTIDAD= ?,USUARIO= ?,TARIMA=?,TIPO_ALMACEN=?,DESTINO=?";

  private static String GETFALTANTES = "select count(*) from "
      + "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and Status is null and idProceso='3'";

  private static String GET_AUFNR_FROM_HU = "SELECT VPOBJKEY from HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) "
      + "on VEPO.VENUM = VEKP.VENUM and VEKP.EXIDV= ? and VEPO.WERKS = ? and VEPO.VELIN='1'";

  // TODO Remove unused code found by UCDetector
  // static String VALIDAR_ORDEN_PRODUCCION_EN_PICKING = "SELECT VBELN from
  // HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN=? and
  // idProceso='3'";

  private static String VALIDA_PICK = "select distinct(MATNR) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = ? and idProceso='3'";

  // private static String LIMPIA_PENDIENTE_USUARIO = "update
  // HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null
  // where VBELN = ? and Status = '1' and usuarioMontacarga = ? and
  // idProceso='3'";

  public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput) {

    OrdenProduccionDTO orden = new OrdenProduccionDTO();
    ResultDTO result = new ResultDTO();
    result.setId(0);

    LOCATION.error("Dentro de DAO: " + ordenInput.getOrdenProduccion());
    LOCATION.error("Dentro de DAO: " + ordenInput.getWerks());

    Object[] args = { ordenInput.getOrdenProduccion(), ordenInput.getWerks() };
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

    CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
    List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

    OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
    List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();

    ResultDTO result = new ResultDTO();
    Object[] args = { aufnr };
    OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

    // int contabilizado = 0;
    jdbcTemplate.query(DATOS_ORDEN, args, new RowMapper<OrdenProduccionDetalleDTO>() {
      @Override
      public OrdenProduccionDetalleDTO mapRow(ResultSet rs, int i) throws SQLException {
        int cant = 0;
        while (rs.next()) {

          cant++;
          item.setPosicion(rs.getString("POSNR"));
          item.setMaterial(rs.getString("MATNR"));
          item.setDescripcion(rs.getString("MAKTX"));
          item.setCajas(rs.getString("PSMNG"));
          item.setCajasAsignadas(rs.getString("WEMNG"));
          item.setMe(rs.getString("MEINS"));
          Object[] args2 = { aufnr, item.getMaterial() };
          // primer query
          int cantidadPrimero = jdbcTemplate.queryForObject(SUMA_ZPICKING_ORDEN_PRODUCCION, args2, Integer.class);
          String cantidad = cantidadPrimero + "";
          item.setCajasAsignadas((Float.parseFloat(cantidad) + Float.parseFloat(rs.getString("WEMNG"))) + "");
          // segundo query
          Object[] args3 = { item.getMaterial(), aufnr };
          int cantidadSegundo = jdbcTemplate.queryForObject(HUS_EN_ORDEN_PRODUCCION, args3, Integer.class);
          String cantidadSubida = cantidadSegundo + "";
          item.setHus(cantidadSubida);
          // tercer query
          Object[] args4 = { item.getMaterial(), aufnr };
          int cantidadTercer = jdbcTemplate.queryForObject(PICKING_COMPLETO_POSICION, args4, Integer.class);
          String cantidadSubidaTercer = cantidadTercer + "";
          item.setHusPendientes(cantidadSubidaTercer);
        }

        if (cant != 0) {
          result.setId(1);
          result.setMsg("Detalle de Orden recuperado con exito");
        } else {
          result.setId(2);
          result.setMsg("No fue posible recuperar el detalle de la orden");
        }

        return item;
      }
    });

    detalle.add(item);

    detalleList.setItem(detalle);
    ordenProduccionDTO.setItems(detalleList);

    carrilesList.setItem(carriles);
    ordenProduccionDTO.setCarriles(carrilesList);

    ordenProduccionDTO.setResultDT(result);
    return ordenProduccionDTO;
  }

  @Override
  public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z, String LGORT,
      String VBELN, HashMap<String, String> carrilesBloqueados) {

    CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
    List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();

    HashMap<String, String> hashMap = new HashMap<String, String>();

    Object[] args = { MATNR, WERKS, ID_PR, ID_PR_Z, 1 };

    jdbcTemplate.query(GET_CARRILES, args, new RowMapper<List<CarrilUbicacionDTO>>() {
      @Override
      public List<CarrilUbicacionDTO> mapRow(ResultSet rs, int i) throws SQLException {
        while (rs.next()) {

          if (carrilesBloqueados
              .get(rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim()) == null) {

            CarrilUbicacionDTO item = new CarrilUbicacionDTO();
            item.setLgnum(rs.getString("LGNUM"));
            item.setLgtyp(rs.getString("LGTYP"));
            item.setLgpla(rs.getString("LGPLA"));
            item.setCantidadHus(rs.getString("TOTAL"));
            item.setCajas(rs.getString("VERME"));
            item.setMe(rs.getString("MEINS"));
            item.setMaxle(rs.getString("MAXLE"));
            hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
            carrilList.add(item);
          }
        }
        return carrilList;
      }
    });

    Object[] args2 = { MATNR, LGORT, WERKS, ID_PR, ID_PR_Z };

    jdbcTemplate.query(GET_CARRILES_VACIOS, args2, new RowMapper<List<CarrilUbicacionDTO>>() {
      @Override
      public List<CarrilUbicacionDTO> mapRow(ResultSet rs2, int i) throws SQLException {
        while (rs2.next()) {

          if (carrilesBloqueados
              .get(rs2.getString("LGNUM") + rs2.getString("LGTYP") + rs2.getString("LGPLA").trim()) == null) {
            CarrilUbicacionDTO item = new CarrilUbicacionDTO();
            item.setLgnum(rs2.getString("LGNUM"));
            item.setLgtyp(rs2.getString("LGTYP"));
            item.setLgpla(rs2.getString("LGPLA"));
            item.setCantidadHus("0");
            hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
            carrilList.add(item);
          }
        }
        return carrilList;
      }
    });

    Object[] args3 = { VBELN, WERKS, ID_PR_Z, MATNR };

    jdbcTemplate.query(GET_CARRILES_PROPUESTOS, args3, new RowMapper<List<CarrilUbicacionDTO>>() {
      @Override
      public List<CarrilUbicacionDTO> mapRow(ResultSet rs4, int i) throws SQLException {

        while (rs4.next()) {
          if (carrilesBloqueados
              .get(rs4.getString("LGNUM") + rs4.getString("LGTYP") + rs4.getString("LGPLA").trim()) != null) {

            CarrilUbicacionDTO item = new CarrilUbicacionDTO();
            item.setLgnum(rs4.getString("LGNUM"));
            item.setLgtyp(rs4.getString("LGTYP"));
            item.setLgpla(rs4.getString("LGPLA"));
            item.setCantidadHus("-1");

            if (hashMap.get(item.getLgnum() + item.getLgtyp() + item.getLgpla()) == null)
              carrilList.add(0, item);
          }
        }
        return carrilList;
      }

    });

    for (int x = 0; x < carrilList.size(); x++) {

      CarrilUbicacionDTO item = carrilList.get(x);

      Object[] args4 = { item.getLgnum(), item.getLgtyp(), item.getLgpla() };
      int result = jdbcTemplate.queryForObject(PENDIENTES_POR_PICKEAR_POR_CARRIL, args4, Integer.class);
      int cantidad = result;
      item.setHusPendientes(cantidad + "");

    }

    LOCATION.error("Size carriles SQL:" + carrilList.size());
    carrilesDTO.setItem(carrilList);

    return carrilesDTO;

  }

  @Override
  public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {

    ResultDTO resultDT = new ResultDTO();

    for (int x = 0; x < carrilesDTO.getItem().size(); x++) {

      BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());

      int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

      for (int y = 0; y < ingresarZpicking; y++) {
        LOCATION.error("Ingresando");
        Object[] args = { VBELN, carrilesDTO.getItem().get(x).getLgnum(), carrilesDTO.getItem().get(x).getLgtyp(),
            carrilesDTO.getItem().get(x).getLgpla(), Utils.zeroFill(carrilesDTO.getItem().get(x).getMaterial(), 18),
            user, "3", werks, carrilesDTO.getItem().get(x).getLgnum() };
        int retorno = jdbcTemplate.update(DETAIL_ENTRY, args);

      }
    }
    resultDT.setId(1);
    resultDT.setMsg("Se registro la orden de produccion correctamete, mandar a montacarga");

    return resultDT;
  }

  @Override
  public OrdenProduccionDetalleDTO getDataHU(String hu) {

    ResultDTO result = new ResultDTO();
    OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();
    Object[] args = { hu };
    LOCATION.error("HU: " + hu);

    jdbcTemplate.query(GET_DATA_HU, args, new RowMapper<OrdenProduccionDetalleDTO>() {
      @Override
      public OrdenProduccionDetalleDTO mapRow(ResultSet rs, int i) throws SQLException {
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
        return orden;
      }
    });

    orden.setResultDT(result);

    return orden;

  }

  @Override
  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) {

    CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();
    ResultDTO resultDT = new ResultDTO();

    Object[] args = { vbeln, hu };

    jdbcTemplate.query(CONSULTA_RESERVA_CARRIL_HU, args, new RowMapper<ResultDTO>() {
      @Override
      public ResultDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {

          carrilUbicacionDTO.setLgnum(rs.getString("LGNUM"));
          carrilUbicacionDTO.setLgtyp(rs.getString("LGTYP"));
          carrilUbicacionDTO.setLgpla(rs.getString("LGPLA"));

          resultDT.setId(1);
          resultDT.setMsg("HU1 encontrada");
          resultDT.setTypeS(rs.getString("STATUS"));

        } else {
          resultDT.setId(0);
          resultDT.setMsg("El HU no esta diponible para pickear");
        }

        return resultDT;
      }
    });

    carrilUbicacionDTO.setResultDT(resultDT);

    return carrilUbicacionDTO;
  }

  @Override
  public ResultDTO confirmaPickingHU(String VBELN, String hu) {
    ResultDTO result = new ResultDTO();
    result.setId(0);
    result.setMsg("Error de conexion a BD");
    Object[] args = { VBELN, hu };

    int retorno = jdbcTemplate.update(CONFIRMA_PICKING_HU, args);

    if (retorno > 0) {
      result.setId(1);
      result.setMsg("HU CONFIRMADA EN UBICACION");
    } else {
      result.setId(0);
      result.setMsg("NO FUE POSIBLE CONFIRMAR HU EN UBICACION");
    }

    return result;

  }

  @Override
  public ResultDTO reservarCarrilHU(String entrega, String hu, String matnr) {
    ResultDTO result = new ResultDTO();
    Object[] args = { hu, entrega, matnr };

    int retorno = jdbcTemplate.update(RESERVAR_CARRIL_HU, args);
    if (retorno > 0) {
      result.setId(1);
      result.setMsg("LUGAR RESERVADO PARA HU");
    } else {
      result.setId(0);
      result.setMsg("NO FUE POSIBLE RESERVAR HU");
    }
    return result;

  }

  @Override
  public ResultDTO aumentaInventario(OrdenProduccionInputDTO orden, String hu) {

    ResultDTO result = new ResultDTO();

    LOCATION.error("Se consume inventario: ");

    // static String UPDATE_LQUA =
    // "insert into HCMDB.dbo.LQUA
    // (LGNUM,MATNR,WERKS,BESTQ,LGTYP,LGPLA,MEINS,GESME,VERME,LENUM,LGORT) VALUES "
    // +
    // "(?,?,?,(select DISTINCT(Clase_Inspeccion) from HCMDB.dbo.MaterialQUA as QA
    // where Centro= ? and Material=?),?,?,?,?,?,?,?)";
    Object[] args = { orden.getuDestino0(), orden.getMatnr(), orden.getWerks(), orden.getuDestino1(),
        orden.getuDestino2(), orden.getCantT(), orden.getCant(), orden.getCant(), hu, "LV01", orden.getWerks(),
        hu, orden.getWerks() };
    // stmn.setString(1, orden.getuDestino0());// LGNUM
    // stmn.setString(2, orden.getMatnr());// MATNR
    // stmn.setString(3, orden.getWerks());// WERKS
    //// stmn.setString(4, orden.getWerks());// BESTQ
    //// stmn.setString(5, orden.getMatnr());// BESTQ
    // stmn.setString(4, orden.getuDestino1());// LGTYP
    // stmn.setString(5, orden.getuDestino2()); // LGPLA
    // stmn.setString(6, orden.getCantT());// MEINS
    // stmn.setString(7, orden.getCant());// GESME
    // stmn.setString(8, orden.getCant());// VERME
    // stmn.setString(9, hu);// LENUM
    // stmn.setString(10, "LV01");// LGORT //EN PT solo se ingresa al LV01
    // stmn.setString(11, orden.getWerks());// LGORT //EN PT solo se
    // ingresa al LV01
    // stmn.setString(12, hu);// LGORT //EN PT solo se ingresa al LV01
    // stmn.setString(13, orden.getWerks());// LGORT //EN PT solo se
    // ingresa al LV01
    int retorno = jdbcTemplate.update(UPDATE_LQUA, args);
    if (retorno > 0) {
      result.setId(1);
      result.setMsg("Inventario aumentado");

      LOCATION.error("ubicacion LGNUM: " + orden.getuDestino0() + " LGTYP: " + orden.getuDestino1()
          + " LGPLA:" + orden.getuDestino2());

      Utils.actualizarInventarioCarriles(orden.getuDestino0(), orden.getuDestino1(), orden.getuDestino2());

    } else {
      result.setId(0);
      result.setMsg("El inventario no fue aumentado");
    }

    return result;

  }

  @Override
  public ResultDTO insertProcesoContingencia_7(OrdenProduccionInputDTO orden, String hu) {

    ResultDTO result = new ResultDTO();
    Object[] args = { orden.getWerks(), hu, orden.getOrdeProduccion(), orden.getCant(), orden.getUsuarioMontacarga(),
        orden.getTarima(), orden.getuDestino1(), orden.getuDestino2() };
    int retorno = jdbcTemplate.update(INSERT_PROCESO_ZCONTINGENCIA_7, args);

    if (retorno > 0) {
      result.setId(1);
      result.setMsg("7to paso ejecutado con exito");
    } else {
      result.setId(0);
      result.setMsg("7to paso ejecutado con error");
    }
    return result;
  }

  @Override
  public int getFaltantes(String entry) {
    int x = 999999;
    Object[] args = { entry };

    int resultado = jdbcTemplate.queryForObject(GETFALTANTES, args, Integer.class);

    if (resultado >= 0) {
      x = resultado;
    } else {
      x = 0;
    }

    return x;
  }

  @Override
  public ResultDTO getAUFNRFromHu(String hu, String werks) {

    ResultDTO resultDT = new ResultDTO();
    Object[] args = { hu, werks };
    resultDT.setTypeS("");

    jdbcTemplate.query(GET_AUFNR_FROM_HU, args, new RowMapper<ResultDTO>() {
      @Override
      public ResultDTO mapRow(ResultSet rs, int i) throws SQLException {
        LOCATION.error("Werks daoUbicacionPT: " + werks);
        if (rs.next()) {
          resultDT.setTypeS(rs.getString("VPOBJKEY"));
          resultDT.setId(1);
          resultDT.setMsg("Orden de producción encontrada");
        } else {
          resultDT.setId(0);
          resultDT.setMsg("Orden de producción no encontrada vía HU y con centro de montacarguista: " + werks);
        }
        return resultDT;
      }
    });

    return resultDT;
  }

  @Override
  public OrdenProduccionInputDTO validarOrdenEnPickin(String entry) {

    OrdenProduccionInputDTO orden = new OrdenProduccionInputDTO();
    ResultDTO result = new ResultDTO();
    HashMap<String, String> map = new HashMap<String, String>();
    Object[] args = { entry };
    jdbcTemplate.query(VALIDA_PICK, args, new RowMapper<ResultDTO>() {
      @Override
      public ResultDTO mapRow(ResultSet rs, int i) throws SQLException {
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
        return result;
      }
    });

    orden.setMateriales(map);
    orden.setResultDT(result);
    return orden;
  }

  // TODO Remove unused code found by UCDetector
  // public ResultDTO limpiaPendientesXUsuario(String vbeln, String user) {
  // ResultDTO result = new ResultDTO();
  // Connection con = DBConnection.createConnection();
  // PreparedStatement stmn = null;
  //
  // try {
  //
  // LOCATION.error("Limpia pendientes DAO :" + vbeln);
  // stmn = con.prepareStatement(LIMPIA_PENDIENTE_USUARIO);
  // stmn.setString(1, vbeln);
  // stmn.setString(2, user);
  //
  // if (stmn.executeUpdate() > 0) {
  // result.setId(1);
  // result.setMsg("Limpieza ejecutada con exito");
  // } else {
  // result.setId(1);
  // result.setMsg("Limpieza ejecutada con exito");
  // }
  // } catch (SQLException e) {
  // result.setId(2);
  // result.setMsg(e.getMessage());
  // } catch (Exception en) {
  // result.setId(2);
  // result.setMsg(en.getMessage());
  // } finally {
  // try {
  // DBConnection.closeConnection(con);
  // } catch (Exception e) {
  // result.setId(2);
  // result.setMsg(e.getMessage());
  // }
  // }
  // return result;
  // }

  @Override
  public OrdenProduccionDTO detalleOrdenProduccionSoloCabecera(String aufnr, String werks) {
    OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();

    CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
    List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

    OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
    List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();

    ResultDTO result = new ResultDTO();
    Object[] args = { aufnr };

    LOCATION.error("AUFNR_en DAO: " + aufnr);

    jdbcTemplate.query(DATOS_ORDEN, args, new RowMapper<List<OrdenProduccionDetalleDTO>>() {
      @Override
      public List<OrdenProduccionDetalleDTO> mapRow(ResultSet rs, int i) throws SQLException {
        int cant = 0;
        while (rs.next()) {
          LOCATION.error("while++");
          cant++;
          OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

          item.setPosicion(rs.getString("POSNR"));
          item.setMaterial(rs.getString("MATNR"));
          item.setDescripcion(rs.getString("MAKTX"));
          item.setCajas(rs.getString("PSMNG"));
          item.setCajasAsignadas(rs.getString("WEMNG"));
          item.setMe(rs.getString("MEINS"));
          detalle.add(item);
        }
        if (cant != 0) {
          result.setId(1);
          result.setMsg("Detalle de Orden recuperado con exito");
        } else {
          result.setId(2);
          result.setMsg("No fue posible recuperar el detalle de la orden");
        }
        return detalle;
      }
    });

    detalleList.setItem(detalle);
    ordenProduccionDTO.setItems(detalleList);

    carrilesList.setItem(carriles);
    ordenProduccionDTO.setCarriles(carrilesList);

    ordenProduccionDTO.setResultDT(result);
    return ordenProduccionDTO;
  }

  @Override
  public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {

    HashMap<String, String> map = new HashMap<String, String>();

    Object[] args = { idProceso, werks };
    jdbcTemplate.query(GET_CARRILES_BLOQUEADOS, args, new RowMapper<HashMap<String, String>>() {
      @Override
      public HashMap<String, String> mapRow(ResultSet rs, int i) throws SQLException {
        while (rs.next()) {
          map.put(rs.getString("CARRIL").trim(), "");
        }
        return map;
      }
    });
    return map;
  }

}
