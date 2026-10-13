package CartApp.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private String productSku;
    private double unitPrice;
    private Integer quantity;
    private double subtotal;
    private Integer availableStock;
    private LocalDateTime addedAt;
}
