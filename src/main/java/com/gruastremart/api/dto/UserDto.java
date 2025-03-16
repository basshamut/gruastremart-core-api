package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private String id;
    private String email;
    private String password;
    private String role;// CLIENT / CRANE_OPERATOR / ADMIN
}
