package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public void deleteMentee(long menteeId, long mentorId) {
        User mentorOfTheMentee = validateAndGet(mentorId);
        User menteeToDelete = validateAndGet(menteeId);
        mentorOfTheMentee.getMentees()
                .removeIf(menteeToDelete::equals);//delete mentee if mentor has one
    }

    private User validateAndGet(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found"));
    }
}