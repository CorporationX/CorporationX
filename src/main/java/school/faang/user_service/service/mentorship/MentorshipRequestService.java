package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.Rejection;
import school.faang.user_service.dto.mentorship.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> filters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequest) {
        mentorshipRequestValidator.validate(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.create(
                mentorshipRequest.getRequesterId(),
                mentorshipRequest.getReceiverId(),
                mentorshipRequest.getDescription()
        ));
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilter filter) {
        Stream<MentorshipRequest> reqs = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false);
        filters.stream()
                .filter(candidate -> candidate.isApplicable(filter))
                .forEach(candidate -> candidate.applyFilter(reqs, filter));
        return reqs.skip((long) filter.getPage() * filter.getPageSize())
                .limit(filter.getPageSize())
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest req = find(id);
        List<User> mentors = req.getRequester().getMentors();
        User mentor = req.getReceiver();
        if (!mentors.contains(mentor)) {
            mentors.add(mentor);
            req.setStatus(RequestStatus.ACCEPTED);
            return mentorshipRequestMapper.toDto(req);
        }
        throw new DataValidationException("User " + mentor.getId() + " is already a mentor of user " + req.getRequester().getId() + ".");
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, Rejection rejection) {
        MentorshipRequest req = find(id);
        req.setStatus(RequestStatus.REJECTED);
        req.setRejectionReason(rejection.getReason());
        return mentorshipRequestMapper.toDto(req);
    }

    private MentorshipRequest find(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no mentorship request with id " + id + "."));
    }
}
