package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entiity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('url_sequence') FROM generate_series(1, :range)", nativeQuery = true)
    List<Long> getNextSequenceValues(@Param("range") int range);

    @Modifying
    @Query(value = """
                SELECT h.value
                FROM Hash h
                WHERE h.used = false
                LIMIT :limit
            """, nativeQuery = true)
    List<String> findUnusedHashes(@Param("limit") int limit);

    @Modifying
    @Query(value = """
            INSERT INTO hash (value, used)
            VALUES (:hash, false)
            ON CONFLICT (value) DO UPDATE 
            SET used = false
            """, nativeQuery = true)
    @Transactional
    void saveHash(@Param("hash") String hash);

    @Modifying
    @Query(value = """
            INSERT INTO hash (value, used)
            SELECT hash_value, false FROM (
                SELECT UNNEST(:hashes::varchar[]) AS hash_value
            ) AS h
            ON CONFLICT (value) DO UPDATE 
            SET used = false
            """, nativeQuery = true)
    @Transactional
    void saveHashBatch(@Param("hashes") List<String> hashes);

    @Query("SELECT COUNT(h) FROM Hash h WHERE h.used = false")
    long countUnusedHashes();

    Optional<Hash> findByValueAndUsedFalse(String value);
}