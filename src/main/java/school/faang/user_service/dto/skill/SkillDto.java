package school.faang.user_service.dto.skill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SkillDto {
    private Long id;
    private String title;
    private List<Long> userIds;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
