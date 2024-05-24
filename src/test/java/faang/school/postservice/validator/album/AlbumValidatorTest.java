package faang.school.postservice.validator.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.user.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private AlbumValidator albumValidator;

    @Test
    void testValidateCreateAlbum() {
        Album album = new Album();
        album.setTitle("Test Album");

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.empty());

        assertDoesNotThrow(() -> albumValidator.validateCreateAlbum(1L, album));

        verify(userValidator).validateUserExistence(anyLong());
        verify(albumRepository).findByAuthorId(anyLong());
    }

    @Test
    void testValidateAddPostToAlbum() {
        Album album = new Album();
        album.setAuthorId(1L);
        Post post = new Post();
        post.setId(1L);

        album.setPosts(List.of(post));

        assertThrows(DataValidationException.class, () -> albumValidator.validateAddPostToAlbum(album, 1L, 1L));
    }

    @Test
    void testValidateAddAlbumToFavorites() {
        Album album = new Album();
        album.setAuthorId(1L);

        when(albumRepository.checkAlbumExistsInFavorites(anyLong(), anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> albumValidator.validateAddAlbumToFavorites(album, 1L));
    }

    @Test
    void testValidateUpdateAlbum() {
        Album oldAlbum = new Album();
        oldAlbum.setAuthorId(1L);
        oldAlbum.setTitle("Old Title");

        AlbumDto updatedAlbumDto = new AlbumDto();
        Album updatedAlbum = new Album();
        updatedAlbum.setTitle("New Title");

        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(updatedAlbum);
        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.empty());

        assertDoesNotThrow(() -> albumValidator.validateUpdateAlbum(oldAlbum, 1L, updatedAlbumDto));

        verify(userValidator).validateUserExistence(anyLong());
        verify(albumMapper).toEntity(any(AlbumDto.class));
        verify(albumRepository).findByAuthorId(anyLong());
    }

    @Test
    void testValidateDeleteAlbum() {
        Album album = new Album();
        album.setAuthorId(1L);

        assertDoesNotThrow(() -> albumValidator.validateDeleteAlbum(album, 1L));

        verify(userValidator).validateUserExistence(anyLong());
    }

    @Test
    void testValidateRemoveAlbumFromFavorite() {
        Album album = new Album();
        album.setAuthorId(1L);

        when(albumRepository.checkAlbumExistsInFavorites(anyLong(), anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> albumValidator.validateRemoveAlbumFromFavorite(album, 1L));
    }

    @Test
    void testValidateRemovePostFromAlbum() {
        Album album = new Album();
        album.setAuthorId(1L);
        Post post = new Post();
        post.setId(1L);

        album.setPosts(List.of(post));

        assertThrows(DataValidationException.class, () -> albumValidator.validateRemovePostFromAlbum(album, 1L, 1L));
    }

    @Test
    void testCheckAlbumExistenceInFavorites() {
        Album album = new Album();
        album.setId(1L);

        when(albumRepository.checkAlbumExistsInFavorites(anyLong(), anyLong())).thenReturn(true);

        assertTrue(albumValidator.checkAlbumExistenceInFavorites(album, 1L));
        verify(albumRepository).checkAlbumExistsInFavorites(anyLong(), anyLong());
    }
}
