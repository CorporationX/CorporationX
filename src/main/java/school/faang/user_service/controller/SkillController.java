package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@AllArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(Skill skill) {
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId){
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}