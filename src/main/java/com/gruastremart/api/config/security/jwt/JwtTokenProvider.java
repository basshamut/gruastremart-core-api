package com.gruastremart.api.config.security.jwt;

import com.gruastremart.api.config.security.SecurityProperties;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityProperties securityProperties;

    public Authentication getAuthentication(String token) {
        Key signingKey = Keys.hmacShaKeyFor(securityProperties.getSupabaseSecret().getBytes());//Here get the supabase secret from yaml file

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.containsKey("email") ? claims.get("email").toString() : "unknown";

        var user = userRepository.findByEmail(email).orElseThrow(() -> new ServiceException("User not found", 404));
        List<String> roles = List.of(user.getRole().getValue());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());

        UserDetails userDetails = User.withUsername(email)
                .password("") // No hay password porque es un JWT
                .authorities(authorities)
                .build();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);

        authToken.setDetails(claims); // Guardamos los claims en "details"
        return new UsernamePasswordAuthenticationToken(email, token, authorities);
    }
}

//TODO chequear bien el tema de los roles y permisos
