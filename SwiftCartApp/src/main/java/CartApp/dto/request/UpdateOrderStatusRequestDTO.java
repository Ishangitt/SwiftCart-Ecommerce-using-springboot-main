package CartApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequestDTO {
    
    @NotBlank(message = "Order status is required")
    private String orderStatus;

    private String notes;
}	
