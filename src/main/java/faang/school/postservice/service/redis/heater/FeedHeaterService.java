package faang.school.postservice.service.redis.heater;

import faang.school.postservice.event.heat.HeatUsersFeedEvent;

public interface FeedHeaterService {

    void feedHeat();

    void generateUsersFeed(HeatUsersFeedEvent event);
}
