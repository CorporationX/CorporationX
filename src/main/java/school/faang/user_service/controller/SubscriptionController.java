package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscription/{followerId}/user/{followeeId}")
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        isFollowerEqualsToFollowee(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/subscription/{followerId}/user/{followeeId}")
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        isFollowerEqualsToFollowee(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/subscription/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody @Validated UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/subscription/{followerId}/followers/count")
    public long getFollowersCount(@PathVariable long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @PostMapping("/subscription/{followerId}/following")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody @Validated UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/subscription/{followerId}/following/count")
    public long getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
    
    private void isFollowerEqualsToFollowee(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot unfollow yourself");
        }
    }
}