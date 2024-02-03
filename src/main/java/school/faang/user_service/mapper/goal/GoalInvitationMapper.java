package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

/**
 * @author Alexander Bulgakov
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation invitation);

    @Mapping(target = "inviter.id", ignore = true)
    @Mapping(target = "invited.id", ignore = true)
    GoalInvitation toEntity(GoalInvitationDto invitationDto);
}
