package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumFilterServiceImpl implements AlbumFilterService {
    private final List<AlbumFilter> filters;

    @Override
    public Stream<Album> applyFilters(Stream<Album> albums, AlbumFilterDto filterDto) {
        if (filterDto != null) {
            albums = filters.stream()
                    .filter(filter -> filter.isAcceptable(filterDto))
                    .reduce(albums, (acc, filter) -> filter.applyFilter(acc, filterDto), (a, b) -> b);
        }
        return albums;
    }
}
