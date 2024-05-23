package school.faang.user_service.service.subscription;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;

@ExtendWith(MockitoExtension.class)
class AboutPatternFilterTest {

    @Mock
    private User user;

    @Mock
    private UserFilterDTO filter;

    @InjectMocks
    private AboutPatternFilter aboutPatternFilter;

    @Test
    void testCheckWhenAboutPatternIsNull() {
        when(filter.getAboutPattern()).thenReturn(null);

        boolean result = aboutPatternFilter.check(user, filter);

        assertTrue(result);
    }

    @Test
    void testCheckWhenAboutPatternMatches() {
        when(user.getAboutMe()).thenReturn("I love programming in Java.");
        when(filter.getAboutPattern()).thenReturn(".*programming.*");

        boolean result = aboutPatternFilter.check(user, filter);

        assertTrue(result);
    }

    @Test
    void testCheckWhenAboutPatternDoesNotMatch() {
        when(user.getAboutMe()).thenReturn("I love programming in Java.");
        when(filter.getAboutPattern()).thenReturn(".*python.*");

        boolean result = aboutPatternFilter.check(user, filter);

        assertFalse(result);
    }
}
