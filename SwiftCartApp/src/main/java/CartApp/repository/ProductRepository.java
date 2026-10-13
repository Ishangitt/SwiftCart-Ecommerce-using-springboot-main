package CartApp.repository;

import CartApp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by SKU
    Optional<Product> findBySku(String sku);

    // Check if SKU exists
    boolean existsBySku(String sku);

    // Find all available products
    List<Product> findByIsAvailableTrue();

    // Find by category
    List<Product> findByCategoryIgnoreCase(String category);

    // Find products within price range
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Search products by name or description
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.isAvailable = true")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    // Update stock quantity
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // Increase stock quantity
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.isAvailable = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}
