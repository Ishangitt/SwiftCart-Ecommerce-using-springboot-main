package CartApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart Entity - Represents a user's shopping cart.
 * Mapped to the "carts" table in the database.
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Total monetary value of all items in the cart.
     */
    @Column
    @Builder.Default
    private double totalAmount = 0.0;

    /**
     * Total number of items in the cart (sum of all quantities).
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer totalItems = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ==================== HELPER METHODS ====================

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
        recalculateTotals();
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        recalculateTotals();
    }

    public void recalculateTotals() {
        int totalItemsCount = 0;
        for (CartItem item : cartItems) {
            totalItemsCount = totalItemsCount + item.getQuantity();
        }
        this.totalItems = totalItemsCount;

        double total = 0.0;
        for (CartItem item : cartItems) {
            total = total + item.getSubtotal();
        }
        this.totalAmount = total;
    }

    public void clearCart() {
        for (CartItem cartItem : new ArrayList<>(cartItems)) {
            cartItem.setCart(null);
        }
        cartItems.clear();
        this.totalItems = 0;
        this.totalAmount = 0.0;
    }
}
