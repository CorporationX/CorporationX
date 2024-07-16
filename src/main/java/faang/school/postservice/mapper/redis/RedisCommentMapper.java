package faang.school.postservice.mapper.redis;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCommentMapper {

    RedisUser toEntity(CommentDto userDto);

    RedisComment toRedisDto(CommentDto comment);
}