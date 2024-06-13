package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "mapAttendeesToAttendeeIds")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "type", target = "typeNumber", qualifiedByName = "mapTypeToTypeNumber")
    @Mapping(source = "status", target = "statusNumber", qualifiedByName = "mapStatusToStatusNumber")
    EventDto toDto(Event event);

    @Mapping(source = "attendeeIds", target = "attendees", qualifiedByName = "mapAttendeeIdsToAttendees")
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "typeNumber", target = "type", qualifiedByName = "mapTypeNumberToType")
    @Mapping(source = "statusNumber", target = "status", qualifiedByName = "mapStatusNumberToStatus")
    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);

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

    @Named("mapTypeToTypeNumber")
    default Integer mapTypeToTypeNumber(EventType type) {
        return mapEnumToOrdinal().apply(type);
    }

    @Named("mapTypeNumberToType")
    default EventType mapTypeNumberToType(int number) {
        return mapOrdinalToEnum(EventType.values()).apply(number);
    }

    @Named("mapStatusToStatusNumber")
    default Integer mapStatusToStatusNumber(EventStatus status) {
        return mapEnumToOrdinal().apply(status);
    }

    @Named("mapStatusNumberToStatus")
    default EventStatus mapStatusNumberToStatus(int number) {
        return mapOrdinalToEnum(EventStatus.values()).apply(number);
    }

    private Function<Enum, Integer> mapEnumToOrdinal() {
        return Enum::ordinal;
    }

    private <T> Function<Integer, T> mapOrdinalToEnum(T[] values) {
        return index -> values[index];
    }
}