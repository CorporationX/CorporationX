package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

  private long followerId = 1L;
  private long followeeId = 2L;

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private List<UserFilter> userFilters;

  @InjectMocks
  private SubscriptionService subscriptionService;

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    user = new User();
    userDto = new UserDto();
  }

  @Test
  void testFollowYourself() {
    assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
  }

  @Test
  void testExistingSubscription() {
    when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
    assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
  }

  @Test
  void testFollowUser() throws DataValidationException {
    when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
    subscriptionService.followUser(followerId, followeeId);

    verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);

  }

  @Test
  void testUnfollowYourself() {
    assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followerId));
  }

  @Test
  void testUnfollowUser() throws DataValidationException {
    subscriptionService.unfollowUser(followerId, followeeId);

    verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
  }

  @Test
  void testGetFollowers() {
    List<User> users = List.of(user);

    when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users.stream());
    when(userMapper.toDto(user)).thenReturn(userDto);

    List<UserDto> result = subscriptionService.getFollowers(followeeId);

    assertEquals(1, result.size());
    assertEquals(userDto, result.get(0));
  }

  @Test
  void testGetFollowersCount() {
    int count = 5;

    when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(count);

    int result = subscriptionService.getFollowersCount(followeeId);

    assertEquals(count, result);
  }

  @Test
  void testGetFollowing() {
    List<User> users = List.of(user);
    UserFilterDto filter = new UserFilterDto();

    when(subscriptionRepository.findByFollowerId(followeeId)).thenReturn(users.stream());
    when(userMapper.toDto(user)).thenReturn(userDto);
    when(userFilters.stream()).thenReturn(Stream.empty());

    List<UserDto> result = subscriptionService.getFollowing(followeeId, filter);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(userDto, result.get(0));
  }

  @Test
  void testGetFollowingCount() {
    int count = 5;

    when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(count);

    int result = subscriptionService.getFollowingCount(followerId);

    assertEquals(count, result);
  }
}