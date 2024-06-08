package school.faang.user_service.service.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;

@Component
public class ExperienceMaxPatternFilter implements UserFilterStrategy {
    @Override
    public boolean check(User user, UserFilterDTO filter) {
        return (filter.getExperienceMax() != null && user.getExperience() > filter.getExperienceMax());
    }
}