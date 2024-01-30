package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> getRelatedSkills(List<Long> relatedSkillIds) {
        return skillRepository.findAllById(relatedSkillIds);
    }

}
