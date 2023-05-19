package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MentorshipRequestDto {
    private Long id;
    @NotBlank(message = "description should not be blank")
    private String description;
    @NotNull(message = "requesterId should not be null")
    private Long requesterId;
    @NotNull(message = "receiverId should not be null")
    private Long receiverId;
}
