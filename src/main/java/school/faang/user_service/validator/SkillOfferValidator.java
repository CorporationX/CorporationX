package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillOfferValidator {

    private final SkillService skillService;

    public void validate(List<SkillOfferDto> skillOffers) {
        List<Long> skillIds = retainUniqueOnly(skillOffers);
        if (skillIds.size() != skillOffers.size() || skillService.areExistingSkills(skillIds)) {
            throw new DataValidationException("Invalid skills offered within a recommendation");
        }
    }

    private List<Long> retainUniqueOnly(List<SkillOfferDto> skillOffers) {
        return skillOffers.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();
    }
}
