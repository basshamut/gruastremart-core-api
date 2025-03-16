package com.gruastremart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ContactFormCreateRequestDto {
    private String name;
    private String phone;
    private String email;
    private String message;


}
