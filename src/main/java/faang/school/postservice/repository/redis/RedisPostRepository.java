package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.TreeSet;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {

    private final RedisTemplate<String, RedisPost> redisPostsTemplate;
    private final RedisPostRepository redisPostRepository;
    private final PostService postService;
    private static final String ZSET_KEY = "redisPostZSet";

    public RedisPost getById(Long postId) {
        Set<RedisPost> resultSet = redisPostsTemplate.opsForZSet().rangeByScore(ZSET_KEY, postId, postId);
        if (resultSet == null || resultSet.isEmpty()) {
            return buildNewRedisPost(postId);
        } else {
            return resultSet.iterator().next();
        }
    }

    public void save(Long id, RedisPost redisPost) {
        redisPostsTemplate.opsForZSet().add(ZSET_KEY, redisPost, id);
    }

    public void remove(Long id) {
        RedisPost redisPost = getById(id);
        if (redisPost != null) {
            redisPostsTemplate.opsForZSet().remove(ZSET_KEY, redisPost);
        }
    }

    private RedisPost buildNewRedisPost(Long postId) {
        PostDto postDto = postService.getById(postId);
        RedisPost redisPost = RedisPost.builder()
                .id(postId)
                .postDto(postDto)
                .redisCommentsIds(new TreeSet<>())
                .redisLikesIds(new TreeSet<>())
                .viewerIds(new TreeSet<>())
                .version(1L)
                .build();
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }
}