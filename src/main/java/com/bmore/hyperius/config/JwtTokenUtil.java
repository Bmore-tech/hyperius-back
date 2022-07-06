package com.bmore.hyperius.config;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -6038749138384946355L;

	public static final long JWT_TOKEN_VALIDITY = 90 * 60;

	private String secret = "ah_prro";

	public String getUsernameFromToken(String token) throws Exception {

		try {
			return getClaimFromToken(token, Claims::getSubject);
		} catch (Exception e) {
			throw e;
		}

	}

	public String getIdFromToken(String token) throws Exception {
		try {

			return getClaimFromToken(token, Claims::getId);
		} catch (Exception e) {
			throw e;
		}

	}

	public Date getExpirationDateFromToken(String token) throws Exception {
		try {
			return getClaimFromToken(token, Claims::getExpiration);
		} catch (Exception e) {
			throw e;
		}

	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws Exception {
		try {
			final Claims claims = getAllClaimsFromToken(token);
			return claimsResolver.apply(claims);
		} catch (Exception e) {
			throw e;
		}

	}

	private Claims getAllClaimsFromToken(String token) throws Exception {
		try {
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	public Boolean isTokenExpired(String token) throws Exception {
		try {
			final Date expiration = getExpirationDateFromToken(token);
			return expiration.before(new Date());
		} catch (Exception e) {
			throw e;
		}
	}

	public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
		return doGenerateToken(claims, "PC13");
	}
	
	public String updateToken(String username, Map<String, Object> claims) {
		return doGenerateToken(claims, username);
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).setId("")
				.setAudience("PC13").signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
		try {
			final String username = getUsernameFromToken(token);
			return (username.equals("PC13") && !isTokenExpired(token));
		} catch (Exception e) {
			throw e;
		}
	}

	public String getAudienceFromToken(String token) throws Exception {
		try {
			return getClaimFromToken(token, Claims::getAudience);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Obtiene el usuario en el Payload del Token.
	 * 
	 * @param token de la sesión de usuario.
	 * @return String con el werks encontrado en el token.
	 * @throws Exception en caso de token erroneo.
	 */
	public String getWerksFromToken(String token) throws Exception {
		try {
			final Claims claims = getAllClaimsFromToken(token.substring(7));
			return (String) claims.get("werks");
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Obtiene el usuario en el Payload del Token.
	 * 
	 * @param token de la sesión de usuario.
	 * @return String con el usuario encontrado en el token.
	 * @throws Exception en caso de token erroneo.
	 */
	public String getUsuarioFromToken(String token) throws Exception {
		try {
			final Claims claims = getAllClaimsFromToken(token.substring(7));
			return (String) claims.get("usuario");
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Obtiene el usuario en el Payload del Token.
	 * 
	 * @param token de la sesión de usuario.
	 * @return String con el admin encontrado en el token.
	 * @throws Exception en caso de token erroneo.
	 */
	public String getAdminFromToken(String token) throws Exception {
		try {
			final Claims claims = getAllClaimsFromToken(token.substring(7));
			return claims.get("admin") + "";
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Indica si el Token ha expirado.
	 * 
	 * @param token de la sesión de usuario.
	 * @return <code>false</code> si el token no ha expirado o <code>true</code> en
	 *         cualquier otro caso.
	 */
	public Boolean hasTokenExpired(String token) throws Exception {
		try {
			return isTokenExpired(token.substring(7));
		} catch (Exception e) {
			throw e;
		}
	}
}
