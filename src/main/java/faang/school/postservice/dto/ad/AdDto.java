package faang.school.postservice.dto.ad;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdDto {
    private long id;
    private long postId;
    private long buyerId;
    private int appearancesLeft;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
