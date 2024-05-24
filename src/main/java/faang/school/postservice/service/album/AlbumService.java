package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;

import java.util.List;

public interface AlbumService {
    AlbumDto createAlbum(long userId, AlbumDto albumDto);

    AlbumDto addPostToAlbum(long albumId, long postId, long userId);

    AlbumDto addAlbumToFavorites(long albumId, long userId);

    List<AlbumDto> getAllUserAlbums(long userId, AlbumFilterDto filters);

    List<AlbumDto> getAllUserFavoriteAlbums(long userId, AlbumFilterDto filters);

    List<AlbumDto> getAllAlbums(long userId, AlbumFilterDto filters);

    AlbumDto getAlbumById(long userId, long albumId);

    AlbumDto updateAlbum(long albumId, long userId, AlbumDto albumDto);

    AlbumDto deleteAlbum(long albumId, long userId);

    AlbumDto removeAlbumFromFavorite(long albumId, long userId);

    AlbumDto removePostFromAlbum(long albumId, long postId, long userId);
}
