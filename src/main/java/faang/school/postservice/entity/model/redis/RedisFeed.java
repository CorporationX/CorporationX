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
import java.util.LinkedHashSet;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "Feed")
public class RedisFeed implements Serializable {

    @Id
    private Long userId;

    private LinkedHashSet<Long> postsIds;

    @Version
    private Long version;

    @TimeToLive
    private Long ttl;

    public void incrementVersion() {
        this.version++;
    }
}