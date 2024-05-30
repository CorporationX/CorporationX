package school.faang.user_service.service.subscription;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilterStrategy {
    boolean check(User user, UserFilterDto filter);
}