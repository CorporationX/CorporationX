package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMatchByFilterChecker userMatchByFilterChecker;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @DisplayName("Проверка, что нельзя подписаться на пользователя, если уже на него подписан")
    void followUserAlreadySubscribeTest() {

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(10, 20)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(10, 20);
        });

        verify(subscriptionRepository, Mockito.never()).followUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Проверка, что подписка на пользователя работает, в service")
    void followUserSuccessServiceTest() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(10, 20)).thenReturn(false);

        subscriptionService.followUser(10, 20);

        verify(subscriptionRepository).followUser(10, 20);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в service")
    void testGetFollowersCountPositive() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(1L)).thenReturn(42);

        int followersCount = subscriptionService.getFollowersCount(1L);
        assertEquals(42, followersCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowersCount работает, в service, если передать  несуществующее ID")
    void testGetFollowersCountNegative() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(2L)).thenReturn(0); // Предположим, что возвращается 0 при отсутствии данных

        int followersCount = subscriptionService.getFollowersCount(2L);
        assertEquals(0, followersCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в service")
    void testGetFollowingCountPositive() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(1L)).thenReturn(42);

        int FollowingCount = subscriptionService.getFollowingCount(1L);
        assertEquals(42, FollowingCount);
    }

    @Test
    @DisplayName("Проверка, что метод getFollowingCount работает, в service, если передать  несуществующее ID")
    void testGetFollowingCountNegative() {

        when(subscriptionRepository.findFollowersAmountByFolloweeId(2L)).thenReturn(0); // Предположим, что возвращается 0 при отсутствии данных

        int FollowingCount = subscriptionService.getFollowingCount(2L);
        assertEquals(0, FollowingCount);
    }

    @Test
    void testUnfollowUser() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    void testUnfollowUserNotSubscribed() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        try {
            subscriptionService.unfollowUser(followerId, followeeId);
            fail("Expected DataValidationException was not thrown");
        } catch (DataValidationException e) {
            // Expected exception thrown
        }
    }

    @Test
    void testGetFollowers() {
        long followeeId = 1;
        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(1);
        filter.setPageSize(10);

        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .build();

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user1, user2));
        when(userMatchByFilterChecker.isUserMatchFiltration(any(), any())).thenReturn(true);

        List<UserDTO> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());

        verify(subscriptionRepository).findByFolloweeId(followeeId);
        verify(userMatchByFilterChecker, times(2)).isUserMatchFiltration(any(), any());
    }

    @Test
    void testGetFollowing() {

        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        when(subscriptionRepository.findByFollowerId(anyLong())).thenReturn(Stream.of(user1));
        when(userMatchByFilterChecker.isUserMatchFiltration(any(User.class), any(UserFilterDTO.class))).thenReturn(true);


        SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, userMatchByFilterChecker);


        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(1);
        filter.setPageSize(10);
        List<UserDTO> result = subscriptionService.getFollowing(123L, filter);


        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user1@example.com", result.get(0).getEmail());


        verify(subscriptionRepository).findByFollowerId(123L);
        verify(userMatchByFilterChecker).isUserMatchFiltration(any(User.class), any(UserFilterDTO.class));
    }
}
