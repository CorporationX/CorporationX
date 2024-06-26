package faang.school.postservice.config.scheduler.comment;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentService commentService;

    @Scheduled(cron = "${moderation.cron}")
    @Transactional
    public void moderateOffensiveContent() {
        commentService.moderateOffensiveContent();
    }
}