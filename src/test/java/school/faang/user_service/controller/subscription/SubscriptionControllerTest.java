package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    @DisplayName("Проверка, что пользователь не может подписаться сам на себя")
    void followUserSameIdTest() {
        Exception exception = assertThrows(DataValidationException.class, () -> {
            subscriptionController.followUser(42, 42);
        });
        assertEquals("Нельзя подписаться на самого себя", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка, что подписка на пользователя работает, в controller")
    void followUserSuccessControllerTest() {
        subscriptionController.followUser(10, 20);
        verify(subscriptionService).followUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что пользователь не может отписаться от самого себя")
    void unfollowUserSamIdTest() {
        assertThrows(DataValidationException.class, () -> {
            subscriptionController.unfollowUser(42, 42);
        });
    }

    @Test
    @DisplayName("Проверка, что отписка от пользователя работает, в controller")
    void unfollowUserSuccessControllerTest() {
        subscriptionController.unfollowUser(10, 20);
        verify(subscriptionService).unfollowUser(10, 20);
    }


    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в controller")
    void testGetFollowersCountPositive() {
        when(subscriptionService.getFollowersCount(1L)).thenReturn(42);

        int followersCount = subscriptionController.getFollowersCount(1L);
        assertEquals(42, followersCount);
    }


    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в controller")
    void testGetFollowingCountPositive() {
        when(subscriptionService.getFollowingCount(1L)).thenReturn(42);

        int followingCount = subscriptionController.getFollowingCount(1L);
        assertEquals(42, followingCount);
    }

    @Test
    void testGetFollowers() {

        when(subscriptionService.getFollowers(anyLong(), any(UserFilterDTO.class)))
                .thenReturn(List.of(new UserDTO(1L, "user1", "user1@example.com","password")));

        SubscriptionController subscriptionController = new SubscriptionController(subscriptionService);

        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(1);
        filter.setPageSize(10);
        List<UserDTO> result = subscriptionController.getFollowers(456L, filter);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("password", result.get(0).getPassword());

        verify(subscriptionService).getFollowers(456L, filter);
    }

    @Test
    void testGetFollowing() {

        when(subscriptionService.getFollowing(anyLong(), any(UserFilterDTO.class)))
                .thenReturn(List.of(new UserDTO(1L, "user1", "user1@example.com", "password")));

        SubscriptionController subscriptionController = new SubscriptionController(subscriptionService);

        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(1);
        filter.setPageSize(10);
        List<UserDTO> result = subscriptionController.getFollowing(123L, filter);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("password", result.get(0).getPassword());

        verify(subscriptionService).getFollowing(123L, filter);
    }
}
