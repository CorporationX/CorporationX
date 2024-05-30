package school.faang.user_service.service.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class EmailPatternFilter implements UserFilterStrategy {
    @Override
    public boolean check(User user, UserFilterDto filter) {
        return filter.getEmailPattern() == null || user.getEmail().matches(filter.getEmailPattern());
    }
}