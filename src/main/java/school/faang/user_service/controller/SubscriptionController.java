package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
  private final SubscriptionService subscriptionService;

  public void followUser(long followerId, long followeeId) throws DataValidationException {
    subscriptionService.followUser(followerId, followeeId);
  }

  public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
    subscriptionService.unfollowUser(followerId, followeeId);
  }

  public List<UserDto> getFollowers(long followeeId) {
    return subscriptionService.getFollowers(followeeId);
  }

  public int getFollowersCount(long followerId) {
    return subscriptionService.getFollowersCount(followerId);
  }

  public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
    return subscriptionService.getFollowing(followeeId, filter);
  }

  public int getFollowingCount(long followerId) {
    return subscriptionService.getFollowingCount(followerId);
  }
}
