package faang.school.postservice.entity.model.redis;

import faang.school.postservice.entity.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.SortedSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "User", timeToLive = 86400)
public class RedisUser implements Serializable {

    @Id
    private Long id;

    private UserDto userDto;
}