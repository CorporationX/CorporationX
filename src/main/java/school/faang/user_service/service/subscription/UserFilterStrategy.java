package school.faang.user_service.service.subscription;

import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;

public interface UserFilterStrategy {
    boolean check(User user, UserFilterDTO filter);
}