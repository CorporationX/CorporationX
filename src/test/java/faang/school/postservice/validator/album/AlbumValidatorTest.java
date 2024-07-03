package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AlbumValidator albumValidator;



    @Test
    void testValidateAlbumExistence() {
        Album album = new Album();
        album.setAuthorId(1L);

        when(albumRepository.checkAlbumExistsInFavorites(anyLong(), anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> albumValidator.validateAlbumExistence(album, 1L));
    }

    @Test
    void testValidateAlbumTitleIsUnique() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("test");

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));

        assertThrows(DataValidationException.class, () -> albumValidator.validateAlbumTitleIsUnique(1, "test"));
    }

    @Test
    void testValidateAccessOnlyAuthor() {
        Album album = new Album();
        album.setId(1L);
        album.setAuthorId(2L);
        album.setVisibility(AlbumVisibility.ONLY_AUTHOR);

        assertThrows(NoAccessException.class, () -> albumValidator.validateAccess(album, 3L));
    }

    @Test
    void testValidateAccessSelectedUsers() {
        Album album = new Album();
        album.setId(1L);
        album.setAuthorId(2L);
        album.setVisibility(AlbumVisibility.SELECTED_USERS);
        album.setSelectedUserIds(List.of(3L));

        assertThrows(NoAccessException.class, () -> albumValidator.validateAccess(album, 4L));
    }

    @Test
    void testValidateAccessOnlySubscribers() {
        Album album = new Album();
        album.setId(1L);
        album.setAuthorId(2L);
        album.setVisibility(AlbumVisibility.ONLY_SUBSCRIBERS);

        when(userServiceClient.getFollowers(2L)).thenReturn(List.of(new UserDto(3L, "", "")));

        assertThrows(NoAccessException.class, () -> albumValidator.validateAccess(album, 4L));
    }

    @Test
    void testCheckPostExistenceInAlbum() {
        Album album = new Album();
        album.setId(1L);
        album.setPosts(List.of(
                Post.builder().id(1L).build(),
                Post.builder().id(2L).build(),
                Post.builder().id(3L).build()
        ));

        assertThrows(DataValidationException.class, () -> albumValidator.checkPostExistenceInAlbum(album, 1L));
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
