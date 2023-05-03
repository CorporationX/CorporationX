package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filter.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
public class SubscriptionService extends AbstractUserService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, List<UserFilter> filters,
                               UserMapper userMapper) {
        super(filters, userMapper);
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followers, filter);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(followees, filter);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}