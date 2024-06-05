package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    private Long id;
    private Long parentGoalId;
    private String title;
    private GoalStatus status;
    private LocalDateTime deadLine;
    private Long mentorId;
    private List<Long> userIds;
}