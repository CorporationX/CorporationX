package faang.school.postservice.entity.model.redis;

import faang.school.postservice.entity.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.SortedSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post", timeToLive = 604800) // timeToLive 1 Week
public class RedisPost implements Serializable {

    @Id
    private Long id;

    private PostDto postDto;

    SortedSet<Long> redisCommentsIds;

    SortedSet<Long> viewerIds;

    @Version
    private Long version;

    public void incrementVersion() {
        this.version++;
    }
}