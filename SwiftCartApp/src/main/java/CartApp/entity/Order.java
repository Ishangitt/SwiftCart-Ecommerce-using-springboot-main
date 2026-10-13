package CartApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order Entity - Represents a customer's purchase order.
 * Mapped to the "orders" table in the database.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable order number. Format: "ORD-XXXXXXXX".
     * Generated automatically before saving.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // ==================== PRICE ====================

    /**
     * Total amount the customer pays (sum of all order item subtotals).
     */
    @Column(nullable = false)
    private double totalAmount;

    // ==================== ORDER STATUS ====================

    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = STATUS_CONFIRMED;

    // ==================== NOTES ====================

    @Column(length = 500)
    private String notes;

    // ==================== TIMESTAMPS ====================

    private LocalDateTime orderDate;

    // ==================== LIFECYCLE CALLBACKS ====================

    @PrePersist
    public void generateOrderNumber() {
        if (this.orderNumber == null) {
            this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
    }

    // ==================== HELPER METHODS ====================

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Calculates the total amount as the sum of all order item subtotals.
     */
    public void calculateTotals() {
        double total = 0.0;
        for (OrderItem item : orderItems) {
            total = total + item.getSubtotal();
        }
        this.totalAmount = total;
    }
}
