package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;

    public void validateUser(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new DataValidationException("This user is not registered");
        }
    }

    public void validateUser(long followerId, long followeeId) {
        if (!userRepo.existsById(followeeId) || !userRepo.existsById(followerId)) {
            throw new DataValidationException("This user is not registered");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("IDs cannot be equal!");
        }
    }

    public void validateExistsSubscription(long followerId, long followeeId) {
        if (subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription already exists!");
        }
    }

    public void validateNonExistsSubscription(long followerId, long followeeId) {
        if (!subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription non exists!");
        }
    }
}