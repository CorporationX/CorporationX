package faang.school.postservice.entity.model.redis;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "Feed", timeToLive = 86400)
public class RedisFeed implements Serializable {

    @Id
    private Long userId;

    private SortedSet<RedisPost> posts;

    @Version
    private Long version;

    public void incrementVersion() {
        this.version++;
    }
}