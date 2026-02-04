package com.arrayindex.productmanagementapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "old_price", nullable = false)
    private Double oldPrice;
    
    @Column(name = "new_price", nullable = false)
    private Double newPrice;
    
    @Column(name = "change_reason")
    private String changeReason;
    
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
    
    @Column(name = "changed_by")
    private String changedBy;

    // Constructors
    public PriceHistory() {}

    public PriceHistory(Long id, Product product, Double oldPrice, Double newPrice, 
                       String changeReason, LocalDateTime changedAt, String changedBy) {
        this.id = id;
        this.product = product;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changeReason = changeReason;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Double getOldPrice() { return oldPrice; }
    public void setOldPrice(Double oldPrice) { this.oldPrice = oldPrice; }

    public Double getNewPrice() { return newPrice; }
    public void setNewPrice(Double newPrice) { this.newPrice = newPrice; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceHistory that = (PriceHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "PriceHistory{" +
                "id=" + id +
                ", oldPrice=" + oldPrice +
                ", newPrice=" + newPrice +
                ", changeReason='" + changeReason + '\'' +
                ", changedAt=" + changedAt +
                ", changedBy='" + changedBy + '\'' +
                '}';
    }
}