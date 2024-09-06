package school.faang.user_service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
  boolean isApplicable(UserFilterDto filter);
  void apply(Stream<User> users, UserFilterDto filter);
}
