package faang.school.postservice.entity.model.redis;

import faang.school.postservice.entity.dto.user.UserDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "User")
public class RedisUser implements Serializable {

    @Id
    private Long id;

    private UserDto userDto;

    @Version
    private Long version;

    @TimeToLive
    private Long ttl;

    public void incrementVersion() {
        this.version++;
    }
}