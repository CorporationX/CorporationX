package school.faang.user_service.service.recommendation.filters;

import java.util.List;

public class RecommendationRequestFilterStorage {
    public static List<RecommendationRequestFilter> getStorage() {
        return List.of(new RecommendationRequestFilterByMessage(), new RecommendationRequestFilterByRequester(),
                new RecommendationRequestFilterByReceiver(), new RecommendationRequestFilterByCreatedTime(),
                new RecommendationRequestFilterBySkills(), new RecommendationRequestFilterByUpdatedTime(),
                new RecommendationRequestFilterByRequestStatus());
    }
}