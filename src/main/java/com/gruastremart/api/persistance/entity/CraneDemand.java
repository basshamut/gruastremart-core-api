package com.gruastremart.api.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "crane_demands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CraneDemand {
    @Id
    private String id;
    private String description;
    private Date dueDate;
    private String state;
    private String userId;
}
