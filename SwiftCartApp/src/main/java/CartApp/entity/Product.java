package CartApp.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Product Entity - Represents items available for sale in the store.
 * Mapped to the "products" table in the database.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    /**
     * Product price in rupees.
     */
    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String brand;

    @Column(length = 500)
    private String imageUrl;

    @Column(unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
}
