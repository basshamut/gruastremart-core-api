package com.gruastremart.api.persistance.entity;

import com.gruastremart.api.utils.enums.OperatorVehiculeTypeEnum;
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
public class Operator {
    @Id
    private String id;
    private String userId;
    private OperatorVehiculeTypeEnum operatorVehiculeType;
}
