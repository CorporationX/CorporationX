package faang.school.postservice.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HashtagDto {
    private Long id;
    private String hashtag;
    private Long postId;
    private LocalDateTime createdAt;
}
