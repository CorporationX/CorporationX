package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserSkillFilter implements UserFilter {
  @Override
  public boolean isApplicable(UserFilterDto filter) {
    return filter.getSkillPattern() != null;
  }

  @Override
  public void apply(Stream<User> users, UserFilterDto filter) {
    users.filter(user -> user.getSkills().stream().anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern())));
  }
}
