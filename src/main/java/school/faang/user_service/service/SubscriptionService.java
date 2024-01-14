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

    public int getFollowersCount(long followeeId) {
        subscriptionValidator.validateUserExists(followeeId);
        return subscriptionRepo.findFollowersAmountByFolloweeId(followeeId);
    }
}