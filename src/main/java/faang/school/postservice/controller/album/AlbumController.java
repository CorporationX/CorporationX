package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/create/album/user/{userId}")
    public AlbumDto createAlbum(
            @PathVariable ("userId") long userId,
            @RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(userId, albumDto);
    }

    @PutMapping("/album/{albumId}/add/post/{postId}/user/{userId}")
    public AlbumDto addPostToAlbum(
            @PathVariable("albumId") long albumId,
            @PathVariable("postId") long postId,
            @PathVariable("userId") long userId) {
        return albumService.addPostToAlbum(albumId, postId, userId);
    }

    @PutMapping("/add/album/{albumId}/favorites/user/{userId}")
    public AlbumDto addAlbumToFavorite(
            @PathVariable("albumId") long albumId,
            @PathVariable("userId") long userId){
        return albumService.addAlbumToFavorites(albumId, userId);
    }

    @GetMapping("/get/album/user/{userId}")
    public List<AlbumDto> getUserAlbums(
            @PathVariable("userId") long userId,
            @RequestParam(required = false) AlbumFilterDto filters) {
        return albumService.getAllUserAlbums(userId, filters);
    }

    @GetMapping("/get/favorites/user/{userId}")
    public List<AlbumDto> getUserFavoriteAlbums(
            @PathVariable("userId") long userId,
            @RequestParam(required = false) AlbumFilterDto filters) {
        return albumService.getAllUserFavoriteAlbums(userId, filters);
    }

    @GetMapping("/get/all")
    public List<AlbumDto> getAllAlbums(
            @RequestParam(required = false) AlbumFilterDto filters) {
        return albumService.getAllAlbums(filters);
    }

    @GetMapping("/get/album/{albumId}")
    public AlbumDto getAlbumById(@PathVariable("albumId") long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @PutMapping("/update/album/{albumId}/user/{userId}")
    public AlbumDto updateAlbum(@PathVariable ("albumId") long albumId,
                                @PathVariable ("userId") long userId,
                                @RequestBody AlbumDto albumDto) {
        return albumService.updateAlbum(albumId, userId, albumDto);
    }

    @DeleteMapping("/delete/album/{albumId}/user/{userId}")
    public AlbumDto deleteAlbum(
            @PathVariable("albumId") long albumId,
            @PathVariable("userId") long userId) {
        return albumService.deleteAlbum(albumId, userId);
    }

    @DeleteMapping("/remove/album/{albumId}/favorite/user/{userId}")
    public AlbumDto removeAlbumFromFavorite(
            @PathVariable("albumId") long albumId,
            @PathVariable("userId") long userId){
        return albumService.removeAlbumFromFavorite(albumId, userId);
    }

    @DeleteMapping("/album/{albumId}/remove/post/{postId}/user/{userId}")
    public AlbumDto removePostFromAlbum(
            @PathVariable("albumId") long albumId,
            @PathVariable("postId") long postId,
            @PathVariable("userId") long userId){
        return albumService.removePostFromAlbum(albumId, postId, userId);
    }
}