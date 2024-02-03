package school.faang.user_service.validator.eventFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class EventFilterValidator {
    public void checkFilterNotNull(EventFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataValidationException("Filter cannot be null");
        }
    }

}
