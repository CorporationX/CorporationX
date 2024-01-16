package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.SubscriptionValidator;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    public void testNonFollowedThrowsException() {
        Mockito.when(subscriptionRepo.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(false);
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validate(1L, 2L));
    }

    @Test
    public void testUnfollowUserThrowsException() {
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validate(1L, 1L));
    }

    @Test
    public void testUserNonExists() {
        Mockito.when(subscriptionRepo.existsByFollowerIdAndFolloweeId(1L, 2L))
                .thenReturn(true);
        Mockito.lenient().when(userRepo.existsById(1L)).thenReturn(true);
        Mockito.lenient().when(userRepo.existsById(2L)).thenReturn(false);
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validate(1L, 2L));
    }
}
