package school.faang.user_service.dto.premium;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PremiumDto {
    private long id;
    private long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
