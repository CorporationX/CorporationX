package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserService userService;
    private final UserRepository userRepository;

    public void validateEventInController(EventDto eventDto) {
        validateTitle(eventDto);
        validateDate(eventDto);
        validateSkills(eventDto);
    }

    public void validateEventToUpdate(EventDto eventDto) {
        checkIfEventNotStarted(eventDto.getStartDate());
        userService.checkIfOwnerExists(eventDto.getOwnerId());
        checkIfOwnerHasSkillsRequired(eventDto);
    }

    public void validateDate(EventDto eventDto) {
        if (eventDto.getStartDate() != null && eventDto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Date not valid");
        }
    }

    public void validateTitle(EventDto eventDto) {
        if (eventDto.getTitle() != null && eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Title not valid");
        }
    }


    public void validateSkills(EventDto eventDto) {
        if (eventDto.getRelatedSkillIds() != null && eventDto.getRelatedSkillIds().isEmpty()) {
            throw new DataValidationException("Related skills not valid");
        }
    }

    public void checkIfOwnerHasSkillsRequired(EventDto eventDto) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Owner not found"));
        List<Long> ownerSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        if (!new HashSet<>(ownerSkillIds)
                .containsAll(eventDto.getRelatedSkillIds())) {
            throw new DataValidationException("Owner does not have required skills.");
        }
    }

    public void checkIfOwnerExistsById(long id) {
        if (!userService.checkIfOwnerExistsById(id)) {
            throw new DataValidationException("Owner does not exist");
        }
    }

    public void checkIfEventNotStarted(LocalDateTime startDate) {
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Event were started");
        }
    }

}
