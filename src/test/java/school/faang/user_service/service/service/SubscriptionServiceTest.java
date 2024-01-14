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
import school.faang.user_service.mapper.UserMapper;
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

    @Mock
    private UserMapper userMapper;

    @Mock
    private static List<UserFilter> userFilters;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private static List<User> users;
    private static UserFilterDto filter;


    @BeforeEach
    public void init() {

        users = List.of(
                User.builder().username("Ruslan").build()
        );
    }

    @Test
    public void testGetFollowersSuccess() {
        long followeeId = 1L;
        filter = new UserFilterDto();
        UserDto userDto = new UserDto();
        userDto.setUsername("Ruslan");
        filter.setNamePattern("R");

        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(users.stream());
        Mockito.when(userMapper.toDto(users.get(0))).thenReturn(userDto);

        List<UserDto> userDtos = subscriptionService.getFollowers(followeeId, filter);
        Assertions.assertEquals(1, userDtos.size());
        Assertions.assertEquals("Ruslan", userDtos.get(0).getUsername());
        Mockito.verify(subscriptionRepository, Mockito.times(1)).findByFolloweeId(followeeId);
    }
}