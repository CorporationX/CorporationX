package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


}
