package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public int getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}
