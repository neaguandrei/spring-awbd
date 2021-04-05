package com.fmi.product.service;

import com.fmi.common.exception.NotFoundException;
import com.fmi.product.mapper.ProductMapper;
import com.fmi.product.model.Product;
import com.fmi.dao.entity.ProductEntity;
import com.fmi.dao.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public Page<Product> getProducts(String searchKey, ProductEntity.ProductCategory productCategory, Pageable pageable) {
        if (Objects.nonNull(searchKey) && !searchKey.isEmpty()) {
            if (productCategory != null && productCategory != ProductEntity.ProductCategory.NONE) {
                return productRepository.findAllByNameAndCategory(searchKey, productCategory, pageable)
                        .map(productMapper::mapFromEntity);
            }
            return productRepository.findAllByName(searchKey, pageable)
                    .map(productMapper::mapFromEntity);
        }
        return productRepository.findAll(pageable)
                .map(productMapper::mapFromEntity);
    }

    public Product getProductById(Long id) throws NotFoundException {
        Optional<Product> optional = productRepository.findById(id).map(productMapper::mapFromEntity);
        if (!optional.isPresent()) {
            throw new NotFoundException("Product doesn't exist!");
        }

        return optional.get();
    }
}
