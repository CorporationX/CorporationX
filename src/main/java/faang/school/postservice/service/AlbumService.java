package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.filter.AlbumFilter;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumValidator albumValidator;
    private final AlbumMapper albumMapper;
    private final List<AlbumFilter> filters;
    private final PostService postService;

    @Transactional
    public AlbumDto createAlbum(AlbumDto album) {
        albumValidator.validateCreation(album);
        Album entity = albumMapper.toEntity(album);
        return albumMapper.toDto(albumRepository.save(entity));
    }

    @Transactional
    public AlbumDto updateAlbum(long id, AlbumDto album) {
        Album entity = findById(id);
        albumValidator.validateUpdate(entity, album);
        albumMapper.update(entity, album);
        return albumMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserAlbums(long authorId, AlbumFilterDto filter) {
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        return applyFilters(albums, filter);
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbum(long id) {
        return albumMapper.toDto(findById(id));
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAlbums(AlbumFilterDto filter) {
        Stream<Album> albums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        return applyFilters(albums, filter);
    }

    @Transactional
    public void deleteAlbum(long id) {
        albumRepository.deleteById(id);
    }

    @Transactional
    public AlbumDto addPostToAlbum(long albumId, long postId) {
        Album album = findById(albumId);
        Post post = postService.findById(postId);
        album.addPost(post);
        return albumMapper.toDto(album);
    }

    @Transactional
    public AlbumDto removePostFromAlbum(long albumId, long postId) {
        Album album = findByIdWithPosts(albumId);
        album.removePost(postId);
        return albumMapper.toDto(album);
    }

    @Transactional
    public AlbumDto addAlbumToFavorites(long userId, long albumId) {
        Album album = findById(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
        return albumMapper.toDto(album);
    }

    @Transactional
    public AlbumDto removeAlbumFromFavorites(long userId, long albumId) {
        Album album = findById(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        return albumMapper.toDto(album);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserFavoriteAlbums(long userId, AlbumFilterDto filter) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return applyFilters(albums, filter);
    }

    private Album findById(long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album with id " + id + " not found"));
    }

    private Album findByIdWithPosts(long id) {
        return albumRepository.findByIdWithPosts(id)
                .orElseThrow(() -> new EntityNotFoundException("Album with id " + id + " not found"));
    }

    private List<AlbumDto> applyFilters(Stream<Album> albums, AlbumFilterDto filter) {
        filters.stream()
                .filter(albumFilter -> albumFilter.isApplicable(filter))
                .forEach(albumFilter -> albumFilter.applyFilter(albums, filter));
        return albums
                .skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .map(albumMapper::toDto)
                .toList();
    }
}
