package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/skill")
    public SkillDto create(@RequestBody @Validated SkillDto skill) {
        return skillService.create(skill);
    }

    @GetMapping("/skill/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return skillService.getUserSkills(userId, page, pageSize);
    }

    @GetMapping("/skill/{userId}/offered")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return skillService.getOfferedSkills(userId, page, pageSize);
    }

    @PostMapping("/skill/{userId}/offered/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}