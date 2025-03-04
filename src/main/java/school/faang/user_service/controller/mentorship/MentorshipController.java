package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;
import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/{mentorId}/mentees")
    public ResponseEntity<List<UserDto>> getMentees(@PathVariable Long mentorId) {
        List<UserDto> mentees = mentorshipService.getMentees(mentorId);
        return ResponseEntity.ok(mentees);
    }
}
