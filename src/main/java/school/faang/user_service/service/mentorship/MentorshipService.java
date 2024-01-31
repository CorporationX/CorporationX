package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserService userService;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentors(long id) {
        User user = userService.getExistingUserById(id);
        return userMapper.listToDto(user.getMentors());
    }

    public List<UserDto> getMentees(long id) {
        User user = userService.getExistingUserById(id);
        return userMapper.listToDto(user.getMentees());
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.getExistingUserById(menteeId);
        User mentor = userService.getExistingUserById(mentorId);
        if (mentee.getMentors().remove(mentor)) {
            mentorshipRepository.save(mentee);
        }
    }

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = userService.getExistingUserById(mentorId);
        User mentee = userService.getExistingUserById(menteeId);
        if (mentor.getMentees().remove(mentee)) {
            mentorshipRepository.save(mentor);
        }
    }
}