package com.shutterbug.inventoryservice;

import com.shutterbug.inventoryservice.entity.Inventory;
import com.shutterbug.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadInventoryData( InventoryRepository inventoryRepository ) {
		return args -> {
			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("iphone_15");
			inventory1.setQuantity(100);
			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iphone_16");
			inventory2.setQuantity(0);
			List<Inventory> inventoryList = Arrays.asList(inventory1, inventory2);
			inventoryRepository.saveAll(inventoryList);
		};
	}
}
