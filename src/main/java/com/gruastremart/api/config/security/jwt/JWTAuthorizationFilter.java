package com.gruastremart.api.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruastremart.api.config.security.SecurityProperties;
import com.gruastremart.api.controller.handler.json.HttpErrorInfoJson;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.utils.FormatUtils;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

import static com.gruastremart.api.utils.Constants.HEADER_AUTHORIZACION_KEY;
import static com.gruastremart.api.utils.Constants.TOKEN_BEARER_PREFIX;

@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final SecurityProperties securityProperties;

    public JWTAuthorizationFilter(AuthenticationManager authManager, SecurityProperties securityProperties) {
        super(authManager);
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        final String header = req.getHeader(HEADER_AUTHORIZACION_KEY);
        if (header == null || !header.startsWith(TOKEN_BEARER_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        final UsernamePasswordAuthenticationToken authentication;
        try {
            authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } catch (ServiceException ex) {
            final ObjectMapper mapper = new ObjectMapper();
            final HttpErrorInfoJson httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.UNAUTHORIZED, req, ex);

            res.setContentType("application/json;charset=UTF-8");
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.getWriter().write(mapper.writeValueAsString(httpErrorInfoDto));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZACION_KEY);
        if (token != null) {
            // Se procesa el token y se recupera el usuario.
            token = token.replace(TOKEN_BEARER_PREFIX, "");
            try {
                String user = Jwts.parser()
                        .setSigningKey(securityProperties.getSuperSecretKey().getBytes())
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            } catch (Exception exception) {
                throw new ServiceException("Authentication was not posible: " + exception.getMessage(), 403);
            }

            return null;
        }
        return null;
    }
}