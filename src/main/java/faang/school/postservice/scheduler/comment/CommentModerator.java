package faang.school.postservice.scheduler.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.ModerationDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentModerator {

    private final ModerationDictionaryService moderationDictionaryService;
    private final ExecutorService commentModeratorExecutorService;
    private final CommentRepository commentRepository;
    @Value("${moderation.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${moderation.cron}")
    public void moderateOffensiveContent() {
        List<Comment> unverifiedComments = commentRepository.findByVerifiedIsNull();
        for (int i = 0; i < unverifiedComments.size(); i += batchSize) {
            List<Comment> batch = unverifiedComments.subList(i, Math.min(i + batchSize, unverifiedComments.size()));
            CompletableFuture.runAsync(() -> moderateBatch(batch), commentModeratorExecutorService);
        }
    }

    @Async("commentModeratorExecutorService")
    public void moderateBatch(List<Comment> batch) {
        batch.forEach(comment -> {
            comment.setVerified(!moderationDictionaryService.containsBadWord(comment.getContent()));
            comment.setVerifiedDate(LocalDateTime.now());
            commentRepository.save(comment);
            log.info("Moderated comment {}", comment.getId());
        });
    }
}