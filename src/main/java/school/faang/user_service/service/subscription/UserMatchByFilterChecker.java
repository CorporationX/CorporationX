package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;


@RequiredArgsConstructor
@Component
public class UserMatchByFilterChecker {

    private final List<UserFilterStrategy> filters;


    boolean isUserMatchFiltration(User user, UserFilterDto filter) {
        for (UserFilterStrategy filterStrategy : filters) {
            if (!filterStrategy.check(user, filter)) {
                return false;
            }
        }
        return true;
    }
}