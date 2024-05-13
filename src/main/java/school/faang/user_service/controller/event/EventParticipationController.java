package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;
    private final UserMapper userMapper;

    public void registerParticipant(Long eventId, Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDTO> getParticipants(Long eventId) {
        List<User> participants = eventParticipationService.getParticipants(eventId);
        return userMapper.toDTOList(participants);
    }

    public Integer getParticipantsCount(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Invalid eventId");
        }
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
