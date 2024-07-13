package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostHashtagDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.event.NewPostEvent;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationStatus;
import faang.school.postservice.publisher.NewPostPublisher;
import faang.school.postservice.publisher.PostViewPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.async.AsyncHashtagService;
import faang.school.postservice.service.spelling.SpellingService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final AsyncHashtagService asyncHashtagService;
    private final ModerationDictionary moderationDictionary;
    private final SpellingService spellingService;
    private final PostViewPublisher postViewPublisher;
    private final NewPostPublisher newPostPublisher;
    private final UserContext userContext;

    @Override
    public Post findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Post with id %s not found", id)));
        publishPostViewEvent(post);
        return post;
    }

    @Override
    @Transactional
    public PostDto create(PostCreateDto postCreateDto) {
        postValidator.validateAuthor(postCreateDto.getAuthorId(), postCreateDto.getProjectId());
        postValidator.validatePostContent(postCreateDto.getContent());
        Post post = postRepository.save(postMapper.toEntity(postCreateDto));
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto publish(Long id) {
        Post post = findById(id);
        postValidator.validatePublicationPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);

        PostHashtagDto postHashtagDto = postMapper.toHashtagDto(post);
        asyncHashtagService.addHashtags(postHashtagDto);

        publishNewPostEvent(post);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto update(Long id, PostUpdateDto postUpdateDto) {
        Post post = findById(id);
        postValidator.validatePostContent(post.getContent());
        post.setContent(postUpdateDto.getContent());
        post = postRepository.save(post);

        PostHashtagDto postHashtagDto = postMapper.toHashtagDto(post);
        asyncHashtagService.updateHashtags(postHashtagDto);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Post post = findById(id);
        post.setDeleted(true);
        postRepository.save(post);

        PostHashtagDto postHashtagDto = postMapper.toHashtagDto(post);
        asyncHashtagService.removeHashtags(postHashtagDto);
    }

    @Override
    public List<PostDto> findAllByHashtag(String hashtag, Pageable pageable) {
        return asyncHashtagService.getPostsByHashtag(hashtag, pageable).join().stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> findPostDraftsByUserAuthorId(Long id) {
        return postRepository.findByAuthorIdAndPublishedAndDeletedWithLikes(id, false, false).stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<PostDto> findPostDraftsByProjectAuthorId(Long id) {
        return postRepository.findByProjectIdAndPublishedAndDeletedWithLikes(id, false, false).stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<PostDto> findPostPublicationsByUserAuthorId(Long id) {
        return postRepository.findByAuthorIdAndPublishedAndDeletedWithLikes(id, true, false).stream()
                .peek(this::publishPostViewEvent)
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .toList();
    }

    @Override
    public List<PostDto> findPostPublicationsByProjectAuthorId(Long id) {
        return postRepository.findByProjectIdAndPublishedAndDeletedWithLikes(id, true, false).stream()
                .peek(this::publishPostViewEvent)
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .toList();
    }

    @Override
    @Async("executorService")
    public void verifyPost(List<Post> posts) {
        for (Post post : posts) {

            if (moderationDictionary.checkCurseWordsInPost(post.getContent())) {
                post.setIsVerify(VerificationStatus.NOT_VERIFIED);
            } else {
                post.setIsVerify(VerificationStatus.VERIFIED);
            }

            post.setVerifiedDate(LocalDateTime.now());
            postRepository.save(post);
        }
    }

    @Override
    public void correctPosts(){
        List<Post> unpublishedPosts = postRepository.findReadyToPublish();
        Map<Post, CompletableFuture<Optional<String>>> correctedContents = new HashMap<>();
        unpublishedPosts.stream()
                .filter(post -> !post.isCheckedForSpelling())
                .forEach(post->correctedContents.put(post, spellingService.checkSpelling(post.getContent())));

        correctedContents.forEach((post, correctedContent)->{
            try {
                correctedContent.get().ifPresent((content) -> {
                    post.setContent(content);
                    post.setCheckedForSpelling(true);
                });
            }
            catch (InterruptedException | ExecutionException e) {
                log.error("Error when updating a post with an id: {}", post.getId(), e);
                throw new RuntimeException(e);
            }
        });
    }

    private void publishPostViewEvent(Post post) {
        PostViewEvent event = new PostViewEvent(post.getId(), post.getAuthorId(), userContext.getUserId(), LocalDateTime.now());
        postViewPublisher.publish(event);
    }

    private void publishNewPostEvent(Post post) {
        NewPostEvent event = new NewPostEvent(post.getId(), post.getAuthorId(), LocalDateTime.now());
        newPostPublisher.publish(event);
    }
}
