package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.redis.comment.RedisCommentDto;
import faang.school.postservice.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCommentMapper {

    RedisUser toEntity(CommentDto userDto);

    RedisCommentDto toRedisDto(CommentDto comment);
}