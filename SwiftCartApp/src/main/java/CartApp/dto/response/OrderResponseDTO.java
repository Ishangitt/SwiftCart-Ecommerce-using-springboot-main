package CartApp.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private String userEmail;
    private List<OrderItemResponseDTO> items;
    private Integer totalItems;
    private double totalAmount;
    private String status;
    private String notes;
    private LocalDateTime orderDate;
}
