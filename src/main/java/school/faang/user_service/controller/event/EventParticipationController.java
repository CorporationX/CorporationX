package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.event.EventParticipationServiceImpl;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationServiceImpl eventParticipationServiceImpl;
    private final UserMapper userMapper;

    public void registerParticipant(Long eventId, Long userId) {
        eventParticipationServiceImpl.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        eventParticipationServiceImpl.unregisterParticipant(eventId, userId);
    }

    public List<UserDTO> getParticipants(Long eventId) {
        List<User> participants = eventParticipationServiceImpl.getParticipants(eventId);
        return userMapper.toDTOList(participants);
    }

    public Integer getParticipantsCount(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Invalid eventId");
        }
        return eventParticipationServiceImpl.getParticipantsCount(eventId);
    }

}
