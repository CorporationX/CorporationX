package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = getExistingUserById(mentorId);
        User mentee = getExistingUserById(menteeId);
        if (mentor.getMentees().remove(mentee)) {
            mentorshipRepository.save(mentor);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getExistingUserById(menteeId);
        User mentor = getExistingUserById(mentorId);
        if (mentee.getMentors().remove(mentor)) {
            mentorshipRepository.save(mentee);
        }
    }

    private User getExistingUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found"));
    }
}