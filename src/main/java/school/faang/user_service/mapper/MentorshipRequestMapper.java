package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requester", source = "requesterId", qualifiedByName = "mapRequester")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapReceiver")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequest);

    User map(Long value);

    @Named("mapRequester")
    default User mapRequester(Long requesterId) {
        if (requesterId == null) {
            return null;
        }
        User requester = new User();
        requester.setId(requesterId);
        return requester;
    }

    @Named("mapReceiver")
    default User mapReceiver(Long receiverId) {
        if (receiverId == null) {
            return null;
        }
        User receiver = new User();
        receiver.setId(receiverId);
        return receiver;
    }
}