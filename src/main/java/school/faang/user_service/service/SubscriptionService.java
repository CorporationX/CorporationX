package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.filter.user.UserAboutFilter;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserContactFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.filter.user.UserPhoneFilter;
import school.faang.user_service.filter.user.UserSkillFilter;
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

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        subscriptionValidator.validateUserExists(followeeId);
        return filterUsers(subscriptionRepo.findByFolloweeId(followeeId), filter);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto dtoFilter) {
        filters.stream()
                .filter(filter -> filter.isApplicable(dtoFilter))
                .forEach(filter -> filter.apply(users, dtoFilter));
        return userMapper.toDtoList(users.toList());
    }

}
