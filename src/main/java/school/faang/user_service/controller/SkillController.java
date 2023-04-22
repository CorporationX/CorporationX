package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/skill")
    public SkillDto create(@RequestBody SkillDto skill) {
        if (validateSkill(skill)) {
            return skillService.create(skill);
        }
        throw new DataValidationException("Invalid skill is provided for creation");
    }

    @GetMapping("/skill/{userId}")
    public Page<SkillDto> getUserSkills(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return skillService.getUserSkills(userId, page, pageSize);
    }

    @GetMapping("/skill/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return skillService.getOfferedSkills(userId, page, pageSize);
    }

    @PostMapping("/skill/{id}/acquired/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private boolean validateSkill(SkillDto skill) {
        return true;
    }
}
