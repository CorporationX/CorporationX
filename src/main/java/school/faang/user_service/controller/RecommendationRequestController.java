package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/recommendation-request")
@Tag(name = "Recommendation", description = "Операции с рекомендациями")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/")
    @Operation(summary = "Запросить рекомендацию", description = "Отправляет запрос на получение рекомендации")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.requestRecommendation(recommendationRequest);
    }

    @GetMapping("/getListOfRecommendationRequests")
    @Operation(summary = "Получить список запросов на рекомендацию", description = "Возвращает список всех запросов на рекомендацию с возможностью фильтрации")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestParam RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequestService.getRecommendationRequests(requestFilterDto);
    }

    @GetMapping("/getRecommendationRequests/{id}")
    @Operation(summary = "Получить запрос на рекомендацию по ID", description = "Возвращает конкретный запрос на рекомендацию по указанному ID")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/rejectRequest/{id}/reject")
    @Operation(summary = "Отклонить запрос на рекомендацию", description = "Отклоняет запрос на рекомендацию с указанием причины")
    public RecommendationRequestDto rejectRequest(@PathVariable long id,@RequestBody RejectionDto rejection) {
        checkRejectionDto(rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void checkRejectionDto(RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isBlank()) {
            throw new DataValidationException("rejection should contain the reason");
        }
    }
}