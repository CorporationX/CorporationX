package faang.school.postservice.entity.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class RedisPost implements Serializable {

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private HashSet<Long> likesIds;
    private HashSet<Long> viewersIds;
    private LinkedHashSet<Long> commentsIds;
    private LocalDateTime publishedAt;

    @TimeToLive
    private Long ttl;

    @Version
    private Long version;

    public void incrementVersion() {
        this.version++;
    }
}