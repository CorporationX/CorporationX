package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        subscriptionValidator.validateUser(followerId);
        Stream<User> filteredUsers = subscriptionRepo.findByFollowerId(followerId);
        return filterUsers(filteredUsers, filter);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto dtoFilter) {
        Stream<User> filteredUsers = filters.stream()
                .filter(filter -> filter.isApplicable(dtoFilter))
                .flatMap(filter -> filter.apply(users, dtoFilter));
        return userMapper.toDtoList(filteredUsers.toList());
    }
}