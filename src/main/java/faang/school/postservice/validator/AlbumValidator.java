package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateCreation(@Valid AlbumDto album) {
        if (albumRepository.existsByTitleAndAuthorId(album.getTitle(), album.getAuthorId())) {
            throw new DataValidationException("Album with title " + album.getTitle() + " already exists");
        }
        try {
            userServiceClient.getUser(album.getAuthorId());
        } catch (FeignException e) {
            throw new DataValidationException("User with id " + album.getAuthorId() + " does not exist");
        }
        validateSpecificWatchers(album);

    }

    public void validateUpdate(Album entity, @Valid AlbumDto dto) {
        if (!entity.getTitle().equals(dto.getTitle()) && albumRepository.existsByTitleAndAuthorId(dto.getTitle(), dto.getAuthorId())) {
            throw new DataValidationException("Album with title " + dto.getTitle() + " already exists");
        }
        if (entity.getAuthorId() != dto.getAuthorId()) {
            throw new DataValidationException("Album author cannot be changed");
        }
        if (entity.getVisibility() != dto.getVisibility()) {
            validateSpecificWatchers(dto);
        }
    }

    private void validateSpecificWatchers(AlbumDto album) {
        if (album.getVisibility() == Visibility.SPECIFIC_USERS && album.getWatchers() != null && !album.getWatchers().isEmpty()) {
            try {
                List<UserDto> watchers = userServiceClient.getUsersByIds(album.getWatchers());
                if (watchers.size() != album.getWatchers().size()) {
                    throw new DataValidationException("Some of the users do not exist");
                }
            } catch (FeignException e) {
                throw new DataValidationException("Some of the users do not exist");
            }
        }
    }
}
