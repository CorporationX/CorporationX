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

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = validateAndGet(mentorId);
        User mentee = validateAndGet(menteeId);
        mentee.getMentors().removeIf(mentorUser -> mentor.equals(mentorUser));
    }

    private User validateAndGet(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found"));
    }
}