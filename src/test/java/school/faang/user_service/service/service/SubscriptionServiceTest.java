package school.faang.user_service.service.service;

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
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

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
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionValidator,
                filters, userMapper);
        dtoFilter = new UserFilterDto();
        dtoFilter.setNamePattern("R");
    }

    @Test
    void testGetFollowingCallRepositoryMethod() {
        long followerId = 1L;

        Mockito.when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(users.stream());
        subscriptionService.getFollowing(followerId, dtoFilter);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followerId);
    }
}
