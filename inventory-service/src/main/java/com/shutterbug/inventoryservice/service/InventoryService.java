package com.shutterbug.inventoryservice.service;

import com.shutterbug.inventoryservice.dto.response.InventoryResponse;
import com.shutterbug.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService ( InventoryRepository inventoryRepository ) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock ( List<String> skuCode ) {
        
        return  inventoryRepository.findBySkuCodeIn(skuCode)
                .stream().map(
                inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .inStock(inventory.getQuantity()>0)
                        .build()
                ).toList();
    }
}
