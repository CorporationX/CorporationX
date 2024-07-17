package school.faang.user_service.service.skill;

import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    SkillDto create(Skill skill);

    List<SkillDto> getUserSkills(long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    SkillDto acquireSkillFromOffers(long skillId, long userId);

    Skill findById(long skillId);

    void existsById(long skillId);
}
