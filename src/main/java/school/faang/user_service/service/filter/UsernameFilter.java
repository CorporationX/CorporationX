package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UsernameFilter extends UserFilter {

    @Override
    protected boolean applyFilter(User user, UserFilterDto filter) {
        return user.getUsername().contains(filter.getNamePattern());
    }

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return !filter.getNamePattern().isBlank();
    }
}
