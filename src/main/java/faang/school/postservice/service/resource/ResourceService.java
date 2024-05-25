package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ResourceService {

    Resource findById(Long id);

    List<ResourceDto> create(Long postId, List<MultipartFile> files);

    InputStream downloadResource(String key);

    void deleteFile(String key);
}
