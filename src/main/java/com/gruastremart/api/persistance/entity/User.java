package com.gruastremart.api.persistance.entity;

import com.gruastremart.api.utils.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String supabaseId;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String identificationNumber;
    private String birthDate;
    private Role role;
    private Boolean active;
}
