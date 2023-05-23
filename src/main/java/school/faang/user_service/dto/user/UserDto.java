package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
public class UserDto {
    private long id;
    @NotBlank(message = "username should not be blank")
    private String username;
    @NotBlank(message = "email should not be blank")
    @Email(message = "should be email format")
    private String email;

    private String phone;
    private PreferredContact preference;
}
