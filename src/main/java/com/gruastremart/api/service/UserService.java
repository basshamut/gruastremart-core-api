package com.gruastremart.api.service;

import lombok.AllArgsConstructor;
import com.gruastremart.api.dto.AuthDto;
import com.gruastremart.api.persistance.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;

@Repository
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AuthDto loadUserByUsername(String username) {

        //TODO quemamos aqui el usuario para que no falle el login pero se debe obtener de la base de datos
        var pass = new String(Base64.getDecoder().decode("JDJhJDEwJGtoQmg0cE9FSHpJS0M3VTZNc1A5dmUyTU9hYkpFTFpzS0N3dG9YWkUyRDVzNFBJakRYdVdt"));
        return AuthDto.builder()
                .username("test@test.com")
                .password(pass)
                .authorities(new HashSet<>(List.of("ROLE_ADMIN")))
                .build();

//        final Set<String> authorities = new HashSet<>();
//
//        try {
//            var user = userRepository.findByEmail(username);
//
//            authorities.add("ROLE_" + user.getRole());
//            var pass = Arrays.toString(Base64.getDecoder().decode(user.getPassword()));
//
//            return new AuthDto(user.getEmail(), pass, authorities);
//        } catch (Exception e) {
//            return null;
//        }
    }

}
