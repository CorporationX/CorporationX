package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
}
