package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public UserDto validateUserExistence(long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("can't find user with userId:" + userId);
        }
    }
}