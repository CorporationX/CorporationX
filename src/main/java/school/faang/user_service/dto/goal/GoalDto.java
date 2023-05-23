package school.faang.user_service.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GoalDto {
    private Long id;
    private String description;
    private Long parentId;
    private String title;
    private GoalStatus status = GoalStatus.ACTIVE;
    private LocalDateTime deadline;
    private List<Long> skillIds = new ArrayList<>();
    private Long mentorId;
    private List<Long> userIds = new ArrayList<>();
}