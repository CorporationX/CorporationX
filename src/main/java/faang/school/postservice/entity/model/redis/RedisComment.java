package faang.school.postservice.entity.model.redis;

import faang.school.postservice.entity.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.util.SortedSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Comment", timeToLive = 86400)
public class RedisComment {

    @Id
    private Long id;

    private CommentDto commentDto;

    SortedSet<Long> redisLikesIds;

    @Version
    private Long version;

    public void incrementVersion() {
        this.version++;
    }
}