package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
}
