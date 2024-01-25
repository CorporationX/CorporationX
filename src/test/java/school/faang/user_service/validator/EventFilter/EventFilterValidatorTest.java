package school.faang.user_service.validator.EventFilter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.eventFilter.EventFilterValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventFilterValidatorTest {
    @InjectMocks
    private EventFilterValidator eventFilterValidator;

    @Test
    public void shouldFailedCheckFilterNotNullWhenFilterIsNull() {
        EventFilterDto nullFilter = null;
        assertThrows(DataValidationException.class,
                () -> eventFilterValidator.checkFilterNotNull(nullFilter));
    }
}
