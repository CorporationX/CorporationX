package school.faang.user_service.service.filter.user;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public abstract class UserFilter {

    public Stream<User> applyFilter(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> applyFilter(user, filter));
    }

    protected abstract boolean applyFilter(User user, UserFilterDto filter);

    public abstract boolean isApplicable(UserFilterDto filter);
}