package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateAlbumExistence(Album album, long userId) {
        boolean albumExists = checkAlbumExistenceInFavorites(album, userId);
        if (albumExists) {
            throw new DataValidationException(String.format("Album with id '%d' already in favorites", album.getId()));
        }
    }

    public void validateAlbumTitleIsUnique(long userId, String title) {
        List<Album> albums = albumRepository.findByAuthorId(userId).toList();
        boolean isTitleNotUnique = albums.stream()
                .anyMatch(existingAlbum -> existingAlbum.getTitle().equals(title));

        if (isTitleNotUnique) {
            throw new DataValidationException(
                    String.format("Album with this title '%s' already exists", title));
        }
    }

    public boolean validateAccess(Album album, long userId) {
        if (album.getVisibility() == AlbumVisibility.ONLY_AUTHOR && userId != album.getAuthorId()) {
            throw new NoAccessException("Only the author has access to the album with albumId = " + album.getId());
        }
        if (album.getVisibility() == AlbumVisibility.SELECTED_USERS && !album.getSelectedUserIds().contains(userId)) {
            throw new NoAccessException("Only author's selected users has access to the album with albumId = " + album.getId());
        }
        if (album.getVisibility() == AlbumVisibility.ONLY_SUBSCRIBERS) {
            List<Long> authorSubscribersIds = userServiceClient.getFollowers(album.getAuthorId()).stream()
                    .map(UserDto::getId)
                    .toList();
            if (!authorSubscribersIds.contains(userId)) {
                throw new NoAccessException("Only subscribers has access to the album with albumId = " + album.getId());
            }
        }
        return true;
    }

    public void checkPostExistenceInAlbum(Album album, long postId) {
        List<Post> posts = album.getPosts();
        boolean isPostAlreadyExistInAlbum = posts.stream()
                .anyMatch(post -> post.getId() == postId);

        if (isPostAlreadyExistInAlbum) {
            throw new DataValidationException(
                    String.format("Post with id '%d' already exists in album", postId));
        }
    }

    public boolean checkAlbumExistenceInFavorites(Album album, long userId) {
        return albumRepository.checkAlbumExistsInFavorites(album.getId(), userId);
    }

    public void validateUserIsAuthor(Album album, long userId) {
        if (album.getAuthorId() != userId) {
            throw new NoAccessException("Only the author can modify album with albumId = " + album.getId());
        }
    }
}
