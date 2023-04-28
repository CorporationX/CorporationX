package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.EventParticipationRepository;
import school.faang.user_service.service.filter.event.EventFilter;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@Service
public class EventParticipationService extends AbstractEventService{

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final EventValidator eventValidator;

    public EventParticipationService(EventParticipationRepository eventParticipationRepository,
                                     List<EventFilter> filters, EventMapper eventMapper,
                                     EventValidator eventValidator, UserMapper userMapper) {
        super(filters, eventMapper);
        this.eventParticipationRepository = eventParticipationRepository;
        this.eventValidator = eventValidator;
        this.userMapper = userMapper;
    }

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (eventValidator.isUserRegistered(eventId, userId)) {
            throw new DataValidationException(ErrorMessage.USER_ALREADY_REGISTERED);
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        if (!eventValidator.isUserRegistered(eventId, userId)) {
            throw new DataValidationException(ErrorMessage.USER_NOT_REGISTERED);
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Transactional
    public List<UserDto> getParticipants(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().map(userMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}