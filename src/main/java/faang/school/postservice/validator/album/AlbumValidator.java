package faang.school.postservice.validator.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.zip.DataFormatException;

import static java.util.Locale.filter;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Transactional
    public void validateCreateAlbum(long userId, Album album) {

        validateAlbumTittleIsUnique(userId, album);
        validateUserExist(userId);
    }

    @Transactional
    public void validateAddPostToAlbum(Album album, long post, long userId) {

        validateAccess(album, userId);
        checkPostExistenceInAlbum(album, post);
    }

    @Transactional
    public void validateAddAlbumToFavorites(Album album, long userId) {

        validateAccess(album, userId);
        checkAlbumExistenceInFavorites(album, userId);
    }

    @Transactional
    public void validateUpdateAlbum(Album album, long userId, AlbumDto albumDto) {

        validateAccess(album, userId);
        validateUserDontChangedAlbumAuthor(album, userId, albumDto);
        validateUpdatedAlbumDiffersByLast(album, albumDto);

    }

    @Transactional
    public void validateDeleteAlbum(Album album, long userId) {

        validateAccess(album, userId);
    }

    @Transactional
    public void validateRemoveAlbumFromFavorite(Album album, long userId) {

        validateAccess(album, userId);
        checkAlbumExistenceInFavorites(album, userId);
    }

    @Transactional
    public void validateRemovePostFromAlbum(Album album, long postId, long userId) {

        validateAccess(album, userId);
        checkPostExistenceInAlbum(album, postId);
    }

    private void validateAccess(Album album, long userId) {
        if (userId != album.getAuthorId()){
            throw new NoAccessException("Only author have access to the album");
        }
    }

    private void validateUserExist(long userId) {
    }

    private void validateAlbumTittleIsUnique(long userId, Album album) {
        List<Album> albums = albumRepository.findByAuthorId(userId).toList();
        boolean isTitleNotUnique = albums.stream()
                .anyMatch(existingAlbum -> existingAlbum.getTitle().equals(album.getTitle()));

        if (isTitleNotUnique) {
            throw new DataValidationException(
                    String.format("Album with this title '%s' already exists", album.getTitle()));
        }
    }

    private void checkPostExistenceInAlbum(Album album, long postId) {
        List<Post> posts = album.getPosts();

        boolean isPostAlreadyExistInAlbum = posts.stream()
                .anyMatch(post -> post.getId() == postId);

        if (isPostAlreadyExistInAlbum) {
            throw new DataValidationException(
                    String.format("Post with id '%d' already exists in album", postId));
        }
    }

    private void checkAlbumExistenceInFavorites(Album album, long userId) {
    }

    private void validateUpdatedAlbumDiffersByLast(Album oldAlbum, AlbumDto updatedAlbumDto) {
        Album updatedAlbum = albumMapper.toEntity(updatedAlbumDto);

        if (oldAlbum.equals(updatedAlbum)) {
            throw new DataValidationException("No changes detected in the provided update information");
        }
    }

    private void validateUserDontChangedAlbumAuthor(Album album, long userId, AlbumDto albumDto) {
    }

    // Как проверить что альбом в избранных
    // Как проверить что пользователь существует в системе

    // Используйте UserServiceClient для отправки запросов в user_service
    // на проверку присутствия пользователя в системе. Используйте Feign Client из Spring.
    // Аннотация @FeignClient. Разберитесь, что возвращает метод этого бина,
    // когда пользователь есть в системе и когда его нет.
    // Здесь необходимо провести эксперимент и выяснить.
}
