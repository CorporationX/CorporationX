package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
  private final SubscriptionRepository subscriptionRepository;
  private final UserMapper userMapper;
  private final List<UserFilter> userFilters;

  public void followUser(long followerId, long followeeId) throws DataValidationException {
    if (followerId == followeeId) {
      throw new DataValidationException("Нельзя подписаться на самого себя");
    }
    if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
      throw new DataValidationException("Эта подписка уже существует!");
    }
    subscriptionRepository.followUser(followerId, followeeId);
  }

  public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
    if (followerId == followeeId) {
      throw new DataValidationException("Нельзя отписаться от самого себя");
    }
    subscriptionRepository.unfollowUser(followerId, followeeId);
  }

  public List<UserDto> getFollowers(long followeeId) {
    List<User> users =  subscriptionRepository.findByFollowerId(followeeId).toList();
    return users.stream()
      .map(userMapper::toDto)
      .toList();
  }

  private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filters) {
    userFilters.stream()
      .filter(filter -> filter.isApplicable(filters))
      .forEach(filter -> filter.apply(users, filters));
    return users.map(userMapper::toDto).toList();
  }

  public int getFollowersCount(long followeeId) {
    return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
  }

  public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
    Stream<User> users =  subscriptionRepository.findByFollowerId(followeeId);
    return filterUsers(users, filter);
  }

  public int getFollowingCount(long followerId) {
    return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
  }
}
