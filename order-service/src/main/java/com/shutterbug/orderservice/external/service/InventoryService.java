package com.shutterbug.orderservice.external.service;

import com.shutterbug.orderservice.dto.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryService {
    
    @GetMapping("/api/inventory")
    List<InventoryResponse> getInventory(@RequestParam List<String> skuCode);
}
