package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscription/{followerId}/user/{followeeId}")
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot follow yourself");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/subscription/{followerId}/user/{followeeId}")
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot unfollow yourself");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/subscription/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/subscription/{followerId}/followers/count")
    public long getFollowersCount(@PathVariable long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @PostMapping("/subscription/{followerId}/following")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/subscription/{followerId}/following/count")
    public long getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
