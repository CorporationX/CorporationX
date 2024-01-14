package school.faang.user_service.service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testGetFollowingCount() {
        long followerId = 1L;
        int count = 111;

        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(count);

        int followingCount = subscriptionService.getFollowingCount(followerId);

        Assertions.assertEquals(count, followingCount);
    }
}
