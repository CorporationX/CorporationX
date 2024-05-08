package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RecommendationRequestFilterByMessageTest {
    private final RecommendationRequestFilter requestFilter = new RecommendationRequestFilterByMessage();
    private final RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
    private final RecommendationRequest firstRequest = new RecommendationRequest();
    private final RecommendationRequest secondRequest = new RecommendationRequest();
    private Stream<RecommendationRequest> streamRequest;

    @BeforeEach()
    public void setUp() {
        firstRequest.setMessage("msg");
        secondRequest.setMessage("message");
        streamRequest = Stream.of(firstRequest, secondRequest);
    }

    @Test
    void whenRequestFilterDtoIsApplicableThenReturnTrue() {
        filterDto.setMessage("msg");
        assertThat(requestFilter.isApplicable(filterDto)).isTrue();
    }

    @Test
    void whenRequestFilterDtoIsNotApplicableThenReturnFalse() {
        assertThat(requestFilter.isApplicable(filterDto)).isFalse();
    }

    @Test
    void whenFilterThenGetFilteredStream() {
        filterDto.setMessage("msg");
        List<RecommendationRequest> result = requestFilter.filter(streamRequest, filterDto).toList();
        assertThat(result).isEqualTo(List.of(firstRequest));
    }
}