package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);

    @InjectMocks
    private SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository);

    @Test
    public void testAlreadyFollowedThrowsException() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(1L,2L));
    }

    @Test
    public void testFollowedSuccess() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(10L, 20L))
                .thenReturn(false);
        subscriptionService.followUser(10L, 20L);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(Mockito.anyLong(),Mockito.anyLong());
    }
}
