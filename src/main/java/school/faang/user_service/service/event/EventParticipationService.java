package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(Long eventId, Long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : participants) {
            if (user.getId().equals(userId)) {
                throw new IllegalArgumentException("User is already registered for the event");
            }
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        boolean userFound = participants.stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!userFound) {
            throw new IllegalArgumentException("User is not registered for the event");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipants(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Invalid eventId");
        }

        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (participants.isEmpty()) {
            return new ArrayList<>();
        }

        return participants;
    }

    public Integer getParticipantsCount(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Invalid eventId");
        }

        return eventParticipationRepository.countParticipants(eventId);
    }
}
