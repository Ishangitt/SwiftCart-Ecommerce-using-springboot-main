package CartApp.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private Integer stockQuantity;
    private String category;
    private String brand;
    private String imageUrl;
    private String sku;
    private Boolean isAvailable;
    private Boolean inStock;
}
