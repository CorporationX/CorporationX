package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@AllArgsConstructor
@NoArgsConstructor
class SkillValidator {
    private SkillRepository skillRepository;

    public boolean validateSkill(Skill skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("please enter the title of the skill");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(String.format("skill \"%s\" already exists", skill.getTitle()));
        }
        return true;
    }
}