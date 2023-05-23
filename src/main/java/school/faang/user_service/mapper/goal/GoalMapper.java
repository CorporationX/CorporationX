package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "skillIds", source = "skillsToAchieve")
    @Mapping(target = "userIds", source = "users")
    GoalDto toDto(Goal entity);

    default List<Long> toSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId).toList();
    }

    default List<Long> toUserIds(List<User> users) {
        return users.stream()
                .map(User::getId).toList();
    }

    @Mapping(target = "mentor", qualifiedByName = "mapMentor", source = "mentorId")
    @Mapping(target = "skillsToAchieve", source = "skillIds")
    @Mapping(target = "users", source = "userIds")
    Goal toEntity(GoalDto dto);

    @Mapping(target = "id", source = "skillId")
    Skill mapSkillId(Long skillId);

    @Mapping(target = "id", source = "userId")
    User mapUserId(Long userId);

    @Named("mapMentor")
    default User mapMentor(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        User mentor = new User();
        mentor.setId(mentorId);
        return mentor;
    }
}