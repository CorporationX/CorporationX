package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.user.UserService;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/")
    public UserDTO createUser(@RequestBody UserDTO userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable long userId) {
        return userService.findById(userId);
    }

    @GetMapping("/")
    public List<UserDTO> getAll() {
        return userService.findAll();
    }

    @PutMapping("/deactivate/{id}")
    public void deactivateUser(@PathVariable(name = "id") long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("existsById/{userId}")
    public Boolean existById(@PathVariable long userId) {
        return userService.existsById(userId);
    }
}