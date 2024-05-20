package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public List<UserDTO> getMentees(long userId) {
        User mentor = getUserById(userId);
        return mentor.getMentees().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserDTO> getMentors(long userId) {
        User user = getUserById(userId);
        return user.getMentors().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId + " не найден"));
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        if (!mentor.getMentees().contains(mentee)) {
            throw new EntityNotFoundException("Данный пользователь не является менти у указанного ментора");
        }
        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        if (!mentee.getMentors().contains(mentor)) {
            throw new EntityNotFoundException("Данный пользователь не является ментором для указанного менти");
        }
            mentee.getMentors().remove(mentor);
            userRepository.save(mentee);
    }
}