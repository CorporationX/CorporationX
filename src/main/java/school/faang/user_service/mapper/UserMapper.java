package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "goals", target = "goalIds", qualifiedByName = "mapGoalsToIds")
    @Mapping(source = "mentees", target = "menteeIds", qualifiedByName = "mapUsersToIds")
    @Mapping(source = "mentors", target = "mentorIds", qualifiedByName = "mapUsersToIds")
    UserDto toDto(User user);

    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    User toEntity(UserDto userDto);

    @Named("mapGoalsToIds")
    default List<Long> mapGoalsToIds(List<Goal> goals) {
        return goals.stream().map(Goal::getId).toList();
    }

    @Named("mapUsersToIds")
    default List<Long> mapUsersToIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}