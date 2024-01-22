package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.event.EventValidator;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserService userService;

    public EventDto create(EventDto eventDto) {
        userService.findOwnerById(eventDto.getOwnerId());
        Event eventEntity = eventMapper.toEntity(eventDto);
        if (!eventValidator.checkIfOwnerHasSkillsRequired(eventEntity)) {
            throw new DataValidationException("Owner does not have required skills.");
        }
        Event savedEvent = eventRepository.save(eventEntity);

        return eventMapper.toDto(savedEvent);
    }

}
