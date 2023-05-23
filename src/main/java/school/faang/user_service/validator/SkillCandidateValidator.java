package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillCandidate;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillCandidateValidator {

    private final SkillService skillService;

    public void validate(List<? extends SkillCandidate> skillCandidates) {
        List<Long> skillIds = retainUniqueOnly(skillCandidates);
        if (skillIds.size() != skillCandidates.size() || !skillService.areExistingSkills(skillIds)) {
            throw new DataValidationException("Invalid skills offered within a recommendation");
        }
    }

    private List<Long> retainUniqueOnly(List<? extends SkillCandidate> skillCandidates) {
        return skillCandidates.stream()
                .map(SkillCandidate::getSkillId)
                .distinct()
                .toList();
    }
}