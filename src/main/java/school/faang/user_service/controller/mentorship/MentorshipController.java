package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.mentorship.MentorshipService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mentorship")
@Tag(name = "Mentorship", description = "Операции с менторами и менти")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{mentorId}")
    @Operation(summary = "Получить список менти", description = "Возвращает список менти для указанного ментора")
    public List<UserDTO> getMentees(long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentors/{userId}")
    @Operation(summary = "Получить список менторов", description = "Возвращает список менторов для указанного пользователя")
    public List<UserDTO> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentees/{mentorId}/{menteeId}")
    @Operation(summary = "Удалить менти", description = "Удаляет менти от ментора")
    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentors/{menteeId}/{mentorId}")
    @Operation(summary = "Удалить ментора", description = "Удаляет ментора от менти")
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
