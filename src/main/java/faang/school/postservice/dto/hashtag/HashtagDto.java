package faang.school.postservice.dto.hashtag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HashtagDto {
    private Long id;
    private String hashtag;
    private Long postId;
}
