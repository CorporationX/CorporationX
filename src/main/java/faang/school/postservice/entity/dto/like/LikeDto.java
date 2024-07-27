package faang.school.postservice.entity.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long id;
    private Long userId;
    private Long authorId;
    private Long commentId;
    private Long postId;
}
