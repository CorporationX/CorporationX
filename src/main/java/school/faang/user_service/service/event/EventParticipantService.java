package school.faang.user_service.service.event;

import school.faang.user_service.entity.User;

import java.util.List;

public interface EventParticipantService {
    void registerParticipant(Long eventId, Long userId);

    void unregisterParticipant(Long eventId, Long userId);

    List<User> getParticipants(Long eventId);

    Integer getParticipantsCount(Long eventId);
}
