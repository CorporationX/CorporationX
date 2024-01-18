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
    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validate(followerId, followeeId);
        subscriptionRepo.followUser(followerId, followeeId);
    }
}
