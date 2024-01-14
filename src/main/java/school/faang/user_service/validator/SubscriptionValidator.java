package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepo;

    public void validateUserExists(long userId) {
        if (subscriptionRepo.findByFollowerId(userId).findAny().isEmpty()) {
            throw new DataValidationException("User with id: " + userId + " is not registered");
        }
    }
}