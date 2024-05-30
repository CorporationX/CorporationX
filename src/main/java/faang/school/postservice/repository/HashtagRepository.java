package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    void deleteByHashtagAndPostId(String hashtag, long postId);

    List<Hashtag> findAllByHashtag(String hashtag);

    List<Hashtag> findAllByPostId(long postId);
}