package CartApp.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private java.util.List<CartItemResponseDTO> items;
    private Integer totalItems;
    private double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
