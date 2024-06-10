package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipService mentorshipService;
    private final MentorshipEventPublisher mentorshipEventPublisher;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto);
        mentorshipRequestDto.setStatus(RequestStatus.PENDING);
        MentorshipRequest savedMentorshipRequest = mentorshipRequestRepository.save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
        return mentorshipRequestMapper.toDto(savedMentorshipRequest);
    }

    @Override
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findEntityById(id);
        long requesterId = mentorshipRequest.getRequester().getId();
        long receiverId = mentorshipRequest.getReceiver().getId();
        boolean isRequestAlreadyAccepted = mentorshipRequestRepository.existAcceptedRequest(requesterId, receiverId);
        if (isRequestAlreadyAccepted) {
            throw new DataValidationException(String.format("user with id = %d is already mentor for user with id = %d", receiverId, requesterId));
        }
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        mentorshipService.assignMentor(requesterId, receiverId);
        MentorshipStartEvent mentorshipStartEvent = new MentorshipStartEvent(receiverId, requesterId);
        mentorshipEventPublisher.publish(mentorshipStartEvent);
    }

    @Override
    public List<MentorshipRequestDto> findAll() {
        return mentorshipRequestMapper.toDtoList(mentorshipRequestRepository.findAll());
    }

    private MentorshipRequest findEntityById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("request with id= %d not found", id)));
    }
}