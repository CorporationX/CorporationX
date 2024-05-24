package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @Operation(summary = "Create a new album")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @PostMapping("/new/album")
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        long userId = userContext.getUserId();
        return albumService.createAlbum(userId, albumDto);
    }

    @Operation(summary = "Add a post to an album")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post added to album",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @PutMapping("/album/{albumId}/add/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto addPostToAlbum(@PathVariable("albumId") long albumId,
                                   @PathVariable("postId") long postId) {
        long userId = userContext.getUserId();
        return albumService.addPostToAlbum(albumId, postId, userId);
    }

    @Operation(summary = "Add an album to favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album added to favorites",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @PutMapping("/add/album/{albumId}/favorites")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto addAlbumToFavorite(@PathVariable("albumId") long albumId) {
        long userId = userContext.getUserId();
        return albumService.addAlbumToFavorites(albumId, userId);
    }

    @Operation(summary = "Get all user albums with filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user albums",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @GetMapping("/album/user")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getUserAlbums(
            @ParameterObject @RequestBody(required = false) AlbumFilterDto filter) {

        long userId = userContext.getUserId();
        return albumService.getAllUserAlbums(userId, filter);
    }

    @Operation(summary = "Get all user favorite albums")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user favorite albums",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @GetMapping("/favorites")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getUserFavoriteAlbums(
            @ParameterObject @RequestBody(required = false) AlbumFilterDto filter) {

        long userId = userContext.getUserId();
        return albumService.getAllUserFavoriteAlbums(userId, filter);
    }

    @Operation(summary = "Get all albums")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all albums",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDto> getAllAlbums(
            @ParameterObject @RequestBody(required = false) AlbumFilterDto filter) {

        long userId  = userContext.getUserId();
        return albumService.getAllAlbums(userId, filter);
    }

    @Operation(summary = "Get album by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album details",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @GetMapping("/album/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto getAlbumById(@PathVariable("albumId") long albumId) {

        long userId  = userContext.getUserId();
        return albumService.getAlbumById(userId, albumId);
    }

    @Operation(summary = "Update an album")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @PutMapping("/album/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto updateAlbum(@PathVariable("albumId") long albumId,
                                @RequestBody AlbumDto albumDto) {
        long userId = userContext.getUserId();
        return albumService.updateAlbum(albumId, userId, albumDto);
    }

    @Operation(summary = "Delete an album")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @DeleteMapping("/album/{albumId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto deleteAlbum(@PathVariable("albumId") long albumId) {
        long userId = userContext.getUserId();
        return albumService.deleteAlbum(albumId, userId);
    }

    @Operation(summary = "Remove an album from favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album removed from favorites",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @DeleteMapping("/album/{albumId}/favorites")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto removeAlbumFromFavorite(@PathVariable("albumId") long albumId) {
        long userId = userContext.getUserId();
        return albumService.removeAlbumFromFavorite(albumId, userId);
    }

    @Operation(summary = "Remove a post from an album")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post removed from album",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))})
    })
    @DeleteMapping("/album/{albumId}/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto removePostFromAlbum(@PathVariable("albumId") long albumId,
                                        @PathVariable("postId") long postId) {
        long userId = userContext.getUserId();
        return albumService.removePostFromAlbum(albumId, postId, userId);
    }
}
