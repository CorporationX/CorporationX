package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("resources")
@RequiredArgsConstructor
@Tag(name = "Resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "Save resource")
    @PostMapping("upload/{postId}")
    public List<ResourceDto> uploadFiles(
            @PathVariable Long postId,
            @RequestPart List<MultipartFile> files
    ) {
        return resourceService.create(postId, files);
    }

    @Operation(summary = "Download resource")
    @GetMapping("download/{key}")
    public InputStream downloadFile(@PathVariable String key) {
        return resourceService.downloadResource(key);
    }

    @Operation(summary = "Delete resource")
    @DeleteMapping("{key}")
    public void deleteFile(@PathVariable String key) {
        resourceService.deleteFile(key);
    }
}
