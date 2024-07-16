package faang.school.postservice.entity.model.redis;

import faang.school.postservice.entity.dto.user.UserDto;
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
@RedisHash(value = "User", timeToLive = 86400) // timeToLive 24 hr
public class RedisUser implements Serializable {

    @Id
    private Long id;

    private UserDto userDto;

    private SortedSet<Long> followersIds;

    @Version
    private Long version;

    public void incrementVersion() {
        this.version++;
    }
}