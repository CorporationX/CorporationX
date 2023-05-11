package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @PostMapping("/user/list")
    public List<UserDto> getUsers(@RequestBody UserFilterDto filter) {
        return userService.getUsers(filter);
    }

    @PostMapping("/users")
    public List<UserDto> getUsers(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/user/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }
}
