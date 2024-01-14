package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        user2 = new User();
        user2.setId(-1L);
    }

    @Test
    @DisplayName("Провал валидации названия события")
    public void testFailedWithEmptyTitleEvent() {
        EventDto eventDtoEmptyTitle = EventDto.builder()
                .title("")
                .startDate(LocalDateTime.now())
                .ownerId(user.getId())
                .build();

        assertThrows(DataValidationException.class,
                () -> eventController.create(eventDtoEmptyTitle));
    }

    @Test
    @DisplayName("Провал валидации не верной даты")
    public void testFailedWithEventNotValidDate() {
        EventDto eventDtoNotValidDate = EventDto.builder()
                .title("Event1")
                .startDate(LocalDateTime.now())
                .ownerId(user.getId())
                .build();


        assertThrows(DataValidationException.class,
                () -> eventController.create(eventDtoNotValidDate));
    }

    @Test
    @DisplayName("Провал валидации несуществующего пользователя")
    public void testFailedWithEventOwnerNotValidId() {
        EventDto eventDtoOwnerNotValidId = EventDto.builder()
                .title("Event2")
                .startDate(LocalDateTime.now())
                .ownerId(user2.getId())
                .build();

        assertThrows(DataValidationException.class,
                () -> eventController.create(eventDtoOwnerNotValidId));
    }

}
