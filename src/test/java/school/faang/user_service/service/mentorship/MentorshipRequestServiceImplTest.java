package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceImplTest {
    private static final long MENTORSHIP_REQUEST_ID = 1L;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipEventPublisher mentorshipEventPublisher;
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    private MentorshipRequest mentorshipRequest;
    private MentorshipRequestDto mentorshipRequestDto;
    private User requester;
    private User receiver;


    @BeforeEach
    void setUp() {
        mentorshipRequest = new MentorshipRequest();
        mentorshipRequestDto = new MentorshipRequestDto();
        requester = new User();
        receiver = new User();
        requester.setId(REQUESTER_ID);
        receiver.setId(RECEIVER_ID);
        mentorshipRequest.setId(MENTORSHIP_REQUEST_ID);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequestDto.setId(MENTORSHIP_REQUEST_ID);
    }

    @Test
    public void whenRequestMentorshipThenGetMentorshipRequestDto() {
        when(mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto)).thenReturn(true);
        when(mentorshipRequestMapper.toEntity(mentorshipRequestDto)).thenReturn(mentorshipRequest);
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);
        MentorshipRequestDto actual = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        assertThat(actual).isEqualTo(mentorshipRequestDto);
    }

    @Test
    public void whenAcceptRequestSuccessfully() {
        when(mentorshipRequestRepository.findById(MENTORSHIP_REQUEST_ID)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.existAcceptedRequest(REQUESTER_ID, RECEIVER_ID)).thenReturn(false);
        mentorshipRequestService.acceptRequest(MENTORSHIP_REQUEST_ID);
        verify(mentorshipRequestRepository).save(mentorshipRequest);
        verify(mentorshipService).assignMentor(REQUESTER_ID, RECEIVER_ID);
        verify(mentorshipEventPublisher).publish(new MentorshipStartEvent(RECEIVER_ID, REQUESTER_ID));
    }

    @Test
    public void whenFindAllThenListOfMentorshipRequestDto() {
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));
        when(mentorshipRequestMapper.toDtoList(List.of(mentorshipRequest))).thenReturn(List.of(mentorshipRequestDto));
        List<MentorshipRequestDto> actual = mentorshipRequestService.findAll();
        assertThat(actual).isEqualTo(List.of(mentorshipRequestDto));
    }
}