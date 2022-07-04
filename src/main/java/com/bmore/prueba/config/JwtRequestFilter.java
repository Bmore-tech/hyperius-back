package com.bmore.prueba.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bmore.prueba.web.service.impl.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// Solicitudes sin filtro
		if (request.getRequestURI().contains("/login/authenticate") || request.getRequestURI().contains("/v2/api-docs")
				|| request.getRequestURI().contains("/swagger-ui.html")
				|| request.getRequestURI().contains("/configuration/ui")
				|| request.getRequestURI().contains("/configuration/security")
				|| request.getRequestURI().contains("/swagger-resources")
				|| request.getRequestURI().contains("/webjars")) {
			chain.doFilter(request, response);
		} else {
			final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
			String username = null;
			String jwtToken = null;
			
			// Valída si tiene token y si empieza con el prefijo.
			if (token != null && token.startsWith("Bearer ")) {
				jwtToken = token.substring(7);

				try {
					username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				} catch (IllegalArgumentException e) {
					response.sendError(HttpServletResponse.SC_CONFLICT, "Unable to get JWT Token");
				} catch (ExpiredJwtException e) {
					response.sendError(HttpServletResponse.SC_CONFLICT, "JWT Token has expired");
				} catch (Exception e) {
					response.sendError(HttpServletResponse.SC_CONFLICT, "Invalid JWT Token or Undefined token");
				}
			} else {
				// Error si no tiene el token.
				response.sendError(HttpServletResponse.SC_CONFLICT, "JWT Token does not begin with Bearer String");
			}

			// 
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				// Valída los roles del usuario.
				if (jwtUserDetailsService.hasRole(username)) {
					UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
					try {
						if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
							UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
									userDetails, null, userDetails.getAuthorities());
							usernamePasswordAuthenticationToken
									.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
							chain.doFilter(request, response);
						}
					} catch (Exception e) {
						response.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
					}
				} else {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized [Rol not Available]");
				}
			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			}
		}
	}
}
