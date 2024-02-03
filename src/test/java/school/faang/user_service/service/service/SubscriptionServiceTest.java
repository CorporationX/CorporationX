package school.faang.user_service.service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepo;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private static List<User> users;
    private static UserFilterDto dtoFilter;

    @BeforeEach
    public void init() {
        users = List.of(
                User.builder().username("Ruslan").build(),
                User.builder().username("Oleg").build(),
                User.builder().username("Roman").build()
        );
        UserNameFilter userNameFilter = Mockito.mock(UserNameFilter.class);
        List<UserFilter> filters = List.of(userNameFilter);
        subscriptionService = new SubscriptionService(subscriptionRepo, subscriptionValidator,
                filters, userMapper);
        dtoFilter = new UserFilterDto();
        dtoFilter.setNamePattern("R");
    }

    @Test
    void testGetFollowersCallRepositoryMethod() {
        long followeeId = 1L;

        Mockito.when(subscriptionRepo.findByFolloweeId(followeeId)).thenReturn(users.stream());
        subscriptionService.getFollowers(followeeId, dtoFilter);

        Mockito.verify(subscriptionRepo, Mockito.times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    void testGetFollowingCallRepositoryMethod() {
        long followerId = 1L;

        Mockito.when(subscriptionRepo.findByFollowerId(followerId)).thenReturn(users.stream());
        subscriptionService.getFollowing(followerId, dtoFilter);

        Mockito.verify(subscriptionRepo, Mockito.times(1))
                .findByFollowerId(followerId);
    }

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