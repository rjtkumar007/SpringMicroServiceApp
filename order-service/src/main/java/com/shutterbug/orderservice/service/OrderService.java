package com.shutterbug.orderservice.service;
import com.shutterbug.orderservice.dto.request.OrderItemDto;
import com.shutterbug.orderservice.dto.request.OrderRequest;
import com.shutterbug.orderservice.dto.response.InventoryResponse;
import com.shutterbug.orderservice.entity.Order;
import com.shutterbug.orderservice.entity.OrderItem;
import com.shutterbug.orderservice.event.OrderPlaceEvent;
import com.shutterbug.orderservice.external.service.InventoryService;
import com.shutterbug.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    private final InventoryService inventoryService;
    @Autowired
    KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;
    
    
    @Autowired
    public OrderService ( OrderRepository orderRepository, WebClient.Builder webClientBuilder, InventoryService inventoryService ) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.inventoryService = inventoryService;
    }

    public String placeOrder ( OrderRequest orderRequest ) {
        var orderBuilder = Order.builder();
        var orderItemList =  orderRequest.getOrderItemDtoList().stream().map(this:: mapOrderItemRequest).toList();
        orderBuilder.orderItemList(orderItemList).orderNumber(UUID.randomUUID().toString());
        
        List<String> skuCodes = orderRequest.getOrderItemDtoList().stream().map(OrderItemDto::getSkuCode).toList();
//        var response = webClientBuilder.build().get()
//                .uri("http://INVENTORY-SERVICE/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
//                .retrieve()
//                .bodyToMono(InventoryResponse[].class)
//                .block();
        var response = inventoryService.getInventory(skuCodes);
        var isAllInStock = response.size()> 0 ? response.stream().allMatch(InventoryResponse::isInStock): false;
        if(isAllInStock) {
            var order = orderBuilder.build();
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlaceEvent(order.getOrderNumber()));
            return "Successfully placed order.";
        } else {
            throw new IllegalArgumentException("Product is not in stock, Please try again later ");
        }
    }

    private OrderItem mapOrderItemRequest (OrderItemDto orderItemDto ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemDto.getId());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setSkuCode(orderItemDto.getSkuCode());
        return orderItem;
    }
}