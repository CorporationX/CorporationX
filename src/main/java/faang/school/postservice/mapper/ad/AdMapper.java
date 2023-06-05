package faang.school.postservice.mapper.ad;

import faang.school.postservice.dto.ad.AdDto;
import faang.school.postservice.model.ad.Ad;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdMapper {

    @Mapping(target = "postId", source = "post.id")
    AdDto toDto(Ad ad);
}
