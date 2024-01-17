package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @DeleteMapping("/mentor")
    public void deleteMentor(@RequestParam @Positive long menteeId, @RequestParam @Positive long mentorId) {
        if (menteeId == mentorId) {
            throw new UnsupportedOperationException("You cannot delete the mentor-user who is the same as the mentee-user.");
        }
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}