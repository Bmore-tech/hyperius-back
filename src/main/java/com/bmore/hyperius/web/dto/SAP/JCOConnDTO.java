package com.bmore.hyperius.web.dto.SAP;

import lombok.Data;

@Data
public class JCOConnDTO {
    String source;
    String destinationName;
    String hostname;
    String username;
    String key;
    String password;
    String port;
    String cli;
    String sys;
    String base;
    String doma;
    Integer response;
}
