package faang.school.postservice.validator.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserValidator userValidator;

    @Transactional
    public void validateCreateAlbum(long userId, Album album) {
        userValidator.validateUserExistence(userId);
        validateAlbumTitleIsUnique(userId, album);
    }

    @Transactional
    public void validateAddPostToAlbum(Album album, long postId, long userId) {
        validateUserAndAccess(album, userId);
        checkPostExistenceInAlbum(album, postId);
    }

    @Transactional
    public void validateAddAlbumToFavorites(Album album, long userId) {
        validateUserAndAccess(album, userId);
        boolean albumExists = checkAlbumExistenceInFavorites(album, userId);
        if (albumExists) {
            throw new DataValidationException(String.format("Album with id '%d' already in favorites", album.getId()));
        }
    }

    @Transactional
    public void validateUpdateAlbum(Album oldAlbum, long userId, AlbumDto updatedAlbumDto) {
                validateUserAndAccess(oldAlbum, userId);
        Album updatedAlbum = albumMapper.toEntity(updatedAlbumDto);

        if(!oldAlbum.getTitle().equals(updatedAlbum.getTitle())){
            validateAlbumTitleIsUnique(userId, updatedAlbum);
        }
    }

    @Transactional
    public void validateAlbumTitleIsUnique(long userId, Album album) {
        List<Album> albums = albumRepository.findByAuthorId(userId).toList();
        boolean isTitleNotUnique = albums.stream()
                .anyMatch(existingAlbum -> existingAlbum.getTitle().equals(album.getTitle()));

        if (isTitleNotUnique) {
            throw new DataValidationException(
                    String.format("Album with this title '%s' already exists", album.getTitle()));
        }
    }


    @Transactional
    public void validateDeleteAlbum(Album album, long userId) {
        validateUserAndAccess(album, userId);
    }

    @Transactional
    public void validateRemoveAlbumFromFavorite(Album album, long userId) {
        validateUserAndAccess(album, userId);
        boolean albumExists = checkAlbumExistenceInFavorites(album, userId);
        if (!albumExists) {
            throw new DataValidationException(String.format("Album with id '%d' already not in favorites", album.getId()));
        }
    }

    @Transactional
    public void validateRemovePostFromAlbum(Album album, long postId, long userId) {
        validateUserAndAccess(album, userId);
        checkPostExistenceInAlbum(album, postId);
    }

    @Transactional
    public void validateUserAndAccess(Album album, long userId) {
        userValidator.validateUserExistence(userId);
        validateAccess(album, userId);
    }

    private void validateAccess(Album album, long userId) {
        if (userId != album.getAuthorId()) {
            throw new NoAccessException("Only the author has access to the album");
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

    @Transactional
    public boolean checkAlbumExistenceInFavorites(Album album, long userId) {
        return albumRepository.checkAlbumExistsInFavorites(album.getId(), userId);
    }
}
