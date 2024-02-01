package com.shutterbug.inventoryservice.controller;

import com.shutterbug.inventoryservice.dto.response.InventoryResponse;
import com.shutterbug.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getAllInventory ( @RequestParam List<String> skuCode ) {
        return inventoryService.isInStock(skuCode);
    }
}
