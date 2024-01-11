package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;


    public EventDto create(EventDto event) {
        if (!isOwnerHasSkill(event)) {
            throw new DataValidationException("Owner hasn't required skill");
        }
        Event eventEntity = eventMapper.toEntity(event);
        eventRepository.save(eventEntity);
        return eventMapper.toDto(eventEntity);
    }

    public boolean isOwnerHasSkill(EventDto event) {
        Optional<User> owner = userRepository.findById(event.getOwnerId());
        return owner.map(user -> user.getSkills().stream().anyMatch(skill -> (event.getRelatedSkills()).contains(skill))).orElse(false);
    }
}
