package com.gruastremart.api.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.Key;
import java.util.List;

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

    public JwtSupabaseSecurityFilter(String supabaseJwtSecret) {
        // Convertimos el secreto en una llave HMAC
        this.signingKey = Keys.hmacShaKeyFor(supabaseJwtSecret.getBytes());
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
                // Seguimos la cadena sin autenticar
                chain.doFilter(request, response);
                return;
            }

            // 3) Extraemos el token
            String token = authorizationHeader.substring(7);

            // 4) Parsear y verificar firma con JJWT
            Claims claims = null;

            // Sin verificación del issuer
            claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 5) Si llega aquí, significa que la firma es válida
            // y el token no está expirado ni es inválido.
            // Creamos una autenticación "sencilla" para Spring Security.

            // Suele interesar sacar el "sub" como ident. del usuario
            String subject = claims.getSubject();

            // Podrías extraer más claims (ej. roles) y mapearlos a GrantedAuthorities.
            // Aquí sólo usamos un rol ficticio "SIMPLE_AUTHORITY".
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    subject,
                    null,
                    List.of(new SimpleGrantedAuthority("SIMPLE_AUTHORITY")));

            // 6) Guardamos la autenticación en el contexto de seguridad.
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (JwtException e) {
            // Cualquier problema con la firma, expiración, etc.
            logger.error("Error al verificar el token JWT de Supabase con JJWT", e);
        } catch (Exception ex) {
            // Otras excepciones
            logger.error("Excepción en el filtro de seguridad", ex);
        }

        // Continuamos la cadena de filtros
        chain.doFilter(request, response);

        // Al terminar, limpiamos el contexto de seguridad
        SecurityContextHolder.clearContext();
    }
}
