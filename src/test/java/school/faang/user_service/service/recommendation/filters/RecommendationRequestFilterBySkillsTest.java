package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RecommendationRequestFilterBySkillsTest {
    private static final long ID_OF_FIRST_SKILL = 1L;
    private static final long ID_OF_SECOND_SKILL = 2L;
    private final RecommendationRequestFilter requestFilter = new RecommendationRequestFilterBySkills();
    private final RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto();
    private final RecommendationRequest firstRequest = new RecommendationRequest();
    private final RecommendationRequest secondRequest = new RecommendationRequest();
    private Stream<RecommendationRequest> streamRequest;

    @BeforeEach()
    public void setUp() {
        SkillRequest firstSkillReq = new SkillRequest();
        Skill firstSkill = new Skill();
        firstSkill.setId(ID_OF_FIRST_SKILL);
        firstSkillReq.setSkill(firstSkill);
        SkillRequest secondSkillReq = new SkillRequest();
        Skill secondSkill = new Skill();
        secondSkill.setId(ID_OF_SECOND_SKILL);
        secondSkillReq.setSkill(secondSkill);
        firstRequest.setSkills(List.of(firstSkillReq));
        secondRequest.setSkills(List.of(secondSkillReq));
        streamRequest = Stream.of(firstRequest, secondRequest);
    }

    @Test
    void whenRequestFilterDtoIsApplicableThenReturnTrue() {
        filterDto.setSkillIds(List.of(ID_OF_FIRST_SKILL));
        assertThat(requestFilter.isApplicable(filterDto)).isTrue();
    }

    @Test
    void whenRequestFilterDtoIsNotApplicableThenReturnFalse() {
        assertThat(requestFilter.isApplicable(filterDto)).isFalse();
    }

    @Test
    void whenFilterThenGetFilteredStream() {
        filterDto.setSkillIds(List.of(ID_OF_FIRST_SKILL));
        List<RecommendationRequest> result = requestFilter.filter(streamRequest, filterDto).toList();
        assertThat(result).isEqualTo(List.of(firstRequest));
    }
}