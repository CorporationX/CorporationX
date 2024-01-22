package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/unfollow/{followerId}/{followeeId}")
    public void unfollowUser(@PathVariable long followerId,
                             @PathVariable long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/follow/{followerId}/{followeeId}")
    public void followUser(@PathVariable long followerId,
                           @PathVariable long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @GetMapping("/followers-count/{id}")
    public int getFollowersCount(@PathVariable("id") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following-count/{id}")
    public int getFollowingCount(@PathVariable("id") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}