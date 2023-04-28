package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UserDto owner;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
}