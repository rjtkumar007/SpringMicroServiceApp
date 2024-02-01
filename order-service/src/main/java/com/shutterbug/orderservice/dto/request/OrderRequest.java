package com.shutterbug.orderservice.dto.request;

import com.shutterbug.orderservice.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private List<OrderItemDto> orderItemDtoList;
}
