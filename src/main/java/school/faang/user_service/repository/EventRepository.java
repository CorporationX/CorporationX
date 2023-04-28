package school.faang.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO event (title, start_date, owner_id, description, end_date, location, max_attendees)
            VALUES (:title, :startDate, :ownerId, :description, :endDate, :location, :maxAttendees)
            """)
    @Modifying
    Event create(String title, LocalDateTime startDate, long ownerId,
                 String description, LocalDateTime endDate, String location, int maxAttendees);

    @Query(nativeQuery = true, value = """
            UPDATE event SET title = :title, start_date = :startDate,
                             owner_id = :ownerId, description =  :description,
                             end_date = :endDate, location = :location,
                             max_attendees = :maxAttendees
            WHERE id = :id
            """)
    @Modifying
    Event update(long id, String title, LocalDateTime startDate, long ownerId,
                 String description, LocalDateTime endDate, String location, int maxAttendees);

    Page<Event> findAllByUserId(long userId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            JOIN user_event ue ON ue.event_id = e.id
            WHERE ue.user_id = :userId
            """, countQuery = """
            SELECT COUNT(e.id) FROM event e
            JOIN user_event ue ON ue.event_id = e.id
            WHERE ue.user_id = :userId
            GROUP BY e.id
            """)
    Page<Event> findParticipatedEventsByUserId(long userId, Pageable pageable);

    @Query(nativeQuery = true, value = "INSERT INTO event_skill (skill_id, event_id) VALUES (:skillId, :eventId)")
    @Modifying
    void addSkillsToEvent(long skillId, long eventId);
}