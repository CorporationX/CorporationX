package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

@Data
@NoArgsConstructor
public class EventDto {
    private Long id;
    @NotBlank(message = "title should not be blank")
    private String title;
    @NotNull(message = "startDate should not be null")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "ownerId should not be null")
    private Long ownerId;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
}