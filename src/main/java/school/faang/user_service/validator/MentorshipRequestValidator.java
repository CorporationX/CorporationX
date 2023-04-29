package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {

    private static final int REQ_INTERVAL_MONTHS = 3;

    private final UserService userService;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validate(MentorshipRequestDto mentorshipRequest) {
        if (mentorshipRequest.getRequesterId() == null) {
            throw new DataValidationException("Mentee id is required");
        }
        if (mentorshipRequest.getReceiverId() == null) {
            throw new DataValidationException("Mentor id is required");
        }
        if (mentorshipRequest.getReceiverId().equals(mentorshipRequest.getRequesterId())) {
            throw new DataValidationException("Mentor and mentee cannot be the same person");
        }
        if (!userService.existsById(mentorshipRequest.getRequesterId())) {
            throw new DataValidationException("Mentee does not exist");
        }
        if (!userService.existsById(mentorshipRequest.getReceiverId())) {
            throw new DataValidationException("Mentor does not exist");
        }
        mentorshipRequestRepository.findLatestRequest(mentorshipRequest.getRequesterId(), mentorshipRequest.getReceiverId())
                .ifPresent(req -> {
                    if (!req.getCreatedAt().plusMonths(REQ_INTERVAL_MONTHS).isAfter(LocalDateTime.now())) {
                        throw new DataValidationException("Mentorship request interval is too short");
                    }
                });
    }
}
