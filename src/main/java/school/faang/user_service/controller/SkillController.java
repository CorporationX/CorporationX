package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.service.skill.SkillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/skill")
@Tag(name = "Skill", description = "Операции со скиллами пользователя")
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    @Operation(summary = "Создать скилл", description = "Создаёт новый скилл")
    public SkillDto create(@RequestBody Skill skill) {
        return skillService.create(skill);
    }

    @GetMapping("/userSkills/{userId}")
    @Operation(summary = "Получить скиллы пользователя", description = "Возвращает список скиллов пользователя по его ID")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/offeredSkills/{userId}")
    @Operation(summary = "Получить предлагаемые навыки", description = "Возвращает список предложенных скиллов для пользователя по его ID")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/acquireSkillFromOffers/{skillId}/{userId}")
    @Operation(summary = "Получить скилл из предложеных", description = "Приобретаетение скилла из предложенных")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId,@PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}