package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RecommendationRequestFilterByRequestStatusTest {
    private final RecommendationRequestFilter requestFilter = new RecommendationRequestFilterByRequestStatus();
    private final RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
    private final RecommendationRequest firstRequest = new RecommendationRequest();
    private final RecommendationRequest secondRequest = new RecommendationRequest();
    private Stream<RecommendationRequest> streamRequest;

    @BeforeEach()
    public void setUp() {
        firstRequest.setStatus(RequestStatus.ACCEPTED);
        secondRequest.setStatus(RequestStatus.PENDING);
        streamRequest = Stream.of(firstRequest, secondRequest);
    }

    @Test
    void whenRequestFilterDtoIsApplicableThenReturnTrue() {
        filterDto.setStatus(RequestStatus.ACCEPTED);
        assertThat(requestFilter.isApplicable(filterDto)).isTrue();
    }

    @Test
    void whenRequestFilterDtoIsNotApplicableThenReturnFalse() {
        assertThat(requestFilter.isApplicable(filterDto)).isFalse();
    }

    @Test
    void whenFilterThenGetFilteredStream() {
        filterDto.setStatus(RequestStatus.ACCEPTED);
        List<RecommendationRequest> result = requestFilter.filter(streamRequest, filterDto).toList();
        assertThat(result).isEqualTo(List.of(firstRequest));
    }
}