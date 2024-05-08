package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RecommendationRequestFilterByRequesterTest {
    private final RecommendationRequestFilter requestFilter = new RecommendationRequestFilterByRequester();
    private final RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
    private final RecommendationRequest firstRequest = new RecommendationRequest();
    private final RecommendationRequest secondRequest = new RecommendationRequest();
    private Stream<RecommendationRequest> streamRequest;

    @BeforeEach()
    public void setUp() {
        User firstRequester = new User();
        firstRequester.setId(1L);
        User secondRequester = new User();
        secondRequester.setId(2L);
        firstRequest.setRequester(firstRequester);
        secondRequest.setRequester(secondRequester);
        streamRequest = Stream.of(firstRequest, secondRequest);
    }

    @Test
    void whenRequestFilterDtoIsApplicableThenReturnTrue() {
        User firstRequester = new User();
        firstRequester.setId(1L);
        filterDto.setRequesterId(firstRequest.getRequester().getId());
        assertThat(requestFilter.isApplicable(filterDto)).isTrue();
    }

    @Test
    void whenRequestFilterDtoIsNotApplicableThenReturnFalse() {
        assertThat(requestFilter.isApplicable(filterDto)).isFalse();
    }

    @Test
    void whenFilterThenGetFilteredStream() {
        User firstRequester = new User();
        firstRequester.setId(1L);
        filterDto.setRequesterId(firstRequest.getRequester().getId());
        List<RecommendationRequest> result = requestFilter.filter(streamRequest, filterDto).toList();
        assertThat(result).isEqualTo(List.of(firstRequest));
    }
}