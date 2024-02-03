package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;

@RestController
@RequestMapping("api/v1/mentorship")
@RequiredArgsConstructor
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{id}")
    public List<UserDto> getMentors(@PathVariable @Min(1) long id) {
        return mentorshipService.getMentors(id);
    }

    @GetMapping("/mentees/{id}")
    public List<UserDto> getMentees(@PathVariable @Min(1) long id) {
        return mentorshipService.getMentees(id);
    }

    @DeleteMapping("/mentors")
    public void deleteMentor(@RequestParam @Min(1) long menteeId, @RequestParam @Min(1) long mentorId) {
        MentorshipValidator.validateMentorshipIds(mentorId, menteeId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    @DeleteMapping("/mentees")
    public void deleteMentee(@RequestParam @Min(1) long mentorId, @RequestParam @Min(1) long menteeId) {
        MentorshipValidator.validateMentorshipIds(mentorId, menteeId);
        mentorshipService.deleteMentee(mentorId, menteeId);
    }
}