package com.shutterbug.productservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private  String id;
    private  String description;
    private  String name;
    private  BigDecimal price;
}
