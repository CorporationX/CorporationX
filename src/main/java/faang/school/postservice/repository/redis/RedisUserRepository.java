package faang.school.postservice.repository.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.SortedSet;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {

    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, RedisUser> redisUsersTemplate;
    private static final String ZSET_KEY = "redisUserZSet";

    public RedisUser getById(Long id) {
        Set<RedisUser> resultSet = redisUsersTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public RedisUser getById(Long userId, SortedSet<Long> followersIds) {
        Set<RedisUser> resultSet = redisUsersTemplate.opsForZSet().rangeByScore(ZSET_KEY, userId, userId);
        if (resultSet == null || resultSet.isEmpty()) {
            return buildNewRedisUser(userId, followersIds);
        } else {
            return resultSet.iterator().next();
        }
    }

    public void save(Long id, RedisUser redisUser) {
        redisUsersTemplate.opsForZSet().add(ZSET_KEY, redisUser, id);
    }

    public void remove(Long id) {
        RedisUser redisUser = getById(id);
        if (redisUser != null) {
            redisUsersTemplate.opsForZSet().remove(ZSET_KEY, redisUser);
        }
    }

    public void update(Long id, RedisUser redisUser) {
        remove(id);
        save(id, redisUser);
    }

    private RedisUser buildNewRedisUser(Long userId, SortedSet<Long> followersIds) {
        UserDto userDto = userServiceClient.getUser(userId);
        return RedisUser.builder()
                .id(userId)
                .userDto(userDto)
                .followersIds(followersIds)
                .version(1L)
                .build();
    }
}