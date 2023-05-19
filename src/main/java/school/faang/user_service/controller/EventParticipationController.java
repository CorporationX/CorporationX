package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register/{userId}")
    public void registerParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public void unregisterParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable Long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public long getParticipantsCount(@PathVariable Long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}