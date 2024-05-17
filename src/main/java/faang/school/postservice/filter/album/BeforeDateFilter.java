package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Component
public class BeforeDateFilter implements AlbumFilter {

    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getBeforeDate() != null;
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDate beforeDate = LocalDate.parse(albumFilterDto.getBeforeDate(), DateTimeFormatter.ISO_DATE);
        return albums.filter(album -> album.getCreatedAt().toLocalDate().isBefore(ChronoLocalDate.from(beforeDate))
                                      || album.getCreatedAt().toLocalDate().isEqual(ChronoLocalDate.from(beforeDate)));
    }
}