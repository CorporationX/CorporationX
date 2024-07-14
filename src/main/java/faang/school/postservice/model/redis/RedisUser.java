package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "User", timeToLive = 86400)
public class RedisUser implements Serializable {

    @Id
    private long id;
    private String username;
    private List<Long> followerIds;
    private List<Long> followeeIds;
}