package CartApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * CartItem Entity - Represents a single product entry in a shopping cart.
 * Mapped to the "cart_items" table in the database.
 */
@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = { "cart_id", "product_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Number of units of this product in the cart.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Price per unit at the time the product was added to cart.
     */
    @Column(nullable = false)
    private double unitPrice;

    /**
     * Total value of this cart item: unitPrice * quantity
     */
    @Column
    private double subtotal;

    /**
     * When this item was added to the cart.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime addedAt;

    /**
     * When this item was last modified (quantity changed).
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ==================== LIFECYCLE CALLBACKS ====================

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        this.subtotal = unitPrice * quantity;
    }

    // ==================== HELPER METHODS ====================

    public void incrementQuantity(int amount) {
        this.quantity += amount;
        calculateSubtotal();
    }

    public void decrementQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
        calculateSubtotal();
    }
}
