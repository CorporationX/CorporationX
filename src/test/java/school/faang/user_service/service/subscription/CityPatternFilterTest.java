package school.faang.user_service.service.subscription;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@ExtendWith(MockitoExtension.class)
class CityPatternFilterTest {

    @Mock
    private User user;

    @Mock
    private UserFilterDto filter;

    @InjectMocks
    private CityPatternFilter cityPatternFilter;

    @Test
    void testCheckWhenCityPatternIsNull() {
        when(filter.getCityPattern()).thenReturn(null);

        boolean result = cityPatternFilter.check(user, filter);

        assertTrue(result);
    }

    @Test
    void testCheckWhenCityPatternMatches() {
        when(user.getCity()).thenReturn("New York");
        when(filter.getCityPattern()).thenReturn("New .*");

        boolean result = cityPatternFilter.check(user, filter);

        assertTrue(result);
    }

    @Test
    void testCheckWhenCityPatternDoesNotMatch() {
        when(user.getCity()).thenReturn("Saint Petersburg");
        when(filter.getCityPattern()).thenReturn("New .*");

        boolean result = cityPatternFilter.check(user, filter);

        assertFalse(result);
    }
}
