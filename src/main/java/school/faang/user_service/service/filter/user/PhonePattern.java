package school.faang.user_service.service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class PhonePattern extends UserFilter {

    @Override
    protected boolean applyFilter(User user, UserFilterDto filter) {
        return user.getPhone().contains(filter.getPhonePattern());
    }

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return !filter.getPhonePattern().isBlank();
    }
}