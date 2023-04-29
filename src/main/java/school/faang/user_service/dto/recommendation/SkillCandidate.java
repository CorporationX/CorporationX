package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public abstract class SkillCandidate {
    private Long id;
    private long skillId;
}
