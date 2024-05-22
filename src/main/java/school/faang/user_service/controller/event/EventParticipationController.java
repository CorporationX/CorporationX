package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.service.event.EventParticipantService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event-participation")
@Tag(name = "Event", description = "Участие в мероприятии")
public class EventParticipationController {

    private final EventParticipantService eventParticipantService;
    private final UserMapper userMapper;

    @PostMapping("/register/{eventId}/{userId}")
    @Operation(summary = "Зарегистрировать участника", description = "Регистрирует участника на мероприятие")
    public void registerParticipant(@PathVariable Long eventId,@PathVariable Long userId) {
        eventParticipantService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/unregister/{eventId}/{userId}")
    @Operation(summary = "Отменить участие", description = "Отменяет участие гостя в мероприятии")
    public void unregisterParticipant(@PathVariable Long eventId,@PathVariable Long userId) {
        eventParticipantService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/participants/{eventId}")
    @Operation(summary = "Получить список участников", description = "Возвращает список участников мероприятия по его ID")
    public List<UserDTO> getParticipants(@PathVariable Long eventId) {
        List<User> participants = eventParticipantService.getParticipants(eventId);
        return userMapper.toDTOList(participants);
    }

    @GetMapping("/participants/count/{eventId}")
    @Operation(summary = "Получить количество участников", description = "Возвращает количество участников мероприятия по его ID")
    public Integer getParticipantsCount(@PathVariable Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Invalid eventId");
        }
        return eventParticipantService.getParticipantsCount(eventId);
    }
}
