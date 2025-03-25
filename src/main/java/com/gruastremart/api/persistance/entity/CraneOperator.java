package com.gruastremart.api.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "operators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CraneOperator {
    @Id
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String identificationNumber;
    private String birthDate;
    private Boolean active;
}
