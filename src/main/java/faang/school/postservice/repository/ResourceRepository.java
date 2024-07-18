package faang.school.postservice.repository;

import faang.school.postservice.entity.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    boolean existsByKey(String key);

    Resource findByKey(String key);

    int countAllByPost_Id(Long postId);

    void deleteByKey(String key);
}
