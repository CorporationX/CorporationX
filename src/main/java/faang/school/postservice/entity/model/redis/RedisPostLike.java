package faang.school.postservice.entity.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "PostLike", timeToLive = 86400) // timeToLive 24 hr
public class RedisPostLike implements Serializable {

    @Id
    private Long id;
    private Long userId;
    private Long postId;
}