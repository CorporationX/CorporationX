package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.event.SkillMapper;
import school.faang.user_service.mapper.event.SkillMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Spy
    private SkillMapper skillMapper = new SkillMapperImpl();
    @Spy
    private EventMapper eventMapper = new EventMapperImpl(skillMapper);
    @InjectMocks
    private EventService eventService;
    @Mock
    private UserRepository userRepository;

    //to testSuccessfulCreateEventIsValidate
    private EventDto eventDto;
    private Event event;
    private User user;

    //to testFailedCreateEventNotValidate
    private EventDto eventDto2;
    private Event event2;
    private User user2;
    private User user3;
    SkillDto skillDto = SkillDto.builder().id(1L).build();

    @BeforeEach
    public void init() {
        //to testCreateEventIsValidate
        user = new User();
        user.setId(1L);

        List<User> users = new ArrayList<>(List.of(user));
        Skill skill = Skill.builder()
                .id(1L)
                .build();
        user.setSkills(new ArrayList<>(List.of(skill)));

        eventDto = EventDto.builder()
                .id(1L)
                .ownerId(user.getId())
                .title("Event1")
                .relatedSkills(List.of(skillDto))
                .build();
        eventService = new EventService(eventRepository, eventMapper, userRepository);

        //to testCreateEventNotValidate
        user2 = new User();
        user2.setId(2L);

        List<User> users2 = new ArrayList<>(List.of(user));

        eventDto2 = EventDto.builder()
                .id(2L)
                .ownerId(user2.getId()).title("Event2").relatedSkills(List.of(skillDto)).build();
        event2 = eventMapper.toEntity(eventDto);

        user3 = new User();
        user3.setId(-1L);
    }

    @Test
    @DisplayName("Успешное создание события")
    public void testSuccessfulCreateEventIsValid() {
        event = eventMapper.toEntity(eventDto);

        Mockito.when(eventRepository.save(event)).thenReturn(event);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        EventDto eventDto1 = eventMapper.toDto(event);
        eventService.create(eventDto1);

        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventRepository, Mockito.times(1)).save(argumentCaptor.capture());
        Event capturedEvent = argumentCaptor.getValue();

        assertEquals(event.getTitle(), capturedEvent.getTitle());
        assertEquals(event.getRelatedSkills(), capturedEvent.getRelatedSkills());
    }

    @Test
    @DisplayName("Неуспешное создание события")
    public void testFailedCreateEventNotValid() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto2));
    }
}
