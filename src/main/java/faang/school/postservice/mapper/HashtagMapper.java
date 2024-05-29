package faang.school.postservice.mapper;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.model.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {

    @Mapping(source = "postId", target = "post.id")
    Hashtag toEntity(HashtagDto hashtagDto);

    @Mapping(source = "post.id", target = "postId")
    HashtagDto toDto(Hashtag hashtag);
}
