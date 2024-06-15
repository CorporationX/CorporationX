package school.faang.user_service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.user.UserService;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectSubscriptionValidatorTest {
    private static final long USER_ID = 1L;
    private static final long PROJECT_ID = 2L;

    @Mock
    private UserService userService;
    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;
    @InjectMocks
    private ProjectSubscriptionValidator projectSubscriptionValidator;

    @Test
    public void whenValidateProjectSubscriptionAnsUserNotExistThenThrowsException() {
        when(userService.existsById(USER_ID)).thenThrow(NoSuchElementException.class);
        Assert.assertThrows(NoSuchElementException.class,
                () -> projectSubscriptionValidator.validateProjectSubscription(USER_ID, PROJECT_ID));
    }

    @Test
    public void whenValidateProjectSubscriptionAnsProjectNotExistThenThrowsException() {
        when(userService.existsById(USER_ID)).thenReturn(true);
        when(projectSubscriptionRepository.existsById(PROJECT_ID)).thenReturn(false);
        Assert.assertThrows(NoSuchElementException.class,
                () -> projectSubscriptionValidator.validateProjectSubscription(USER_ID, PROJECT_ID));
    }
}