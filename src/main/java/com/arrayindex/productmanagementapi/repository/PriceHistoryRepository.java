package com.arrayindex.productmanagementapi.repository;

import com.arrayindex.productmanagementapi.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    
    List<PriceHistory> findByProductIdOrderByChangedAtDesc(Long productId);
    
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.product.id = :productId AND ph.changedAt >= :since")
    List<PriceHistory> findByProductIdAndChangedAtAfter(@Param("productId") Long productId, 
                                                        @Param("since") java.time.LocalDateTime since);
}