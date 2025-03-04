package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final EventValidator eventValidator;

    public void registerParticipant(Long eventId, Long userId) {
        validateDateById(eventId, userId);

        if (checkExistsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("User is already registered for the event");
        }

        eventParticipationRepository.register(eventId, userId);
        log.info("User registered for event");
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        validateDateById(eventId, userId);

        if (!checkExistsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("User is not registered for the event");
        }

        eventParticipationRepository.unregister(eventId, userId);
        log.info("User unregister for event");
    }

    public List<UserDto> getParticipant(Long eventId) {
        eventValidator.checkEventExistsById(eventId);
        return userMapper.toListUserDto(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    public void validateDateById(Long eventId, Long userId) {
        userValidator.checkUserExistsById(userId);
        eventValidator.checkEventExistsById(eventId);
    }

    public boolean checkExistsByEventIdAndUserId(Long eventId, Long userId) {
        return eventParticipationRepository.existsByEventIdAndUserId(eventId, userId);
    }
}
