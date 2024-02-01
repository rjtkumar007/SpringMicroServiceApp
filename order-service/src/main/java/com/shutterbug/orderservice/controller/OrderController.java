package com.shutterbug.orderservice.controller;

import com.shutterbug.orderservice.dto.request.OrderRequest;
import com.shutterbug.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController ( OrderService orderService ) {
        this.orderService = orderService;
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder( @RequestBody  OrderRequest orderRequest) {
        log.info("Inside Create Order from Order Controller");
        orderService.placeOrder(orderRequest);
        return "Successfully placed order.";
    }
}
