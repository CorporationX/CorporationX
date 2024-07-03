package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    void deleteByHashtagAndPostId(String hashtag, long postId);

    boolean existsByHashtag(String hashtag);

    @Query("""
            SELECT h FROM Hashtag h
            JOIN h.post p
            WHERE h.hashtag = :hashtag
            ORDER BY SIZE(p.likes) DESC
            """)
    Page<Hashtag> findPageByHashtagAndSortedByLikes(String hashtag, Pageable pageable);
}