package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "mapAttendeesToAttendeeIds")
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Mapping(source = "attendeeIds", target = "attendees", qualifiedByName = "mapAttendeeIdsToAttendees")
    @Mapping(source = "ownerId", target = "owner.id")
    Event toEntity(EventDto eventDto);

    @Named("mapAttendeesToAttendeeIds")
    default List<Long> mapAttendeesToAttendeeIds(List<User> attendee) {
        return attendee.stream()
                .map(User::getId)
                .toList();
    }

    @Named("mapAttendeeIdsToAttendees")
    default List<User> mapAttendeeIdsToAttendees(List<Long> attendeeIds) {
        return attendeeIds.stream()
                .map(attendeeId -> {
                    User user = new User();
                    user.setId(attendeeId);
                    return user;
                })
                .toList();
    }
}