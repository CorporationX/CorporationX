package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final SubscriptionValidator subscriptionValidator;

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateNonExistsSubscription(followerId, followeeId);
        subscriptionValidator.validateExistsUser(followerId);
        subscriptionValidator.validateExistsUser(followeeId);
        subscriptionValidator.validateUserUnfollowToYourself(followerId, followeeId);
        subscriptionRepo.unfollowUser(followerId, followeeId);
    }
}