package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilterService;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumValidator albumValidator;
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumFilterService albumFilterService;


    @Override
    public AlbumDto createAlbum(long userId, AlbumDto albumDto) {

        Album album = albumMapper.toEntity(albumDto);

        albumValidator.validateCreateAlbum(userId, album);

        albumRepository.save(album);

        return albumDto;
    }

    @Override
    public AlbumDto addPostToAlbum(long albumId, long postId, long userId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        Post post = postRepository.findById(postId).
                orElseThrow(() -> new NotFoundException(String.format("Post with id %s not found", postId)));

        albumValidator.validateAddPostToAlbum(album, postId, userId);

        album.addPost(post);
        albumRepository.save(album);

        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto addAlbumToFavorites(long albumId, long userId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        albumValidator.validateAddAlbumToFavorites(album, userId);

        albumRepository.addAlbumToFavorites(albumId, userId);

        return albumMapper.toDto(album);
    }

    @Override
    public List<AlbumDto> getAllUserAlbums(long userId, AlbumFilterDto filters) {

        return albumFilterService.applyFilters(albumRepository.findByAuthorId(userId), filters)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    public List<AlbumDto> getAllUserFavoriteAlbums(long userId, AlbumFilterDto filters) {

        return albumFilterService.applyFilters(albumRepository.findFavoriteAlbumsByUserId(userId), filters)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    public List<AlbumDto> getAllAlbums(AlbumFilterDto filters) {

        Iterable<Album> albums = albumRepository.findAll();

        return albumFilterService.applyFilters(StreamSupport.stream(albums.spliterator(), false), filters)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    public AlbumDto getAlbumById(long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto updateAlbum(long albumId, long userId, AlbumDto updatedAlbumDto) {

        Album albumToUpdate = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        albumValidator.validateUpdateAlbum(albumToUpdate, userId, updatedAlbumDto);

        albumMapper.update(updatedAlbumDto, albumToUpdate);

        return albumMapper.toDto(albumRepository.save(albumToUpdate));
    }

    @Override
    public AlbumDto deleteAlbum(long albumId, long userId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        albumValidator.validateDeleteAlbum(album, userId);

        albumRepository.deleteById(albumId);

        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto removeAlbumFromFavorite(long albumId, long userId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        albumValidator.validateRemoveAlbumFromFavorite(album, userId);

        albumRepository.deleteAlbumFromFavorites(albumId, userId);

        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto removePostFromAlbum(long albumId, long postId, long userId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(String.format("Album with id %s not found", albumId)));

        albumValidator.validateRemovePostFromAlbum(album, postId, userId);

        album.removePost(postId);
        albumRepository.save(album);

        return albumMapper.toDto(album);
    }
}
