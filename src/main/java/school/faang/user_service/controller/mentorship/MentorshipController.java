package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{id}")
    public List<UserDto> getMentors(@PathVariable @Positive long id) {
        return mentorshipService.getMentors(id);
    }

    @GetMapping("/mentees/{id}")
    public List<UserDto> getMentees(@PathVariable @Positive long id) {
        return mentorshipService.getMentees(id);
    }

    @DeleteMapping("/mentee")
    public void deleteMentee(@RequestParam @Positive long mentorId, @RequestParam @Positive long menteeId) {
        if (mentorId == menteeId) {
            throw new UnsupportedOperationException("You cannot delete the mentee-user who is the same as the mentor-user.");
        }
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("/mentor")
    public void deleteMentor(@RequestParam @Positive long menteeId, @RequestParam @Positive long mentorId) {
        if (menteeId == mentorId) {
            throw new UnsupportedOperationException("You cannot delete the mentor-user who is the same as the mentee-user.");
        }
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}