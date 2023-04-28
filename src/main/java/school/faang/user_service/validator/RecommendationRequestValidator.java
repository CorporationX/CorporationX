package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {

    private static final int REQUEST_INTERVAL_MONTHS = 6;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validate(RecommendationRequestDto req) {
        if (req.getRequesterId() == req.getReceiverId()) {
            throw new DataValidationException("Author and receiver of a recommendation request cannot be the same person");
        }
        if (!userService.existsById(req.getRequesterId())) {
            throw new DataValidationException("Author with id " + req.getRequesterId() + " does not exist");
        }
        if (!userService.existsById(req.getReceiverId())) {
            throw new DataValidationException("Receiver with id " + req.getReceiverId() + " does not exist");
        }
        checkForPrevious(req);
    }

    private void checkForPrevious(RecommendationRequestDto req) {
        recommendationRequestRepository.findByRequesterIdAndReceiverId(req.getRequesterId(), req.getReceiverId())
                .ifPresent(existing -> {
                    if (existing.getCreatedAt().plusMonths(REQUEST_INTERVAL_MONTHS).isAfter(LocalDateTime.now())) {
                        throw new DataValidationException(String.format(
                                ErrorMessage.RECOMMENDATION_REQUEST_ALREADY_EXISTS.getMessage(),
                                req.getRequesterId(),
                                req.getReceiverId(),
                                REQUEST_INTERVAL_MONTHS
                        ));
                    }
                });
    }
}
