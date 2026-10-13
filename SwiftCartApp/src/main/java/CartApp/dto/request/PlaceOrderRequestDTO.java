package CartApp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
