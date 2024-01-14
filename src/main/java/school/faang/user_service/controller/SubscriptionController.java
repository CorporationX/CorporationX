package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void getFollowers(long followeeId, UserFilterDto filter) {
        subscriptionService.getFollowers(followeeId, filter);
    }
}
