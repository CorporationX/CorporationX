package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Optional;

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {

    Optional<RecommendationRequest> findByRequesterIdAndReceiverId(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
                INSERT INTO recommendation_request (requester_id, receiver_id, message, status, created_at, updated_at)
                VALUES (:requesterId, :receiverId, :message, 0, now(), now())
            """)
    @Modifying
    RecommendationRequest create(long requesterId, long receiverId, String message);
}
