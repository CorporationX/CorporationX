package school.faang.user_service.controller.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.Rejection;
import school.faang.user_service.dto.mentorship.RequestFilter;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/mentorship/request")
    public MentorshipRequestDto requestMentorship(@RequestBody @Validated MentorshipRequestDto mentorshipRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequest);
    }

    @PostMapping("/mentorship/request/list")
    public List<MentorshipRequestDto> getRequests(@RequestBody @Validated RequestFilter filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/mentorship/request/{id}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/mentorship/request/{id}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable long id, @RequestBody @Validated Rejection rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
