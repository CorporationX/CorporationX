package school.faang.user_service.service.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class AboutPatternFilter implements UserFilterStrategy {
    @Override
    public boolean check(User user, UserFilterDto filter) {
        return filter.getAboutPattern() == null || user.getAboutMe().matches(filter.getAboutPattern());
    }
}