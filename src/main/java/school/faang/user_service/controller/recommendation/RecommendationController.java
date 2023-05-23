package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody @Validated RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping("/recommendation/{id}")
    public RecommendationDto updateRecommendation(@PathVariable long id, @RequestBody @Validated RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    @DeleteMapping("/recommendation/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/recommendation/received/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId, @RequestParam int page,
                                                             @RequestParam int pageSize) {
        return recommendationService.getAllUserRecommendations(receiverId, page, pageSize);
    }

    @GetMapping("/recommendation/given/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId, @RequestParam int page,
                                                             @RequestParam int pageSize) {
        return recommendationService.getAllGivenRecommendations(authorId, page, pageSize);
    }
}