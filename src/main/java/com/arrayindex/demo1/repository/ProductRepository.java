package com.arrayindex.demo1.repository;

import com.arrayindex.demo1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
} 