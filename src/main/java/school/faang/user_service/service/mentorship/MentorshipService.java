package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long userId, int page, int pageSize) {
        return getUsers(userId, page, pageSize, User::getMentees);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long userId, int page, int pageSize) {
        return getUsers(userId, page, pageSize, User::getMentors);
    }

    @Transactional
    public void deleteMentee(long userId, long menteeId) {
        mentorshipRepository.findById(userId)
                .ifPresent(mentor -> mentor.getMentees()
                        .removeIf(mentee -> mentee.getId() == menteeId)
                );
    }

    @Transactional
    public void deleteMentor(long userId, long mentorId) {
        mentorshipRepository.findById(userId)
                .ifPresent(mentee -> mentee.getMentors()
                        .removeIf(mentor -> mentor.getId() == mentorId)
                );
    }

    private List<UserDto> getUsers(long userId, int page, int pageSize, Function<User, List<User>> mapper) {
        return mentorshipRepository.findById(userId)
                .map(mapper)
                .orElse(Collections.emptyList())
                .stream()
                .skip((long) page * pageSize)
                .limit(pageSize)
                .map(userMapper::toDto)
                .toList();
    }
}
