package com.shutterbug.productservice.service;

import com.shutterbug.productservice.dto.request.ProductRequest;
import com.shutterbug.productservice.dto.response.ProductResponse;
import com.shutterbug.productservice.entity.Product;
import com.shutterbug.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {
    
    
    private final ProductRepository productRepository;

    @Autowired
    public ProductService ( ProductRepository productRepository ) {
        this.productRepository = productRepository;
    }


    public void findProductById ( String id ) {
    }

    public void createProduct ( ProductRequest productRequest ) {
        Product product = Product.builder()
                .productDescription(productRequest.description())
                .productName(productRequest.name())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product {} is saved ", product.getId());
    }

    public List<ProductResponse> getAllProducts () {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse ( Product data ) {
        return ProductResponse.builder()
                .id(data.getId())
                .description(data.getProductDescription())
                .name(data.getProductName())
                .price(data.getPrice()).build();
    }
}
