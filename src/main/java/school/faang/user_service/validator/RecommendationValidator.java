package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.RecommendationRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private static final int MONTHS_TILL_NEXT_RECOMMENDATION = 6;

    private final RecommendationRepository recommendationRepository;

    public void validate(RecommendationDto recommendation) {
        if (recommendation.getId() == null) {
            recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                            recommendation.getAuthorId(),
                            recommendation.getReceiverId()
                    ).map(rec -> rec.getCreatedAt().plusMonths(MONTHS_TILL_NEXT_RECOMMENDATION).isBefore(LocalDateTime.now()))
                    .ifPresent(early -> {
                        throw new DataValidationException("A new recommendation may be introduced only after " +
                                MONTHS_TILL_NEXT_RECOMMENDATION + " months");
                    });
        }
    }
}
