package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("upload/{postId}")
    public List<ResourceDto> uploadFiles(
            @PathVariable Long postId,
            @RequestPart List<MultipartFile> files
    ) {
        return resourceService.create(postId, files);
    }

    @GetMapping("download/{key}")
    public InputStream downloadFile(@PathVariable String key) {
        return resourceService.downloadResource(key);
    }

    @DeleteMapping("{key}")
    public void deleteFile(@PathVariable String key) {
        resourceService.deleteFile(key);
    }
}
