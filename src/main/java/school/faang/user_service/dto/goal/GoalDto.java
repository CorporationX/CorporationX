package school.faang.user_service.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@NoArgsConstructor
public class GoalDto {
    private Long id;
    private Long parentId;
    private List<Long> userIds;
    private String title;
    private GoalStatus status;
    private List<Long> skillIds;
}