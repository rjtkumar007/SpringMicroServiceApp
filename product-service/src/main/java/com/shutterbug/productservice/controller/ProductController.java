package com.shutterbug.productservice.controller;

import com.shutterbug.productservice.dto.request.ProductRequest;
import com.shutterbug.productservice.dto.response.ProductResponse;
import com.shutterbug.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    
    @Autowired
    ProductService productService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct( @RequestBody ProductRequest productRequest ) {
        log.info("Inside createProduct from ProductController");
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> findAllProducts() {
        log.info("Inside findAllProducts from ProductController");
        return productService.getAllProducts();
    }
}
