package faang.school.postservice.validator.resource;

public interface ResourceValidator {

    void validateCountFilesPerPost(Long postId, int filesToAdd);

    void validateExistenceByKey(String key);
}
