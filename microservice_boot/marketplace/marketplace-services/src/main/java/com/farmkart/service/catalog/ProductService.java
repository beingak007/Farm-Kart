package com.farmkart.service.catalog;

import com.farmkart.client.dto.catalog.CreateProductRequest;
import com.farmkart.client.dto.catalog.ProductResponse;
import com.farmkart.repository.entity.Product;
import com.farmkart.repository.ProductRepository;
import com.farmkart.starter.common.exception.BusinessException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product();
        product.setVendorId(request.vendorId());
        product.setCategoryId(request.categoryId());
        product.setTitle(request.title());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setUnit(request.unit());
        product.setStockQuantity(request.stockQuantity());
        product.setMinOrderQuantity(request.minOrderQuantity());
        product.setImages(request.images() != null ? request.images() : List.of());
        product.setOrganic(request.organic());
        product.setHarvestDate(request.harvestDate());
        product.setStorageInstructions(request.storageInstructions());
        productRepository.save(product);
        return toResponse(product);
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Product not found"));
        return toResponse(product);
    }

    public Page<ProductResponse> search(
            String q,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean organic,
            Pageable pageable) {

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));

            if (q != null && !q.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (organic != null) {
                predicates.add(cb.equal(root.get("organic"), organic));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getVendorId(),
                product.getCategoryId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getUnit(),
                product.getStockQuantity(),
                product.getMinOrderQuantity(),
                product.getImages(),
                product.isOrganic(),
                product.getHarvestDate(),
                product.getStorageInstructions());
    }
}
