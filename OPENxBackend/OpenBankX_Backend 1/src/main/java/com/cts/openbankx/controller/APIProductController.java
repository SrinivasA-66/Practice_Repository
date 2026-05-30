package com.cts.openbankx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.openbankx.model.APIProduct;
import com.cts.openbankx.service.APIProductService;

@RestController
@RequestMapping("/api/v1/api-products")
public class APIProductController {

    private final APIProductService apiProductService;

    public APIProductController(APIProductService apiProductService) {
        this.apiProductService = apiProductService;
    }

    @GetMapping
    public List<APIProduct> getAllProducts() {
        return apiProductService.findAll();
    }

    @GetMapping("/{id}")
    public APIProduct getProductById(@PathVariable Long id) {
        return apiProductService.findById(id);
    }

    @PostMapping
    public ResponseEntity<APIProduct> createProduct(@RequestBody APIProduct product) {
        return new ResponseEntity<>(
                apiProductService.save(product),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public APIProduct updateProduct(
            @PathVariable Long id,
            @RequestBody APIProduct product) {

        apiProductService.findById(id); // ensure exists
        product.setProductId(id);
        return apiProductService.save(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        apiProductService.delete(id);
        return ResponseEntity.noContent().build();
    }
}