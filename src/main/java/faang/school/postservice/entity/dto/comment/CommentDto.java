package faang.school.postservice.entity.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
//    private LocalDateTime createdAt; //TODO
//    private LocalDateTime updatedAt;
    private List<Long> likesIds;
}
