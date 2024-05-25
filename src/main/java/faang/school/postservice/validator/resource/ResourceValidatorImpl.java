package faang.school.postservice.validator.resource;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.property.AmazonS3Properties;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceValidatorImpl implements ResourceValidator {

    private final ResourceRepository resourceRepository;
    private final AmazonS3Properties amazonS3Properties;


    @Override
    @Transactional
    public void validateCountFilesPerPost(Long postId, int filesToAdd) {
        if (resourceRepository.countAllByPost_Id(postId) + filesToAdd > amazonS3Properties.getMaxFilesAmount()) {
            throw new DataValidationException(String.format("Max files per post = %s", amazonS3Properties.getMaxFilesAmount()));
        }
    }

    @Override
    @Transactional
    public void validateExistenceByKey(String key) {
        if (!resourceRepository.existsByKey(key)) {
            throw new NotFoundException(String.format("Resource with key $s not found", key));
        }
    }
}
