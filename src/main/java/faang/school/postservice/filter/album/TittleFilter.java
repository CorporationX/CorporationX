package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TittleFilter implements AlbumFilter {

    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null;
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> album.getTitle().startsWith(albumFilterDto.getTitlePattern()));
    }
}