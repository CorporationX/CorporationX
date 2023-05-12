package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private long id;
    @NotBlank(message = "username should not be blank")
    private String username;
    @NotBlank(message = "email should not be blank")
    @Email(message = "should be email format")
    private String email;
}
