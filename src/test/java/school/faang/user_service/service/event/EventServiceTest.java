package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private UserService userService;
    @Mock
    private EventValidator eventValidator;

    @Test
    void successUpdateEventWhenAllConditionsValid() {
        Skill skillFirst = Skill.builder()
                .id(1L)
                .build();
        Skill skillSecond = Skill.builder()
                .id(2L)
                .build();
        List<Skill> skills = List.of(skillFirst, skillSecond);
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(List.of(skillFirst, skillSecond))
                .build();
        List<Long> ownerSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        EventDto eventDtoExpected = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("EventFirst")
                .relatedSkillIds(ownerSkillIds)
                .maxAttendees(2)
                .startDate(LocalDateTime.now().plusDays(1))
                .build();
        Event eventEntity = eventMapper.toEntity(eventDtoExpected);
        Mockito.when(eventRepository.findById(eventDtoExpected.getId())).thenReturn(Optional.ofNullable(eventEntity));
        Mockito.when(userService.findUserById(eventDtoExpected.getOwnerId())).thenReturn(owner);
        eventEntity.setRelatedSkills(skills);
        Mockito.when(eventRepository.save(eventEntity)).thenReturn(eventEntity);

        EventDto eventDtoActual = eventService.updateEvent(eventDtoExpected);
        Mockito.verify(eventRepository, times(1)).save(eventEntity);
        Mockito.verify(eventMapper, times(1)).toDto(eventRepository.save(eventEntity));
        assertEquals(eventDtoExpected, eventDtoActual);
    }

    @Test
    void shouldThrowUpdateEventWhenOwnerNotExist() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("EventFirst")
                .build();

        assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(eventDto));
    }

    @Test
    void shouldThrowUpdateEventWhenEventNotExist() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(eventDto));
    }

    @Test
    void successCheckIfEventExistsWhenExists() {
        Event eventEntity = Event.builder()
                .id(1L)
                .maxAttendees(2)
                .title("EventFirst")
                .build();
        long eventId = eventEntity.getId();
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(eventEntity));
        eventService.checkIfEventExists(eventId);
    }

    @Test
    void shouldThrowCheckIfEventExistsWhenNotExists() {
        long eventId = 1L;
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> eventService.checkIfEventExists(eventId));
    }

    @Test
    void shouldThrowCheckIfEventNotStarted() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1L);
        assertThrows(DataValidationException.class,
                () -> eventService.checkIfEventNotStarted(startDate));
    }
}
