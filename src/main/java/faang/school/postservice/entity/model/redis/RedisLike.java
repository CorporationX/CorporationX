package faang.school.postservice.entity.model.redis;

import faang.school.postservice.entity.dto.like.LikeDto;
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
@RedisHash(value = "Like", timeToLive = 86400)
public class RedisLike implements Serializable {

    @Id
    private Long id;

    private LikeDto likeDto;
}