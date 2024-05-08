package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RecommendationRequestFilterByUpdatedTimeTest {
    private final RecommendationRequestFilter requestFilter = new RecommendationRequestFilterByUpdatedTime();
    private final RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
    private final RecommendationRequest firstRequest = new RecommendationRequest();
    private final RecommendationRequest secondRequest = new RecommendationRequest();
    private Stream<RecommendationRequest> streamRequest;

    @BeforeEach()
    public void setUp() {
        firstRequest.setUpdatedAt(LocalDateTime.now().minusDays(4));
        secondRequest.setUpdatedAt(LocalDateTime.now().minusYears(4));
        streamRequest = Stream.of(firstRequest, secondRequest);
    }

    @Test
    void whenRequestFilterDtoIsApplicableThenReturnTrue() {
        filterDto.setUpdatedAt(LocalDateTime.now().minusDays(4));
        assertThat(requestFilter.isApplicable(filterDto)).isTrue();
    }

    @Test
    void whenRequestFilterDtoIsNotApplicableThenReturnFalse() {
        assertThat(requestFilter.isApplicable(filterDto)).isFalse();
    }

    @Test
    void whenFilterThenGetFilteredStream() {
        filterDto.setUpdatedAt(LocalDateTime.now().minusDays(4));
        List<RecommendationRequest> result = requestFilter.filter(streamRequest, filterDto).toList();
        assertThat(result).isEqualTo(List.of(firstRequest));
    }
}