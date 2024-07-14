package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisFeed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisFeedRepository extends CrudRepository<RedisFeed, Long> {

}