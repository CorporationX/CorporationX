package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.user.UserService;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/")
    public UserDTO createUser(@RequestBody UserDTO userDto) {
        userService.createUser(userDto);
        return UserDTO.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .build();
    }

    @PutMapping("/deactivate/{id}")
    public void deactivateUser(@PathVariable(name = "id") long userId) {
        userService.deactivateUser(userId);
    }
}