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

    public void validate(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't unfollow to yourself!");
        }
        if (!subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription non exists!");
        }
        if (!userRepo.existsById(followeeId) || !userRepo.existsById(followerId)) {
            throw new DataValidationException("This user is not registered");
        }
    }
}