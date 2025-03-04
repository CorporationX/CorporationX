package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public void registerParticipant(Long eventId, Long userId) {
        validateId(userId);
        validateId(eventId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        validateId(userId);
        validateId(eventId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipation(Long eventId) {
        validateId(eventId);

        return eventParticipationService.getParticipant(eventId);
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id can't be empty");
        }
    }
}
