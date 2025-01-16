package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entiity.Hash;
import faang.school.urlshortenerservice.entiity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query("SELECT u FROM Url u JOIN FETCH u.hash h WHERE h.value = :hashValue")
    Optional<Url> findByHashValue(String hashValue);

    @Modifying
    @Query(value = """
            DELETE FROM url 
            WHERE expires_at < :currentTime 
            RETURNING hash_value
            """, nativeQuery = true)
    List<String> deleteExpiredAndReturnHashes(LocalDateTime currentTime);

    @Query(value = """
            UPDATE url 
            SET visits_count = visits_count + 1 
            WHERE hash_value = :hashValue 
            RETURNING visits_count
            """, nativeQuery = true)
    Optional<Long> incrementVisitsCount(String hashValue);
}