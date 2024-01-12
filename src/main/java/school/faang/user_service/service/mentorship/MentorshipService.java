package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long id) {
        User user = validateAndGet(id);
        return user.getMentees().stream().map(userMapper::toDto)
                .toList();
    }

    private User validateAndGet(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found"));
    }
}