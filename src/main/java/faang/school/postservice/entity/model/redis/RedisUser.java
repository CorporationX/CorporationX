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
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "User")
public class RedisUser implements Serializable {

    @Id
    private Long id;

    private String username;
    private HashSet<Long> followingsIds;
    private HashSet<Long> followersIds;

    @Version
    private Long version;

    @TimeToLive
    private Long ttl;

    public void incrementVersion() {
        this.version++;
    }
}