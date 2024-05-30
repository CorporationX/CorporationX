package school.faang.user_service.service.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class CityPatternFilter implements UserFilterStrategy {
    @Override
    public boolean check(User user, UserFilterDto filter) {
        return filter.getCityPattern() == null || user.getCity().matches(filter.getCityPattern());
    }
}