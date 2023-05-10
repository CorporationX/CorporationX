package faang.school.postservice.service.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public abstract class AlbumFilter {

    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto filter) {
        return albums.filter(album -> applyFilter(album, filter));
    }

    protected abstract boolean applyFilter(Album user, AlbumFilterDto filter);

    public abstract boolean isApplicable(AlbumFilterDto filter);
}
