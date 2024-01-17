package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.SubscriptionValidator;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    public void testUserExists() {
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validate(1L));
    }
}
