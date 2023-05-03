package school.faang.user_service.mapper.goal;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    GoalInvitationDto toDto(GoalInvitation entity);

    GoalInvitation toEntity(GoalInvitationDto dto);
}