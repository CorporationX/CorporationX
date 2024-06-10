package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Optional;

@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship_request (requester_id, receiver_id, description, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW())
            """)
    MentorshipRequest create(long requesterId, long receiverId, String description);

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship_request
            WHERE requester_id = :requesterId AND receiver_id = :receiverId
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<MentorshipRequest> findLatestRequest(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
           SELECT EXISTS(SELECT 1 FROM mentorship_request 
           WHERE requester_id = :requesterId 
           AND receiver_id = :receiverId AND status = 1) 
           """)
    boolean existAcceptedRequest(long requesterId, long receiverId);
}