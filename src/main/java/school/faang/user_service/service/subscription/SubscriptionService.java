package school.faang.user_service.service.subscription;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final UserMatchByFilterChecker userMatchByFilterChecker;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Вы не подписаны на пользователя.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDTO> getFollowers(long followeeId, UserFilterDTO filter) {
        return subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> userMatchByFilterChecker.isUserMatchFiltration(user, filter))
                .skip((long) (filter.getPage() - 1) * filter.getPageSize())
                .limit(filter.getPageSize())
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPassword()))
                .collect(Collectors.toList());
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDTO> getFollowing(long followerId, UserFilterDTO filter) {
        return subscriptionRepository.findByFollowerId(followerId)
                .filter(user -> userMatchByFilterChecker.isUserMatchFiltration(user, filter))
                .skip((long) (filter.getPage() - 1) * filter.getPageSize())
                .limit(filter.getPageSize())
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPassword()))
                .collect(Collectors.toList());
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }
}