package com.gruastremart.api.config.security.jwt;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Filtro para verificar un JWT firmado con HS256
 * usando la librería JJWT (io.jsonwebtoken).
 *
 * Se asume que tienes configurado un secreto en Supabase (JWT_SECRET)
 * y que Supabase firma con HS256.
 */
public class JwtSupabaseSecurityFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtSupabaseSecurityFilter.class);

    private final Key signingKey;

    private final UserRepository userRepository;

    public JwtSupabaseSecurityFilter(String supabaseJwtSecret, UserRepository userRepository) {
        // Convertimos el secreto en una llave HMAC
        this.signingKey = Keys.hmacShaKeyFor(supabaseJwtSecret.getBytes());
        // Inyectamos el repositorio de usuarios
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // 1) Tomamos el header "Authorization"
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String authorizationHeader = httpRequest.getHeader("Authorization");

            // 2) Verificamos que el header exista y sea "Bearer <token>"
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            // 3) Extraemos el token
            String token = authorizationHeader.substring(7);

            // 4) Parsear y verificar firma con JJWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 5) Extraer información relevante del JWT
            String email = claims.containsKey("email") ? claims.get("email").toString() : "unknown";

            // 6) Convertir roles en GrantedAuthority
            var user = userRepository.findByEmail(email).orElseThrow(() -> new ServiceException("User not found", 404));
            List<String> roles = List.of(user.getRole());
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());

            // 7) Crear UserDetails con el email
            UserDetails userDetails = User.withUsername(email)
                    .password("") // No hay password porque es un JWT
                    .authorities(authorities)
                    .build();

            // 8) Crear la autenticación
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);

            authToken.setDetails(claims); // Guardamos los claims en "details"

            // 9) Guardamos la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (JwtException e) {
            logger.error("Error al verificar el token JWT de Supabase con JJWT", e);
        } catch (Exception ex) {
            logger.error("Excepción en el filtro de seguridad", ex);
        }

        // Continuamos la cadena de filtros sin limpiar el contexto de seguridad
        chain.doFilter(request, response);
    }
}