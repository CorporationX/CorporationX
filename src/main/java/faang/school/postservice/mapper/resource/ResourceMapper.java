package faang.school.postservice.mapper.resource;

import faang.school.postservice.entity.dto.resource.ResourceDto;
import faang.school.postservice.entity.model.Resource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceDto toDto(Resource resource);
}
