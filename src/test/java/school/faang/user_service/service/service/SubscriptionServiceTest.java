package school.faang.user_service.service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private static List<UserFilter> userFilters;

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
        UserFilter userNameFilter = new UserNameFilter();
        userFilters = List.of(userNameFilter);
        dtoFilter = new UserFilterDto();
        dtoFilter.setNamePattern("R");
    }

    @Test
    void getFollowersReturnValidUsers() {
        long followeeId = 1L;

        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        List<UserDto> userDtoList = subscriptionService.getFollowers(followeeId, dtoFilter);
        Assertions.assertEquals(2, userDtoList.size());

    }
}