package faang.school.postservice.mapper.redis;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.Post;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisPostMapper {

    RedisPost toRedisPost(PostDto postDto);
}