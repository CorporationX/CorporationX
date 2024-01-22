package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final SubscriptionValidator subscriptionValidator;

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUser(followerId, followeeId);
        subscriptionValidator.validateNonExistsSubscription(followerId, followeeId);
        subscriptionRepo.unfollowUser(followerId, followeeId);
    }

    @Transactional
    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateUser(followerId, followeeId);
        subscriptionValidator.validateExistsSubscription(followerId, followeeId);
        subscriptionRepo.followUser(followerId, followeeId);
    }

    public int getFollowersCount(long followeeId) {
        subscriptionValidator.validateUser(followeeId);
        return subscriptionRepo.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        subscriptionValidator.validateUser(followerId);
        return subscriptionRepo.findFolloweesAmountByFollowerId(followerId);
    }
}