package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisUserMapper {

    RedisUser toEntity(UserDto userDto);
}
