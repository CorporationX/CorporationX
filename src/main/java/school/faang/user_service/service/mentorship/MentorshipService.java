package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.UserDTO;

import java.util.List;

public interface MentorshipService {
    List<UserDTO> getMentees(long mentorId);
    void stopMentorship(long mentorId);
    List<UserDTO> getMentors(long userId);
    void deleteMentee(long menteeId, long mentorId);
    void deleteMentor(long menteeId, long mentorId);
    void assignMentor(long menteeId, long mentorId);

}