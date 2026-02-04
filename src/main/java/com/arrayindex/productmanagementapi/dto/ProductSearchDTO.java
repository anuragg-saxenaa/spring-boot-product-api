package com.arrayindex.productmanagementapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDTO {
    
    private String name;
    private String category;
    
    @DecimalMin(value = "0.01", message = "Minimum price must be greater than 0")
    private Double minPrice;
    
    @DecimalMin(value = "0.01", message = "Maximum price must be greater than 0")
    private Double maxPrice;
    
    private Boolean isActive;
    private String sortBy = "name";
    private String sortDirection = "ASC";
    
    @Min(value = 0, message = "Page number cannot be negative")
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 10;
}