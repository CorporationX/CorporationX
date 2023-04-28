package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        if (validateRecommendation(recommendation)) {
            return recommendationService.create(recommendation);
        }
        throw new DataValidationException("Invalid recommendation data is provided");
    }

    @PutMapping("/recommendation/{id}")
    public RecommendationDto updateRecommendation(@PathVariable long id, @RequestBody RecommendationDto updated) {
        if (validateRecommendation(updated)) {
            return recommendationService.update(updated);
        }
        throw new DataValidationException("Invalid recommendation data is provided");
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

    private boolean validateRecommendation(RecommendationDto recommendation) {
        return true;
    }
}
