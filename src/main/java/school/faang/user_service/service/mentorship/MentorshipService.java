package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@RequiredArgsConstructor
@Service
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

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