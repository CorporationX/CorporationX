package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionDto {
    @NotBlank(message = "reason should not be blank")
    private String reason;
}
