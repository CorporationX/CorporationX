package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/album")
    private AlbumDto createAlbum(@RequestBody @Valid AlbumDto album) {
        return albumService.createAlbum(album);
    }

    @PutMapping("/album/{albumId}")
    private AlbumDto updateAlbum(@PathVariable long albumId, @RequestBody @Valid AlbumDto album) {
        return albumService.updateAlbum(albumId, album);
    }

    @PostMapping("/user/{userId}/album")
    private List<AlbumDto> getUserAlbums(@PathVariable long userId, @RequestBody AlbumFilterDto filter) {
        return albumService.getUserAlbums(userId, filter);
    }

    @PostMapping("/album/list")
    private List<AlbumDto> getAlbums(@RequestBody AlbumFilterDto filter) {
        return albumService.getAlbums(filter);
    }

    @GetMapping("/album/{albumId}")
    private AlbumDto getAlbum(@PathVariable long albumId) {
        return albumService.getAlbum(albumId);
    }

    @DeleteMapping("/album/{albumId}")
    private void deleteAlbum(@PathVariable long albumId) {
        albumService.deleteAlbum(albumId);
    }

    @PostMapping("/album/{albumId}/post/{postId}")
    private AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return albumService.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/album/{albumId}/post/{postId}")
    private AlbumDto removePostFromAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return albumService.removePostFromAlbum(albumId, postId);
    }

    @PostMapping("/user/{userId}/album/{albumId}/favorite")
    private AlbumDto addAlbumToFavorites(@PathVariable long userId, @PathVariable long albumId) {
        return albumService.addAlbumToFavorites(userId, albumId);
    }

    @DeleteMapping("/user/{userId}/album/{albumId}/favorite")
    private AlbumDto removeAlbumFromFavorites(@PathVariable long userId, @PathVariable long albumId) {
        return albumService.removeAlbumFromFavorites(userId, albumId);
    }

    @PostMapping("/user/{userId}/album/favorite")
    public List<AlbumDto> getUserFavoriteAlbums(@PathVariable long userId, @RequestBody AlbumFilterDto filter) {
        return albumService.getUserFavoriteAlbums(userId, filter);
    }
}
