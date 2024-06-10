package school.faang.user_service.controller.mentorship;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorshipRequest")
@AllArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto){
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/accept/{id}")
    public void acceptRequest(@PathVariable("id") long id){
        mentorshipRequestService.acceptRequest(id);
    }

    @GetMapping("/")
    public List<MentorshipRequestDto> findAll() {
        return mentorshipRequestService.findAll();
    }
}
