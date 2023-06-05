package faang.school.postservice.controller.ad;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.ad.AdDto;
import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;
    private final UserContext userContext;

    @PostMapping("/post/{postId}/ad")
    public AdDto buyAd(@PathVariable long postId,
                       @RequestParam(name = "days") int days,
                       @RequestParam(name = "appearances") int appearances) {
        return adService.buyAd(userContext.getUserId(), postId, days, appearances);
    }

    @GetMapping("/post/ad")
    public List<AdDto> getRemainingAds() {
        return adService.getRemainingAds(userContext.getUserId());
    }
}
