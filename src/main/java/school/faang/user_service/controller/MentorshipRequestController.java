package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilter;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/mentorship/request")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequest) {
        if (validate(mentorshipRequest)) {
            return mentorshipRequestService.requestMentorship(mentorshipRequest);
        }
        throw new DataValidationException(ErrorMessage.INVALID_MENTORSHIP_REQUEST);
    }

    @PostMapping("/mentorship/request/list")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilter filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    private boolean validate(MentorshipRequestDto mentorshipRequest) {
        return true;
    }
}
