package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class AbstractUserService {

    private final List<UserFilter> filters;
    protected final UserMapper userMapper;

    protected List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        filters.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                .forEach(userFilter -> userFilter.applyFilter(users, filter));
        return users
                .skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .map(userMapper::toDto)
                .toList();
    }
}
