package school.faang.user_service.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.GoalStatus;

import java.util.List;

@Data
@NoArgsConstructor
public class GoalDto {
    private Long id;
    private Long parentId;
    private Long userId;
    private String title;
    GoalStatus status;
    List<Long> skillIds;
}