package com.bmore.hyperius.web.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.ReporteOperacionesDTO;
import com.bmore.hyperius.web.dto.ReporteShippingDTO;
import com.bmore.hyperius.web.dto.ServletReporteProformaDTO;
import com.bmore.hyperius.web.repository.ServletReporteRepository;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class ServletReporteRepositoryImpl implements ServletReporteRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public List<ServletReporteProformaDTO> getDatosProforma() {
    return null;
  }

  @Override
  public List<ReporteOperacionesDTO> getReporteOperacionesDAO(String werks) {
    String sql = String.format("SELECT MATNR, RECEPCION, ALIMENTACION, PRODUCCION, EMBARQUE, RECEPCIONES, " +
        "EMBARQUES FROM VS_BCPS_UTILS_SUMMARY_BY_MATNR WHERE CENTRO = '%s'", werks);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    return ReporteOperacionesDTO.ServletReporteOperacionesStockDTORS(rows);
  }

  @Override
  public List<ReporteOperacionesDTO> getReporteOperacionesInitialStockDAO(String werks) {
    String sql = String.format("SELECT TOP (100) * FROM VS_BCPS_UTILS_SUMMARY_REPORTE_OPERACIONES " +
        "WITH(NOLOCK) where WERKS= '%s'  ORDER BY MATNR;", werks);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    return ReporteOperacionesDTO.ServletReporteOperacionesStockDTORS(rows);
  }

  @Override
  public String getTknum(String Vbeln) {
    String sql = String.format("SELECT DISTINCT TKNUM AS TKNUM FROM VTTP WHERE VBELN = '%s';", Vbeln);
    return (String) jdbcTemplate.queryForMap(sql).get("TKNUM");
  }

  @Override
  public List<ReporteShippingDTO> getDatosShipping(String werks) {
    List<ReporteShippingDTO> shippingList = new ArrayList<>();
    String sql = String.format("SELECT CONTENEDOR, SELLO, BOOKING, DESTINO, PESO, NAVIERA1, NAVIERA2, MEDIDA, " +
        "AA1, AA2, SKU FROM VS_BCPS_REPORTE_SHIPPING WHERE CENTRO = '%s';", werks);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      ReporteShippingDTO shippingDto = new ReporteShippingDTO();
      shippingDto.setContenedor(Utils.isNull((String) row.get("CONTENEDOR")));
      shippingDto.setSello(Utils.isNull((String) row.get("SELLO")));
      shippingDto.setBooking(Utils.isNull((String) row.get("BOOKING")));
      shippingDto.setDestino(Utils.isNull((String) row.get("DESTINO")));
      shippingDto.setPeso(Utils.isNull((String) row.get("PESO")));
      shippingDto.setNaviera(Utils.isNull((String) row.get("NAVIERA1")) + Utils.isNull((String) row.get("NAVIERA2")));
      shippingDto.setMedida(Utils.isNull((String) row.get("MEDIDA")));
      shippingDto.setAa(Utils.isNull((String) row.get("AA1")) + Utils.isNull((String) row.get("AA2")));
      shippingDto.setSku(Utils.isNull((String) row.get("SKU")));
      shippingList.add(shippingDto);
    }

    return shippingList;
  }
}
