package com.arrayindex.productmanagementapi.repository;

import com.arrayindex.productmanagementapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
} 