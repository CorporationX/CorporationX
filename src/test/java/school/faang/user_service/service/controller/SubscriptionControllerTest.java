package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private static UserFilterDto filter;

    @BeforeEach
    public void init() {
        filter = new UserFilterDto();
    }

    @Test
    public void testGetFollowers() {
        subscriptionController.getFollowers(1L, filter);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(1L, filter);
    }

    @Test
    public void testFollowingUser() {
        subscriptionController.getFollowing(1L, filter);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(1L, filter);
    }

    @Test
    public void testFollowedUser() {
        subscriptionController.followUser(1, 2);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(1, 2);
    }

    @Test
    public void testUnfollowedUser() {
        subscriptionController.unfollowUser(1, 2);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(1, 2);
    }

    @Test
    public void testFollowersCount() {
        subscriptionController.getFollowersCount(1L);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(1L);
    }

    @Test
    public void testFollowingCount() {
        subscriptionController.getFollowingCount(1L);
        Mockito.verify(subscriptionService, Mockito.times(1)).getFollowingCount(1L);
    }
}