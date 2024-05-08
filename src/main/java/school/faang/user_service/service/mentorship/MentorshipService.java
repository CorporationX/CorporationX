package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getMentees(long userId) {
        User mentor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Ментор с id: " + userId + " не найден"));
        return mentor.getMentees().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getMentors(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id: " + userId + " не найден"));
        return user.getMentors().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userRepository.findById(mentorId).orElse(null);
        User mentee = userRepository.findById(menteeId).orElse(null);
        if (mentor != null && mentee != null && mentor.getMentees().contains(mentee)) {
            mentor.getMentees().remove(mentee);
            userRepository.save(mentor);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = userRepository.findById(mentorId).orElse(null);
        User mentee = userRepository.findById(menteeId).orElse(null);
        if (mentor != null && mentee != null && mentee.getMentors().contains(mentor)) {
            mentee.getMentors().remove(mentor);
            userRepository.save(mentee);
        }
    }
}