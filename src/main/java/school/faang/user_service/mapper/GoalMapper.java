package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentGoalId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapUsersToUserIds")
    GoalDto toDto(Goal goal);

    @Mapping(source = "parentGoalId", target = "parent.id")
    @Mapping(source = "mentorId", target = "mentor.id")
    @Mapping(source = "userIds", target = "users", qualifiedByName = "mapUserIdsToUsers")
    Goal toEntity(GoalDto goalDto);

    List<GoalDto> toDtoList(List<Goal> goals);

    @Named("mapUsersToUserIds")
    default List<Long> mapUsersToUserIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }

    @Named("mapUserIdsToUsers")
    default List<User> mapUserIdsToUsers(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> {
                    User user = new User();
                    user.setId(userId);
                    return user;
                })
                .toList();
    }
}