package school.faang.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserProfilePic {
    private String fileId;
    private String smallFileId;
}