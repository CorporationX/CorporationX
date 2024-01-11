package school.faang.user_service.service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService = Mockito.mock(SubscriptionService.class);

    @InjectMocks
    private SubscriptionController subscriptionController = new SubscriptionController(subscriptionService);

    @Test
    public void testFollowUserThrowsException() {
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionController.followUser(1, 1));
    }

    @Test
    public void testFollowedUser() {
        subscriptionController.followUser(1, 2);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(1,2);
    }
}
