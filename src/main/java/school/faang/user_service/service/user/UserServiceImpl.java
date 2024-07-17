package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.util.List;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilePictureService profilePictureService;
    private final CountryService countryService;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    @Override
    public boolean existsById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(String.format("user with id: %d is not exists", userId));
        }
        return true;
    }

    @Override
    public UserDTO createUser(UserDTO userDto) {
        Country country = countryService.findEntityById(userDto.getCountryId());
        List<Event> participateEvents = getParticipateEvents(userDto);
        User user = userMapper.toEntity(userDto);
        user.setCountry(country);
        user.setParticipatedEvents(participateEvents);
        profilePictureService.assignPictureToUser(user);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findById(long userId) {
        return userMapper.toDTO(findUserById(userId));
    }

    @Override
    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    @Override
    public UserDTO update(UserDTO userDto) {
        User updatedUser = findUserById(userDto.getId());
        updatedUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(updatedUser);
        return userDto;
    }

    @Override
    public void deactivateUser(long userId) {
        User user = findUserById(userId);
        user.setActive(false);
        deleteUserGoals(user);
        deletePlannedUserEvents(user);
        mentorshipService.stopMentorship(userId);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("user with id=%d not found", userId)));
    }

    private void deleteUserGoals(User user) {
        List<Goal> goals = user.getGoals();
        goals.forEach(goal -> {
            if (goal.getUsers().size() == 1) {
                goalService.deleteById(goal.getId());
            }
        });
    }

    private void deletePlannedUserEvents(User user) {
        List<Event> events = user.getOwnedEvents();
        for (Event event : events) {
            if (event.getStatus() == EventStatus.PLANNED) {
                eventService.deleteById(event.getId());
            }
        }
    }

    private List<Event> getParticipateEvents(UserDTO userDto) {
        return userDto.getParticipatedEventIds().stream()
                .map(eventService::findEventById)
                .toList();
    }
}