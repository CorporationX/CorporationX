package school.faang.user_service.controller.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentorship/{userId}/mentees")
    public List<UserDto> getMentees(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return mentorshipService.getMentees(userId, page, pageSize);
    }

    @GetMapping("/mentorship/{userId}/mentors")
    public List<UserDto> getMentors(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return mentorshipService.getMentors(userId, page, pageSize);
    }

    @DeleteMapping("/mentorship/{userId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable long userId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(userId, menteeId);
    }

    @DeleteMapping("/mentorship/{userId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable long userId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(userId, mentorId);
    }
}
