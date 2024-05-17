package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    AlbumDto toDto(Album album);

    Album toEntity(AlbumDto albumDto);

    void update(AlbumDto dto, @MappingTarget Album entity);
}