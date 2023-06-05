package faang.school.postservice.scheduler;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;

    @Scheduled(cron = "${post.ad-remover.scheduler.cron}")
    public void removeExpiredAds() {
        adService.removeExpiredAds();
    }
}
