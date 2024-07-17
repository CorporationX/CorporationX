package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);
    void acceptRequest(long id);
    List<MentorshipRequestDto> findAll();
}