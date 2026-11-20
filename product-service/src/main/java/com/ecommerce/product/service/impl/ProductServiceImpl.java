package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.BusinessException;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper  productMapper;

    @Override
    @Transactional
    public List<ProductResponse> create(List<ProductRequest> productRequests) {
        if (productRequests == null || productRequests.isEmpty()) {
            throw new BusinessException("INVALID_PRODUCT_LIST", "Product list must not be empty");
        }

        Set<String> uniqueNames = new HashSet<>();

        for (ProductRequest request : productRequests) {
            String normalizedName = request.getName().trim().toLowerCase();

            if (!uniqueNames.add(normalizedName)) {
                throw new BusinessException("PRODUCT_ALREADY_EXISTS", "Product with name " + request.getName() + " already exists in request");
            }
        }

        List<String> names = productRequests.stream().map(request -> request.getName().trim()).toList();

        List<Product> existingProducts = productRepository.findByNameInIgnoreCase(names);

        if (!existingProducts.isEmpty()) {
            throw new BusinessException("PRODUCT_ALREADY_EXISTS", "Product name already exists");
        }

        Set<String> categoryIds = productRequests.stream().map(ProductRequest::getCategoryId).collect(Collectors.toSet());
        Map<String, Category> categories = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        for (String categoryId : categoryIds) {
            if (!categories.containsKey(categoryId)) {
                throw new BusinessException("CATEGORY_DOES_NOT_EXIST", "Category with id " + categoryId + " not found");
            }
        }

        List<Product> products = productRequests.stream()
                .map(productRequest -> {
                    Product product = productMapper.toEntity(productRequest);

                    product.setInitBy("test-id");
                    product.setCreatedDate(LocalDateTime.now());
                    product.setCategory(categories.get(productRequest.getCategoryId()));

                    return product;
                })
                .toList();

        List<Product> savedProducts = productRepository.saveAll(products);

        return savedProducts.stream().map(productMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductResponse getById(String id) {
        Product product  = productRepository.findByIdWithCategory(id).orElseThrow(() ->
                new ResourceNotFoundException("Product with id " + id + " not found")
        );

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAllWithCategory()
                .stream().map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse update(String id, ProductRequest productRequest) {
        Product product  = productRepository.findByIdWithCategory(id).orElseThrow(() ->
                new ResourceNotFoundException("Product with id " + id + " not found")
        );

        Category category  = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + productRequest.getCategoryId() + " not found")
        );

        String name = productRequest.getName().trim();

        if (!name.equalsIgnoreCase(product.getName()) && productRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessException("PRODUCT_ALREADY_EXISTS", "Product with name " + name + " already exists");
        }

        Product productUpdate = productMapper.toEntity(productRequest);
        productUpdate.setId(product.getId());
        productUpdate.setInitBy(product.getInitBy());
        productUpdate.setCreatedDate(product.getCreatedDate());
        productUpdate.setUpdateBy("test-id");
        productUpdate.setUpdatedDate(LocalDateTime.now(ZoneId.systemDefault()));
        productUpdate.setCategory(category);

        Product updatedProduct = productRepository.save(productUpdate);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Product product  = productRepository.findByIdWithCategory(id).orElseThrow(() ->
                new ResourceNotFoundException("Product with id " + id + " not found")
        );

        productRepository.delete(product);
    }
}
