package com.shutterbug.productservice.entity;

import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    private String id;
    
    @Field(name = "name")
    private String productName;   
    
    @Field(name = "description")
    private String productDescription;

    private BigDecimal price;

}
