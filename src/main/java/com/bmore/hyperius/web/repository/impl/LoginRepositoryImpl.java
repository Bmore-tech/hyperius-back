package com.bmore.hyperius.web.repository.impl;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.LoginDTO;
import com.bmore.hyperius.web.dto.NewSecureLoginDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.LoginRepository;

@Repository
public class LoginRepositoryImpl implements LoginRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final String LOCAL = "local";
  private static final String ACTIVEDIRECTORY = "LDAP";
  private static final String OPERADOR = "operador";

  @Override
  public ResultDTO login(String entry) {
    ResultDTO result = new ResultDTO();

    try {
      String sql = String.format("select WERKS, ZADMIN, ZWebApp from  HCMDB.dbo.zUsuario where IDRED = '%s';", entry);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      result.setMsg((String) row.get("WERKS"));
      result.setTypeI((int) row.get("ZADMIN"));
      result.setId(1);
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("No se tiene registrado un centro para el usuario: " + entry);
    }

    return result;
  }

  @Override
  public ResultDTO newLogin(NewSecureLoginDTO entry) {
    ResultDTO result = new ResultDTO();

    try {
      String sql = String.format(
          "SELECT IDRED, DataSource, zPassword, WERKS, ZADMIN, ZWebApp from zUsuario WITH(NOLOCK) WHERE IDRED = '%s';",
          entry.getUser());
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      String fuenteDatos = (String) row.get("DataSource");
      if (fuenteDatos.equalsIgnoreCase(LOCAL)) {
        if (entry.getPassword().equals(new String(Base64.decodeBase64((String) row.get("zPassword"))))) {
          result.setId(1);
          result.setMsg((String) row.get("WERKS"));
          result.setTypeI((int) row.get("ZADMIN"));
        } else {
          result.setId(2);
          result.setMsg("Contraseña Incorrecta");
        }
      } else if (fuenteDatos.equalsIgnoreCase(ACTIVEDIRECTORY)) {
        result = secureLdapLogin(entry);
        if (result.getId() == 1) {
          result.setMsg((String) row.get("WERKS"));
          result.setTypeI((int) row.get("ZADMIN"));
        } else {
          if (result.getMsg().contains("AcceptSecurityContext")) {
            result.setMsg("Contraseña Incorrecta");
          } else if (result.getMsg().contains("timeout")) {
            result.setMsg("Timeout al tratar de validar las credenciales del usuario");
          }
        }
      } else if (fuenteDatos.equalsIgnoreCase(OPERADOR)) {
        result.setId(2);
        result.setMsg("Usuario Operador solo puede ingresar mediante HandHeld: " + entry.getUser());
      } else {
        result.setId(2);
        result.setMsg("Usuario sin Privilegios para acceder al sistema: " + entry.getUser());
      }
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("Usuario sin Privilegios para acceder al sistema: " + entry.getUser());
    }

    return result;
  }

  @Override
  public ResultDTO loginAppWeb(String entry) {
    ResultDTO result = new ResultDTO();

    try {
      String sql = String.format("select WERKS, ZADMIN, ZWebApp from  HCMDB.dbo.zUsuario where IDRED = '%s';", entry);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      if ((int) row.get("ZWebApp") == 0) {
        result.setId(2);
        result.setMsg("El usuario " + entry + " no tiene permisos para la aplicacion Web");
      } else {
        result.setMsg((String) row.get("WERKS"));
        result.setTypeI((int) row.get("ZADMIN"));
        result.setId(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("No se tiene registrado un centro para el usuario: " + entry);
    }

    return result;
  }

  @Override
  public LoginDTO existeRegistroUsuario(String entry) {
    LoginDTO loginDTO = new LoginDTO();
    ResultDTO result = new ResultDTO();

    try {
      String sql = String.format("select * from HCMDB.dbo.zSession where idRed = '%s';", entry);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      loginDTO.setLastLogin((String) row.get("lastLogin"));
      loginDTO.setIdRed((String) row.get("idRed"));
      loginDTO.setLastOperation((String) row.get("lastOperation"));
      loginDTO.setLogOut((String) row.get("logOut"));
      loginDTO.setSessionId((String) row.get("sessionId"));
      result.setId(1);
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(3);
      result.setMsg("Usuario no existe desde DB: " + entry);
    }

    loginDTO.setResult(result);
    return loginDTO;
  }

  @Override
  public ResultDTO ingresaRegistroUsuario(LoginDTO loginDTO) {
    ResultDTO result = new ResultDTO();

    String sql = "insert into  HCMDB.dbo.zSession (idRed,sessionId,lastLogin,lastOperation,logOut) VALUES (?, ?, ?, ?, 0);";
    Object[] args = { loginDTO.getIdRed(), loginDTO.getSessionId(), loginDTO.getLastLogin(),
        loginDTO.getLastOperation() };
    jdbcTemplate.update(sql, args);

    result.setId(1);
    return result;
  }

  @Override
  public ResultDTO actualizaHoraUltimaOperacion(String idRed) {
    ResultDTO result = new ResultDTO();
    java.util.Date date = new java.util.Date();

    String query = "update HCMDB.dbo.zSession set lastOperation = ? where idRed = ?;";
    Object[] args = { date.getTime(), idRed };

    jdbcTemplate.update(query, args);
    return result;
  }

  @Override
  public ResultDTO actualizaRegistroUsuario(LoginDTO loginDTO) {
    ResultDTO result = new ResultDTO();

    String query = "update  HCMDB.dbo.zSession set sessionId = ? , lastLogin = ?, lastOperation = ?,  logOut = 0 where idRed = ?;";
    Object[] args = { loginDTO.getSessionId(), loginDTO.getLastLogin(), loginDTO.getLastOperation(),
        loginDTO.getIdRed() };

    jdbcTemplate.update(query, args);
    result.setId(1);

    return result;
  }

  @Override
  public ResultDTO logOut(String idRed) {
    ResultDTO result = new ResultDTO();

    String sql = "update HCMDB.dbo.zSession set logOut = '1' where idRed = ?;";
    Object[] args = { idRed };

    jdbcTemplate.update(sql, args);

    return result;
  }

  public static ResultDTO secureLdapLogin(NewSecureLoginDTO entry) {
    DirContext ldapContext;
    ResultDTO resultDT = new ResultDTO();
    try {
      Hashtable<String, String> ldapEnv = new Hashtable<>(11);
      ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      ldapEnv.put(Context.PROVIDER_URL, "ldap://modelo.gmodelo.com.mx:389");
      ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
      ldapEnv.put(Context.SECURITY_PRINCIPAL, "Modelo\\" + entry.getUser());
      ldapEnv.put(Context.SECURITY_CREDENTIALS, entry.getPassword());
      ldapEnv.put("java.naming.ldap.attributes.binary", "objectSid");
      ldapEnv.put(Context.REFERRAL, "follow");

      ldapContext = new InitialDirContext(ldapEnv);
      SearchControls searchCtls = new SearchControls();

      searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

      String searchFilter = "(&(objectClass=user)(samaccountname=" + entry.getUser() + "))";
      String searchBase = "DC=modelo,DC=gmodelo,DC=com,DC=mx";

      NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, searchFilter, searchCtls);

      while (answer.hasMoreElements()) {
        answer.next();
      }

      resultDT.setId(1);
      resultDT.setMsg("Successfull Login");
      ldapContext.close();
    } catch (Exception e) {
      resultDT.setId(3);
      resultDT.setMsg(e.getMessage());
    }
    return resultDT;
  }

}
