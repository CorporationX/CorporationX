package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilterService {
    Stream<Album> applyFilters(Stream<Album> albums, AlbumFilterDto filterDto);
}