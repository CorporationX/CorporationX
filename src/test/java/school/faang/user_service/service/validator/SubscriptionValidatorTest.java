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
    private SubscriptionRepository subscriptionRepo;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testUnfollowedSuccess() {
        subscriptionService.unfollowUser(10L, 20L);
        Mockito.verify(subscriptionRepo, Mockito.times(1))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testFollowedSuccess() {
        subscriptionService.followUser(10L, 20L);
        Mockito.verify(subscriptionRepo, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testFollowingCount() {
        Mockito.when(subscriptionRepo.findFolloweesAmountByFollowerId(1L)).thenReturn(5);
        Assertions.assertEquals(5, subscriptionService.getFollowingCount(1L));
    }

    @Test
    public void testFollowersCount() {
        Mockito.when(subscriptionRepo.findFollowersAmountByFolloweeId(1L)).thenReturn(5);
        Assertions.assertEquals(5, subscriptionService.getFollowersCount(1L));
    }
}