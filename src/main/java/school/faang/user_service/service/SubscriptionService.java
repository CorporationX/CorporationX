package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final SubscriptionValidator subscriptionValidator;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        subscriptionValidator.validateUserExists(followerId);
        Stream<User> followee = subscriptionRepo.findByFollowerId(followerId);
        return filterUsers(followee, filter);
    }

    public List<UserDto> filterUsers(Stream<User> followee, UserFilterDto filter) {
        return filters.stream()
                .filter(f -> f.isApplicable(filter))
                .reduce(followee, (stream, f) -> f.apply(stream, filter), Stream::concat)
                .map(userMapper::toDto).toList();
    }
}