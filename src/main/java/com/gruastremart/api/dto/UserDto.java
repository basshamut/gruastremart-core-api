package com.gruastremart.api.dto;

import com.gruastremart.api.utils.enums.Role;
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
    private String supabaseId;
    private String email;
    private String name;
    private String lastName;
    private String phone;
    private String address;
    private String identificationNumber;
    private String birthDate;
    private Role role;
    private Boolean active;
}
