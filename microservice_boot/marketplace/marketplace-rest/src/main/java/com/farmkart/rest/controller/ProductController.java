package com.farmkart.rest.controller;

import com.farmkart.client.dto.catalog.CreateProductRequest;
import com.farmkart.client.dto.catalog.ProductResponse;
import com.farmkart.service.catalog.ProductService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Catalog")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> listProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) BigDecimal min_price,
            @RequestParam(required = false) BigDecimal max_price,
            @RequestParam(required = false) Boolean organic,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(productService.search(q, category, min_price, max_price, organic, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        return ApiResponse.ok(productService.getById(id));
    }

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.ok("Product created", productService.create(request));
    }
}
