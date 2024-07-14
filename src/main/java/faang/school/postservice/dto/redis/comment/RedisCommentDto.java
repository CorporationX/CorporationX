package faang.school.postservice.dto.redis.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisCommentDto {

    private long id;
    private String content;
    private long authorId;
    private Integer likes;
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public synchronized void likeIncrement(){
        likes++;
    }

    public synchronized void likeDecrement(){
        likes--;
    }
}