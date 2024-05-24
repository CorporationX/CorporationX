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
import faang.school.postservice.validator.user.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceImplTest {

    @Mock
    private AlbumValidator albumValidator;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AlbumFilterService albumFilterService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void testCreateAlbum() {
        AlbumDto albumDto = new AlbumDto();
        Album album = new Album();

        when(albumMapper.toEntity(albumDto)).thenReturn(album);
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto result = albumService.createAlbum(1L, albumDto);

        assertNotNull(result);
        verify(albumMapper, times(1)).toEntity(albumDto);
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).validateAlbumTitleIsUnique(anyLong(), any());
        verify(albumRepository, times(1)).save(any(Album.class));
    }

    @Test
    void testAddPostToAlbum() {
        Album album = new Album();
        album.setPosts(new ArrayList<>());
        Post post = new Post();
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.addPostToAlbum(1L, 1L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).checkPostExistenceInAlbum(any(Album.class), anyLong());
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testAddAlbumToFavorites() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.addAlbumToFavorites(1L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).validateAlbumExistence(any(Album.class), anyLong());
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testGetAllUserAlbums() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();
        List<Album> albums = List.of(album);
        AlbumFilterDto filter = new AlbumFilterDto();

        when(albumRepository.findByAuthorId(1L)).thenReturn(albums.stream());
        when(albumFilterService.applyFilters(any(), any(AlbumFilterDto.class)))
                .thenReturn(albums.stream());
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAllUserAlbums(1L, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository).findByAuthorId(1L);
        verify(albumFilterService).applyFilters(any(), any(AlbumFilterDto.class));
        verify(albumMapper).toDto(any(Album.class));
    }

    @Test
    void testGetAllUserFavoriteAlbums() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();
        List<Album> albums = List.of(album);
        AlbumFilterDto filter = new AlbumFilterDto();

        when(albumRepository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(albums.stream());
        when(albumFilterService.applyFilters(any(), any(AlbumFilterDto.class)))
                .thenReturn(albums.stream());
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAllUserFavoriteAlbums(1L, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
        verify(albumFilterService, times(1)).applyFilters(any(), any(AlbumFilterDto.class));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testGetAllAlbums() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();
        List<Album> albums = List.of(album);
        AlbumFilterDto filter = new AlbumFilterDto();

        when(albumRepository.findAll()).thenReturn(albums);
        when(albumFilterService.applyFilters(any(), any(AlbumFilterDto.class)))
                .thenReturn(albums.stream());
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumValidator.validateAccess(any(Album.class), anyLong())).thenReturn(true);

        List<AlbumDto> result = albumService.getAllAlbums(1L, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository, times(1)).findAll();
        verify(albumFilterService, times(1)).applyFilters(any(), any(AlbumFilterDto.class));
        verify(albumValidator, times(1)).validateAccess(any(Album.class), eq(1L));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testGetAlbumById() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.getAlbumById(2L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateAccess(any(Album.class), eq(2L));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testUpdateAlbum() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto result = albumService.updateAlbum(1L, 1L, albumDto);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).validateAlbumTitleIsUnique(anyLong(), any());
        verify(albumMapper, times(1)).toDto(any(Album.class));
        verify(albumMapper, times(1)).update(any(AlbumDto.class), any(Album.class));
        verify(albumRepository, times(1)).save(any(Album.class));
    }

    @Test
    void testDeleteAlbum() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.deleteAlbum(1L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumRepository, times(1)).deleteById(anyLong());
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testRemoveAlbumFromFavorite() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.removeAlbumFromFavorite(1L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).validateAlbumExistence(any(Album.class), anyLong());
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(anyLong(), anyLong());
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testRemovePostFromAlbum() {
        Album album = new Album();
        album.setPosts(new ArrayList<>());
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.removePostFromAlbum(1L, 1L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).validateUserExistence(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).checkPostExistenceInAlbum(any(Album.class), anyLong());
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void testFindById() {
        Album album = new Album();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));

        Album result = albumService.findById(albumRepository, 1L, "Album");

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
    }

    @Test
    void testFindByIdNotFound() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> albumService.findById(albumRepository, 1L, "Album"));

        verify(albumRepository, times(1)).findById(anyLong());
    }
}
