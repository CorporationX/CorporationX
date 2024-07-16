package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserAboutFilter implements UserFilter {
  @Override
  public boolean isApplicable(UserFilterDto filter) {
    return filter.getAboutPattern() != null;
  }

  @Override
  public void apply(Stream<User> users, UserFilterDto filter) {
    users.filter(user -> user.getAboutMe().contains(filter.getAboutPattern()));
  }
}
