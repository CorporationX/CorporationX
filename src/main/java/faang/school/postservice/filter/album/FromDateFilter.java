package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Component
public class FromDateFilter implements AlbumFilter {

    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getFromDate() != null;
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDate fromDate = LocalDate.parse(albumFilterDto.getFromDate(), DateTimeFormatter.ISO_DATE);
        return albums.filter(album -> album.getCreatedAt().toLocalDate().isAfter(ChronoLocalDate.from(fromDate))
                                      || album.getCreatedAt().toLocalDate().isEqual(ChronoLocalDate.from(fromDate)));
    }
}