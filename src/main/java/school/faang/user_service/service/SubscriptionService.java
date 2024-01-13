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

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateExistsSubscription(followerId, followeeId);
        subscriptionValidator.validateExistsUser(followerId);
        subscriptionValidator.validateExistsUser(followeeId);
        subscriptionValidator.validateUserSubscriptionToYourself(followerId, followeeId);
        subscriptionRepo.followUser(followerId, followeeId);
    }
}
