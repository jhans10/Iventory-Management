package com.product.service.repository;

import com.product.service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface RepositoryProduct extends JpaRepository<Product, Long> {
}
