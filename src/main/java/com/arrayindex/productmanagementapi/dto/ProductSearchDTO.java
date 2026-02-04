package com.arrayindex.productmanagementapi.dto;

import jakarta.validation.constraints.*;

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

    // Constructors
    public ProductSearchDTO() {}

    public ProductSearchDTO(String name, String category, Double minPrice, Double maxPrice, 
                           Boolean isActive, String sortBy, String sortDirection, int page, int size) {
        this.name = name;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.isActive = isActive;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.page = page;
        this.size = size;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}