package CartApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OrderItem Entity - Represents a single product in a placed order.
 * Stores the product details at the TIME OF PURCHASE as a historical record.
 * Mapped to the "order_items" table in the database.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // ==================== HISTORICAL PRODUCT DATA ====================

    /**
     * Product name at the time of purchase.
     */
    @Column(nullable = false, length = 200)
    private String productName;

    /**
     * Product SKU at the time of purchase.
     */
    @Column(length = 50)
    private String productSku;

    // ==================== QUANTITY AND PRICING ====================

    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price per unit at the time of purchase.
     */
    @Column(nullable = false)
    private double unitPrice;

    /**
     * Total value: unitPrice × quantity
     */
    @Column(nullable = false)
    private double subtotal;

    // ==================== LIFECYCLE CALLBACKS ====================

    @PrePersist
    public void calculateSubtotal() {
        this.subtotal = unitPrice * quantity;
    }

    // ==================== FACTORY METHOD ====================

    /**
     * Creates an OrderItem from a CartItem when placing an order.
     */
    public static OrderItem fromCartItem(CartItem cartItem) {
        return OrderItem.builder()
                .product(cartItem.getProduct())
                .productName(cartItem.getProduct().getName())
                .productSku(cartItem.getProduct().getSku())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}
