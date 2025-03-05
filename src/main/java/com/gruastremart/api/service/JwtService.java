package com.gruastremart.api.service;

import com.gruastremart.api.config.security.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final SecurityProperties securityProperties;

    public String generateToken(Authentication auth) {
        var cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + securityProperties.getTokenExpirationTimeInMinutes());

        return buildToken(auth, cal);
    }

    private String buildToken(Authentication auth, Calendar cal) {
        var preToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setIssuer(securityProperties.getIssuerInfo())
                .setSubject(auth.getName())
                .setExpiration(cal.getTime())
                .signWith(SignatureAlgorithm.HS512, securityProperties.getSuperSecretKey().getBytes());
        preToken.claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return preToken.compact();
    }
}