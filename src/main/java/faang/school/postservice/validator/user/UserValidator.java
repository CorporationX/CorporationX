package faang.school.postservice.validator.user;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExistence(long userId) {
//        try { //TODO uncomment
//            log.debug("Fetching user with ID: " + userId);
//            userServiceClient.getUser(userId);
//        } catch (FeignException.NotFound e) {
//            throw new NotFoundException(String.format("User with id '%d' not exist", userId));
//        } catch (FeignException e) {
//            throw new RuntimeException("Error fetching user", e);
//        }
    }
}